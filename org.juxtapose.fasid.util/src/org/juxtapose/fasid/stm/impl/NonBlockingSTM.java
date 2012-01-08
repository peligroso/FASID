package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;

/**
 * @author Pontus Jörgne
 * Jan 2, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * NonBlockngSTM is experimental ant not complete. It exhibits strange behavior and does not support DataTypeRef
 * Use BlockingSTM
 */
public class NonBlockingSTM extends STM
{
	
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			logError( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		IPublishedData existingData = keyToData.get( inDataKey.getKey() );
		
		boolean set = false;
		do
		{
			if( existingData == null )
			{
				//First subscriber
				IDataProducer producer = producerService.getDataProducer( inDataKey );
				IPublishedData newData = createEmptyData( Status.ON_REQUEST, producer, inSubscriber);
				
				existingData = keyToData.putIfAbsent( inDataKey.getKey(), newData );
				set = (existingData ==  null);
				
				if( set )
				{
					//Init producer
					producer.start();
				}
			}
			else
			{
				IPublishedData newData = existingData.addSubscriber( inSubscriber );
				set = keyToData.replace( inDataKey.getKey(), existingData, newData );
				
				if( !set )
					existingData = keyToData.get( inDataKey.getKey() );
				else
					inSubscriber.updateData( inDataKey.getKey(), existingData, true );
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
		IDataProducerService producerService = idToProducerService.get( inDataKey.getService() );
		if( producerService == null )
		{
			logError( "Key: "+inDataKey+" not valid, producer service does not exist"  );
			return;
		}
		
		IPublishedData existingData = keyToData.get( inDataKey.getKey() );
		
		boolean set = false;
		do
		{
			if( existingData == null )
			{
				logError( "Key: "+inDataKey+" not valid, data does not exist"  );
				return;
			}
			else
			{
				IPublishedData newData = existingData.removeSubscriber( inSubscriber );
				if( newData.hasSubscribers() )
				{
					set = keyToData.replace( inDataKey.getKey(), existingData, newData );
				}
				else
				{
					set = keyToData.remove( inDataKey.getKey(), existingData );
					if( set )
					{
						existingData.getProducer().stop();
					}
				}
				
				if( !set )
					existingData = keyToData.get( inDataKey.getKey() );
			}
		}
		while( !set );
	}
	
	@Override
	public void commit(Transaction inTransaction)
	{
		String dataKey = inTransaction.getDataKey();

		IPublishedData existingData;
		IPublishedData newData;

		try
		{
			do
			{
				existingData = keyToData.get( dataKey );
				if( existingData == null )
				{
					//data has been removed due to lack of interest, transaction is discarded
					return;
				}

				if( STMUtil.validateProducerToData(existingData, inTransaction) )
				{
					logError( "Wrong version DataProducer tried to update data: "+dataKey );
					//The producer for this data is of the wrong version, Transaction is discarded
					return;
				}
				
				inTransaction.putInitDataState( existingData.getDataMap(), existingData.getStatus() );
				inTransaction.execute();

				newData = existingData.setUpdatedData( inTransaction.getStateInstruction(), inTransaction.getDeltaState(), inTransaction.getStatus() );

			}
			while( !keyToData.replace( dataKey, existingData, newData ) );
			newData.updateSubscribers( dataKey );

		}catch( Exception e){}
		
	}


}
