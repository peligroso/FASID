package org.juxtapose.fasid.stm.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

import com.trifork.clj_ds.IPersistentMap;

/**
 * @author Pontus J�rgne
 * Jan 6, 2012
 * Copyright (c) Pontus J�rgne. All rights reserved
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
	public void commit( Transaction inTransaction )
	{	
		String dataKey = inTransaction.getDataKey();
		
		IPublishedData newData = null;
		
		lock( dataKey );
		
		try
		{
			IPublishedData existingData = keyToData.get( dataKey );
			if( existingData == null )
			{
				//data has been removed due to lack of interest, transaction is discarded
				unlock( dataKey );
				return;
			}

			if( !STMUtil.validateProducerToData(existingData, inTransaction) )
			{
				logError( "Wrong version DataProducer tried to update data: "+dataKey );
				//The producer for this data is of the wrong version, Transaction is discarded
				unlock( dataKey );
				return;
			}

			inTransaction.putInitDataState( existingData.getDataMap(), existingData.getStatus() );
			inTransaction.execute();
			IPersistentMap<Integer, DataType<?>> inst = inTransaction.getStateInstruction();
			Map<Integer, DataType<?>> delta = inTransaction.getDeltaState();

			newData = existingData.setUpdatedData( inst, delta, inTransaction.getStatus() );

			keyToData.put( dataKey, newData );
			
		}catch( Throwable t )
		{
			logError( t.getMessage() );
		}
		finally
		{
			unlock( dataKey );
		}
			//Init reference links
		Map< Integer, DataTypeRef > dataReferences = inTransaction.getAddedReferences();
		if( !dataReferences.isEmpty() )
		{
			IDataProducer producer = newData.getProducer();
			if( producer == null )
				logError( "Tried to add reference to data with null producer" );
			else
				producer.initDataReferences( dataReferences );
		}


		//Dispose reference links
		List< Integer > removedReferences = inTransaction.getRemovedReferences();
		if( !removedReferences.isEmpty() )
		{
			IDataProducer producer = newData.getProducer();
			if( producer == null )
				logError( "Tried to remove reference from data with null producer" );
			else
				producer.disposeReferenceLinks( removedReferences );
		}
			
		if( newData != null )
		{
			newData.updateSubscribers( dataKey );
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
		
		if( producer != null )
			producer.start();
		
		inSubscriber.updateData( inDataKey.getKey(), newData, true );
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
			producer.stop();
		
	}

}
