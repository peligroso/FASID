package org.juxtapose.fasid.stm.impl;

import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICES;
import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICE_KEY;
import static org.juxtapose.fasid.util.DataConstants.QUERY_KEY;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.juxtapose.fasid.stm.exp.DataTransaction;
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
	protected ConcurrentHashMap<String, IDataProducerService> m_idToProducerService = new ConcurrentHashMap<String, IDataProducerService>();
	
	protected void init()
	{
		createPublishedData( PRODUCER_SERVICE_KEY, Status.OK );
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
	protected abstract PublishedData createPublishedData( IDataKey inDataKey, Status initState );
	protected abstract void removePublishedData( String inDataKey );
	public abstract void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber );
	
	
}
