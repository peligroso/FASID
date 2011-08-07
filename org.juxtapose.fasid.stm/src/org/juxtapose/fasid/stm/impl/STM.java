package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fasid.util.lock.HashStripedLock;
import org.juxtapose.fasid.util.producer.DataKey;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;
import org.juxtapose.fasid.util.producer.ProducerUtil;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

import static org.juxtapose.fasid.util.DataConstants.*;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 *
 *Software Transactional Memory
 *
 */
public class STM implements IDataProducerService
{
	/**Used for stack validation**/
	static String COMMIT_METHOD = "commit";
	
	private static final boolean USE_LOCKING = true;
	
	private ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();	
	//Services that create producers to data id is service ID
	private ConcurrentHashMap<String, IDataProducerService> m_idToProducerService = new ConcurrentHashMap<String, IDataProducerService>();
	
	private ConcurrentHashMap<String, ReentrantReadWriteLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
	
	/**Used for creation and deletion of DataKey locks**/
	private HashStripedLock m_dataKeyMasterLock = new HashStripedLock( 256 );
	
	protected void init()
	{
		createPublishedData( PRODUCER_SERVICES, Status.OK );
		registerProducer( this, Status.OK );
		
	}
	
	public void registerProducer( final IDataProducerService inProducerService, final Status initState )
	{
		String id = inProducerService.getServiceId();
		m_idToProducerService.put( id, inProducerService );
		
		commit( new DataTransaction( PRODUCER_SERVICES )
		{
			@Override
			public void execute()
			{
				addValue( inProducerService.getServiceId(), new DataTypeString( initState.toString() ) );
			}
		});
	}
	
	
	/**
	 * @param inDataKey
	 * @return
	 */
	private PublishedData createPublishedData( String inDataKey, Status initState )
	{
		PublishedData data;
		
		m_dataKeyMasterLock.lock( inDataKey );
		
		ReentrantReadWriteLock newLock = new ReentrantReadWriteLock( true );
		ReentrantReadWriteLock lock = m_keyToLock.putIfAbsent( inDataKey, newLock );
		lock = lock == null ? newLock : lock;
		
		lock.writeLock().lock();
		
		data = m_keyToData.get( inDataKey );
		
		if( data == null )
		{
			IPersistentMap<String, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, new DataTypeString( initState.toString()));
			IPersistentMap<String, DataType<?>> lastUpdateMap = PersistentHashMap.emptyMap();
			IPersistentVector<IDataSubscriber> subscribers = PersistentVector.emptyVector();
			
			data = new PublishedData( dataMap, lastUpdateMap, subscribers );
			
			m_keyToData.put( inDataKey , data );
			
			//TODO notify publisher and figure out if this should be done inside the lock
		}
		
		lock.writeLock().unlock();
		m_dataKeyMasterLock.unlock( inDataKey );
		
		return data;
	}
	
	/**
	 * @param inDataKey
	 */
	protected void removePublishedData( String inDataKey )
	{
		m_dataKeyMasterLock.lock( inDataKey );
		
		ReentrantReadWriteLock lock = m_keyToLock.get( inDataKey );
		if( lock != null )
		{
			lock.writeLock().lock();
			
			m_keyToData.remove( inDataKey );
			
			lock.writeLock().unlock();
		}
		
		m_keyToLock.remove( inDataKey );
		
		m_dataKeyMasterLock.unlock( inDataKey );
	}
	
	public Status subscribe( String inPublisherKey, HashMap<String, String> inQuery, IDataSubscriber inSubscriber )
	{
//		IDataPublisher publisher = m_idToPublisher.get( inPublisherKey );
//		
//		if( publisher == null )
//			return Status.NA;
//		
//		String key = publisher.subscribe( inQuery );
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
	
	/**
	 * @param inTransaction
	 */
	public void commitWithLocks( Transaction inTransaction )
	{
		String dataKey = inTransaction.getDataKey();
		ReentrantReadWriteLock lock = m_keyToLock.get( dataKey );
		
		if( lock == null )
		{
			//data has been removed due to lack of interest, transaction is discarded
			return;
		}
		
		lock.writeLock().lock();
		
		PublishedData existingData = m_keyToData.get( dataKey );
		if( existingData == null )
		{
			//data has been removed due to lack of interest, transaction is discarded
			return;
		}
		
		inTransaction.putInitDataState( existingData.getDataMap() );
		inTransaction.execute();
		IPersistentMap<String, DataType<?>> inst = inTransaction.getStateInstruction();
		IPersistentMap<String, DataType<?>> delta = inTransaction.getDeltaState();
		
		existingData.setUpdatedData( inst, delta );
		
		lock.writeLock().unlock();
	}
	
	/**
	 * @param inTransaction
	 */
	public void commit( Transaction inTransaction )
	{
		if( USE_LOCKING )
		{
			commitWithLocks( inTransaction );
		}
		else
		{
			commitWithCAS( inTransaction );
		}
	}

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.producer.IDataProducerService#getServiceId()
	 */
	@Override
	public String getServiceId()
	{
		return STMUtil.STM_SERVICE_KEY;
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
			return ProducerUtil.createDataKey( new String[]{PRODUCER_SERVICES, PRODUCER_SERVICES});
		}
		return null;
	}
	
	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//Test purpose
	public static void main( String... args )
	{
		STM stm = new STM();
		stm.init();
	}

	
}
