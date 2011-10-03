package org.juxtapose.fasid.stm.impl;

import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICES;
import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICE_KEY;
import static org.juxtapose.fasid.util.DataConstants.QUERY_KEY;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;
import org.juxtapose.fasid.util.producerservices.ProducerServiceUtil;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 *
 *Software Transactional Memory
 *
 */
public abstract class STM implements ISTM, IDataProducerService, IDataSubscriber, IDataProducer
{
	/**Used for stack validation**/
	static String COMMIT_METHOD = "commit";
	
	protected ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();	
	//Services that create producers to data id is service ID
	protected ConcurrentHashMap<Integer, IDataProducerService> m_idToProducerService = new ConcurrentHashMap<Integer, IDataProducerService>();
	
	public void init()
	{
		m_keyToData.put( PRODUCER_SERVICE_KEY.getKey(), createEmptyData(Status.OK, this));
		registerProducer( this, Status.OK );
	}
	
	/**
	 * @param inProducerService
	 * @param initState
	 */
	public void registerProducer( final IDataProducerService inProducerService, final Status initState )
	{
		Integer id = inProducerService.getServiceId();
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
	public Integer getServiceId()
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
		return this;
	}
	
	public void updateData( IPublishedData inData, boolean inFirstUpdate )
	{
		
	}
	
	public void start()
	{
		
	}
	public void stop()
	{
		
	}
	
	public static PublishedData createEmptyData( Status inStatus, IDataProducer inProducer )
	{
		IPersistentMap<Integer, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, inStatus );
		Map<Integer, DataType<?>> deltaMap = new HashMap<Integer, DataType<?>>();
		IPersistentVector<IDataSubscriber> subscribers = PersistentVector.create();
		
		return new PublishedData( dataMap, deltaMap, subscribers, inProducer );
	}
}
