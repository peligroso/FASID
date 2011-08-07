package org.juxtapose.fasid.stm.impl;

import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICES;
import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICE_KEY;
import static org.juxtapose.fasid.util.DataConstants.QUERY_KEY;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fasid.util.lock.HashStripedLock;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;
import org.juxtapose.fasid.util.producerservices.ProducerServiceUtil;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 *
 *Software Transactional Memory
 *
 */
public abstract class STM implements IDataProducerService
{
	/**Used for stack validation**/
	static String COMMIT_METHOD = "commit";
	
	protected ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();	
	//Services that create producers to data id is service ID
	private ConcurrentHashMap<String, IDataProducerService> m_idToProducerService = new ConcurrentHashMap<String, IDataProducerService>();
	
	/**Used for creation and deletion of DataKey locks**/
	protected HashStripedLock m_dataKeyMasterLock = new HashStripedLock( 256 );
	
	protected void init()
	{
		createPublishedData( PRODUCER_SERVICE_KEY.getKey(), Status.OK );
		registerProducer( this, Status.OK );
		
	}
	
	/**
	 * @param inProducerService
	 * @param initState
	 */
	public void registerProducer( final IDataProducerService inProducerService, final Status initState )
	{
		String id = inProducerService.getServiceId();
		m_idToProducerService.put( id, inProducerService );
		
		commit( new DataTransaction( PRODUCER_SERVICE_KEY.getKey() )
		{
			@Override
			public void execute()
			{
				addValue( inProducerService.getServiceId(), new DataTypeString( initState.toString() ) );
			}
		});
	}
	
	
	public Status subscribe( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = m_idToProducerService.get( inDataKey.getService() );
		
		if( producerService == null )
			return Status.NA;
//		
//		String key = producerService.subscribe( inQuery );
//		
//		if( key == null )
//			return Status.NA;
//		
//		PublishedData data = m_keyToData.get( key );
//		
//		data.addSubscriber( inSubscriber );
//		
		return Status.NA;
		
		//...
		
	}
	
	/**
	 * @param inTransaction
	 */
	public void commitWithCAS( Transaction inTransaction )
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
	

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.producer.IDataProducerService#getServiceId()
	 */
	@Override
	public String getServiceId()
	{
		return ProducerServiceUtil.STM_SERVICE_KEY;
	}

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.producer.IDataProducerService#getKey(java.util.HashMap)
	 */
	@Override
	public IDataKey getDataKey(HashMap<String, String> inQuery)
	{
		String val = inQuery.get( QUERY_KEY );
		
		if( val != null && val == PRODUCER_SERVICES )
		{
			return PRODUCER_SERVICE_KEY;
		}
		return null;
	}
	
	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract void commit( Transaction inTransaction );
	protected abstract PublishedData createPublishedData( String inDataKey, Status initState );
	protected abstract void removePublishedData( String inDataKey );
	
	
}
