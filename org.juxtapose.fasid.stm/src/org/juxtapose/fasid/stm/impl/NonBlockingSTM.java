package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;

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
				IPersistentMap<String, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, Status.ON_REQUEST );
				IPersistentMap<String, DataType<?>> lastUpdateMap = PersistentHashMap.emptyMap();
				IPersistentVector<IDataSubscriber> subscribers = PersistentVector.create(inSubscriber);
			
				PublishedData newData = new PublishedData( dataMap, lastUpdateMap, subscribers );
			
				existingData = m_keyToData.putIfAbsent( inDataKey.getKey(), newData );
				set = (existingData ==  null);
				
				if( set )
				{
					//Init producer
					IDataProducer producer = producerService.getDataProducer( inDataKey );
					producer.start();
				}
			}
			else
			{
				PublishedData newData = existingData.addSubscriber( inSubscriber );
				set = m_keyToData.replace( inDataKey.getKey(), existingData, newData );
				
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

				inTransaction.putInitDataState( existingData.getDataMap() );
				inTransaction.execute();

				newData = existingData.setUpdatedData( inTransaction.getStateInstruction(), inTransaction.getDeltaState() );

			}
			while( !m_keyToData.replace( dataKey, existingData, newData ) );

		}catch( Exception e){}
		
	}

	@Override
	protected PublishedData createPublishedData( IDataKey inDataKey, Status initState )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void removePublishedData(String inDataKey)
	{
		// TODO Auto-generated method stub
		
	}


}
