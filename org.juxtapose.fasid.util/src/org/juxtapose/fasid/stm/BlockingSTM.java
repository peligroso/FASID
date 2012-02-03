package org.juxtapose.fasid.stm;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

import com.trifork.clj_ds.IPersistentMap;

/**
 * @author Pontus Jörgne
 * Jan 6, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 * STM implementation that uses locking as synchronization method around transactions
 */
public class BlockingSTM extends STM
{
	//Fair locking implies that first come, first serve. Fair locking = false may lead to unwanted behavior on highly contended data 
	public final boolean FAIR_LOCKING = true; 
	private final ConcurrentHashMap<String, ReentrantLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantLock>();
	
	/**
	 * @param inKey
	 */
	private void lock( String inKey )
	{
		boolean set = false;
		
		do
		{
			ReentrantLock lock = m_keyToLock.get( inKey );
			if( lock != null )
			{
				lock.lock();
				set = m_keyToLock.replace( inKey, lock, lock );
				if( ! set )
					lock.unlock();
			}
			else
			{
				lock = new ReentrantLock( FAIR_LOCKING );
				lock.lock();
				set = null == m_keyToLock.putIfAbsent( inKey, lock );
			}
		}while( !set );
	}
	
	/**
	 * @param inKey
	 */
	private void unlock( String inKey )
	{
		ReentrantLock lock = m_keyToLock.get( inKey );
		if( lock != null )
		{
			lock.unlock();
		}
		else
		{
			logError("Tried to unlock already disposed lock");
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.impl.STM#commit(org.juxtapose.fasid.stm.impl.Transaction)
	 */
	public void commit( DataTransaction inTransaction )
	{	
		String dataKey = inTransaction.getDataKey();
		
		IPublishedData newData = null;
		
		ReferenceLink[] addedLinks = null;
		ReferenceLink[] removedLinks = null;
		
		lock( dataKey );
		
		try
		{
			IPublishedData existingData = keyToData.get( dataKey );
			if( existingData == null )
			{
				//data has been removed due to lack of interest, transaction is discarded
				return;
			}

			if( !STMUtil.validateProducerToData(existingData, inTransaction) )
			{
				logError( "Wrong version DataProducer tried to update data: "+dataKey );
				//The producer for this data is of the wrong version, Transaction is discarded
				return;
			}

			inTransaction.putInitDataState( existingData.getDataMap(), existingData.getStatus() );
			inTransaction.execute();
			if( inTransaction.isDisposed() )
			{
				return;
			}
			IPersistentMap<Integer, DataType<?>> inst = inTransaction.getStateInstruction();
			Map<Integer, DataType<?>> delta = inTransaction.getDeltaState();

			newData = existingData.setUpdatedData( inst, delta, inTransaction.getStatus() );
			
			keyToData.put( dataKey, newData );
			
			//Init reference links
			Map< Integer, DataTypeRef > dataReferences = inTransaction.getAddedReferences();
			addedLinks = new ReferenceLink[ dataReferences.size() ];
			
			if( !dataReferences.isEmpty() )
			{
				IDataProducer producer = newData.getProducer();
				if( producer == null )
					logError( "Tried to add reference to data with null producer" );
				else
				{
					int i = 0;
					for( Integer fieldKey : dataReferences.keySet() )
					{
						DataTypeRef ref = dataReferences.get( fieldKey );
						ReferenceLink refLink = new ReferenceLink( producer, this, fieldKey, ref );
						addedLinks[i] = refLink;
						i++;
					}
				}
			}


			//Dispose reference links
			List< Integer > removedReferences = inTransaction.getRemovedReferences();
			removedLinks = new ReferenceLink[ removedReferences.size() ];
			
			if( !removedReferences.isEmpty() )
			{
				IDataProducer producer = newData.getProducer();
				if( producer == null )
					logError( "Tried to remove reference from data with null producer" );
				else
				{
					int i = 0;
					for( Integer fieldKey : dataReferences.keySet() )
					{
						ReferenceLink refLink = producer.removeReferenceLink( fieldKey );
						if( refLink == null )
						{
							logError( "Tried to remove reference Link that is not stored in the producer" );
						}
						removedLinks[i] = refLink;
						i++;
					}
				}
			}
			
		}catch( Throwable t )
		{
			logError( t.getMessage() );
		}
		finally
		{
			unlock( dataKey );
		}
		
		
		if(dataKey.contains( "PRICE" ) )
		{
			try
			{
				Thread.sleep( new Random().nextInt( 500 ) );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if( newData != null )
		{
			newData.updateSubscribers( dataKey );
		}
		
		for( ReferenceLink link : addedLinks )
		{
			link.init();
		}
		
		for( ReferenceLink link : removedLinks )
		{
			link.dispose();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.impl.STM#subscribe(org.juxtapose.fasid.util.producer.IDataKey, org.juxtapose.fasid.util.IDataSubscriber)
	 */
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			logError( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		lock( inDataKey.getKey() );
		
		IPublishedData existingData = keyToData.get( inDataKey.getKey() );
		
		IDataProducer producer = null;
		
		IPublishedData newData = null;
		
		if( existingData == null )
		{
			//First subscriber
			producer = producerService.getDataProducer( inDataKey );
			
			//REVISIT Potentially we should not notify subscribers for certain newDatas and just wait for the initial update instead.
			newData = createEmptyData( Status.ON_REQUEST, producer, inSubscriber);
			
			keyToData.put( inDataKey.getKey(), newData );
		}
		else
		{
			newData = existingData.addSubscriber( inSubscriber );
			keyToData.put( inDataKey.getKey(), newData );
		}
		
		unlock( inDataKey.getKey() );
		
		inSubscriber.updateData( inDataKey.getKey(), newData, true );
		
		if( producer != null )
			producer.init();
	}

	
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#unsubscribeToData(org.juxtapose.fasid.producer.IDataKey, org.juxtapose.fasid.util.IDataSubscriber)
	 */
	@Override
	public void unsubscribeToData(IDataKey inDataKey, IDataSubscriber inSubscriber)
	{
		IDataProducerService producerService = idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			logError( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		lock( inDataKey.getKey() );
		
		IPublishedData existingData = keyToData.get( inDataKey.getKey() );
		
		IDataProducer producer = null;
		
		if( existingData == null )
		{
			logError( "Key: "+inDataKey+", Data has already been removed which is unconditional since an existing subscriber is requesting to unsubscribe"  );
			return;
		}
		else
		{
			IPublishedData newData = existingData.removeSubscriber( inSubscriber );
			if( newData.hasSubscribers() )
			{
				keyToData.replace( inDataKey.getKey(), newData );
			}
			else
			{
				keyToData.remove( inDataKey.getKey() );
				producer = existingData.getProducer();
			}
		}
		
		unlock( inDataKey.getKey() );
		
		if( producer != null )
			producer.dispose();
		
	}
}
