package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;
import java.util.Map;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

public class NonBlockingSTM extends STM
{
	
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = m_idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			System.err.print( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		PublishedData existingData = m_keyToData.get( inDataKey.getKey() );
		
		boolean set = false;
		do
		{
			if( existingData == null )
			{
				//First subscriber
				IPersistentMap<Integer, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, Status.ON_REQUEST );
				Map<Integer, DataType<?>> deltaMap = new HashMap<Integer, DataType<?>>();
				IPersistentVector<IDataSubscriber> subscribers = PersistentVector.create(inSubscriber);
				
				IDataProducer producer = producerService.getDataProducer( inDataKey );
			
				PublishedData newData = new PublishedData( dataMap, deltaMap, subscribers, producer );
			
				existingData = m_keyToData.putIfAbsent( inDataKey.getKey(), newData );
				set = (existingData ==  null);
				
				if( set )
				{
					//Init producer
					producer.start();
				}
			}
			else
			{
				PublishedData newData = existingData.addSubscriber( inSubscriber );
				set = m_keyToData.replace( inDataKey.getKey(), existingData, newData );
				
				if( !set )
					existingData = m_keyToData.get( inDataKey.getKey() );
				else
					inSubscriber.updateData( existingData, true );
			}
		}
		while( !set );
		
	}
	
	/**
	 * @param inDataKey
	 * @param inSubscriber
	 */
	public void unsubscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = m_idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			System.err.print( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		PublishedData existingData = m_keyToData.get( inDataKey.getKey() );
		
		boolean set = false;
		do
		{
			if( existingData == null )
			{
				System.err.print( "Key: "+inDataKey+" not valid, data does not exist"  );
				return;
			}
			else
			{
				PublishedData newData = existingData.removeSubscriber( inSubscriber );
				if( newData.hasSubscribers() )
				{
					set = m_keyToData.replace( inDataKey.getKey(), existingData, newData );
				}
				else
				{
					set = m_keyToData.remove( inDataKey.getKey(), existingData );
					if( set )
					{
						existingData.getProducer().stop();
					}
				}
				
				if( !set )
					existingData = m_keyToData.get( inDataKey.getKey() );
			}
		}
		while( !set );
	}
	
	@Override
	public void commit(Transaction inTransaction)
	{
		String dataKey = inTransaction.getDataKey();

		PublishedData existingData;
		PublishedData newData;

		try
		{
			do
			{
				existingData = m_keyToData.get( dataKey );
				if( existingData == null )
				{
					//data has been removed due to lack of interest, transaction is discarded
					return;
				}

				if( STMUtil.validateProducerToData(existingData, inTransaction) )
				{
					System.out.println( "Wrong version DataProducer tried to update data: "+dataKey );
					//The producer for this data is of the wrong version, Transaction is discarded
					return;
				}
				
				inTransaction.putInitDataState( existingData.getDataMap() );
				inTransaction.execute();

				newData = existingData.setUpdatedData( inTransaction.getStateInstruction(), inTransaction.getDeltaState() );

			}
			while( !m_keyToData.replace( dataKey, existingData, newData ) );
			newData.updateSubscribers();

		}catch( Exception e){}
		
	}


}
