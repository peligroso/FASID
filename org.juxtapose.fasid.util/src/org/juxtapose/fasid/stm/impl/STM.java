package org.juxtapose.fasid.stm.impl;

import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICES;
import static org.juxtapose.fasid.stm.exp.STMUtil.PRODUCER_SERVICE_KEY;
import static org.juxtapose.fasid.util.DataConstants.FIELD_QUERY_KEY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.executor.IExecutor;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fasid.util.producerservices.ProducerServiceConstants;

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
	
	protected final ConcurrentHashMap<String, IPublishedData> keyToData = new ConcurrentHashMap<String, IPublishedData>();	
	//Services that create producers to data id is service ID
	protected final ConcurrentHashMap<Integer, IDataProducerService> idToProducerService = new ConcurrentHashMap<Integer, IDataProducerService>();
	
	protected final HashMap<String, List<ReferenceLink>> m_keyToReferensLinks = new HashMap<String, List<ReferenceLink>>();
	
	private IExecutor executor;
	
	private IPublishedDataFactory dataFactory;
	
	/**
	 * @param inExecutor
	 */
	public void init( IExecutor inExecutor )
	{
		executor = inExecutor;
		keyToData.put( PRODUCER_SERVICE_KEY.getKey(), createEmptyData(Status.OK, this, this));
		registerProducer( this, Status.OK );
	}
	
	/**
	 * @param inProducerService
	 * @param initState
	 */
	public void registerProducer( final IDataProducerService inProducerService, final Status initState )
	{
		Integer id = inProducerService.getServiceId();
		idToProducerService.put( id, inProducerService );
		
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
		return ProducerServiceConstants.STM_SERVICE_KEY;
	}

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.producer.IDataProducerService#getKey(java.util.HashMap)
	 */
	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
	{
		String val = inQuery.get( FIELD_QUERY_KEY );
		
		if( val != null && val == PRODUCER_SERVICES )
		{
			return PRODUCER_SERVICE_KEY;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#getDataKey(java.lang.Integer, java.util.HashMap)
	 */
	public IDataKey getDataKey(Integer inProducerService, HashMap<Integer, String> inQuery)
	{
		IDataProducerService producerService = idToProducerService.get( inProducerService );
		if( producerService == null )
		{
			System.err.println( "Producer "+inProducerService+" could not be found ");
			return null;
		}
		return producerService.getDataKey( inQuery );
	}
	
	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		return this;
	}
	
	public void updateData( String inKey, IPublishedData inData, boolean inFirstUpdate )
	{
		
	}
	
	public void start()
	{
		
	}
	public void stop()
	{
		
	}
	
	/**
	 * @param inDataFactory
	 */
	public void setDataFactory( IPublishedDataFactory inDataFactory )
	{
		dataFactory = inDataFactory;
	}
	
	/**
	 * @param inStatus
	 * @param inProducer
	 * @param inSubscriber
	 * @return
	 */
	public IPublishedData createEmptyData( Status inStatus, IDataProducer inProducer, IDataSubscriber inSubscriber )
	{
		if( dataFactory == null )
		{
			logError( "Datafactory has not been initiated" );
			System.exit(1);
		}
		return dataFactory.createData(inStatus, inProducer, inSubscriber);
	}
	
	/**
	 * @param inStatus
	 * @param inProducer
	 * @return
	 */
	protected IPublishedData createEmptyData( Status inStatus, IDataProducer inProducer )
	{
		if( dataFactory == null )
		{
			logError( "Datafactory has not been initiated" );
			System.exit(1);
		}
		return dataFactory.createData(inStatus, inProducer );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#execute(java.lang.Runnable)
	 */
	public void execute( Runnable inRunnable )
	{
		executor.execute( inRunnable );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#execute(java.lang.Runnable, java.lang.String)
	 */
	public void execute( Runnable inRunnable, String inSequenceKey )
	{
		executor.execute( inRunnable, inSequenceKey );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#execute(java.lang.Runnable, java.util.concurrent.locks.ReentrantLock)
	 */
	public void execute( Runnable inRunnable, ReentrantLock inSequenceLock )
	{
		executor.execute( inRunnable, inSequenceLock );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#logInfo(java.lang.String)
	 */
	public void logInfo( String inMessage )
	{
		System.out.println( inMessage );
	}
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.exp.ISTM#logError(java.lang.String)
	 */
	public void logError( String inMessage )
	{
		System.err.println( inMessage );
	}
	
	/**
	 * @param inKey
	 * Not synchronized 
	 */
	protected List<ReferenceLink> getReferensList( String inKey )
	{
		List<ReferenceLink> links = m_keyToReferensLinks.get( inKey );
		if( links == null )
		{
			links = new ArrayList<ReferenceLink>();
			m_keyToReferensLinks.put( inKey, links );
		}
		return links;
	}
	
	/**
	 * @param inKey
	 * NOt Synchronized
	 */
	protected void removeReferenceLinks( String inKey )
	{
		List<ReferenceLink> links = m_keyToReferensLinks.get( inKey );
		if( links != null )
		{
			for( ReferenceLink link : links )
			{
				link.dispose();
			}
		}
		
		m_keyToReferensLinks.remove( inKey );
	}
}
