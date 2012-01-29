package org.juxtapose.fasid.stm;

import static org.juxtapose.fasid.stm.STMUtil.PRODUCER_SERVICES;
import static org.juxtapose.fasid.util.DataConstants.FIELD_QUERY_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.executor.IExecutor;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.KeyConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeRef;
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
	protected final ConcurrentHashMap<String, IPublishedData> keyToData = new ConcurrentHashMap<String, IPublishedData>();	
	//Services that create producers to data id is service ID
	protected final ConcurrentHashMap<Integer, IDataProducerService> idToProducerService = new ConcurrentHashMap<Integer, IDataProducerService>();
	
	private IExecutor executor;
	
	private IPublishedDataFactory dataFactory;
	
	/**
	 * @param inExecutor
	 */
	public void init( IExecutor inExecutor )
	{
		executor = inExecutor;
		keyToData.put( KeyConstants.PRODUCER_SERVICE_KEY.getKey(), createEmptyData(Status.OK, this, this));
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
		
		commit( new DataTransaction( KeyConstants.PRODUCER_SERVICE_KEY.getKey(), this )
		{
			@Override
			public void execute()
			{
				putValue( inProducerService.getServiceId(), new DataTypeString( initState.toString() ) );
			}
		});
	}
	
	/**
	 * @param inProducerService
	 * @param initState
	 */
	public void updateProducerStatus( final IDataProducerService inProducerService, final Status initState )
	{
		commit( new DataTransaction( KeyConstants.PRODUCER_SERVICE_KEY.getKey(), this )
		{
			@Override
			public void execute()
			{
				putValue( inProducerService.getServiceId(), new DataTypeString( initState.toString() ) );
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
			return KeyConstants.PRODUCER_SERVICE_KEY;
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
	
	public void init()
	{
		
	}
	public void dispose()
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
	
	public void logError( String inMessage, Throwable inThrowable )
	{
		System.err.println( inMessage );
		inThrowable.printStackTrace();
	}
	
	public void logWarning( String inMessage )
	{
		System.err.println( inMessage );
	}
	
	public void logDebug( String inMessage )
	{
		System.err.println( inMessage );
	}
	
	public IPublishedData getData( String inKey )
	{
		return keyToData.get( inKey );
	}
	
	public void addDataReferences( Integer inFieldKey, ReferenceLink inLink ){}
	public ReferenceLink removeReferenceLink( Integer inField ){ return null; }
	public void disposeReferenceLinks( List< Integer > inReferenceFields ){}
	public void referencedDataUpdated( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData ){}
}
