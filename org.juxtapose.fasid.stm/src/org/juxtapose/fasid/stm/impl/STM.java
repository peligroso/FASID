package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataPublisher;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;

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
public class STM 
{
	/**Used for stack validation**/
	static String COMMIT_METHOD = "commit";
	
	private static final boolean USE_LOCKING = false;
	
	private ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();	
	private ConcurrentHashMap<Integer, IDataPublisher> m_idToPublisher = new ConcurrentHashMap<Integer, IDataPublisher>();
	private ConcurrentHashMap<String, ReentrantReadWriteLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
	
	private ReentrantLock m_dataKeyMasterLock = new ReentrantLock();
	
	
	private PublishedData createPublishedData( String inDataKey )
	{
		PublishedData data;
		
		m_dataKeyMasterLock.lock();
		
		data = m_keyToData.get( inDataKey );
		
		if( data == null )
		{
			IPersistentMap<String, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.STATUS, new DataTypeString( Status.ON_REQUEST.toString()));
			IPersistentMap<String, DataType<?>> lastUpdateMap = PersistentHashMap.emptyMap();
			IPersistentVector<IDataSubscriber> subscribers = PersistentVector.emptyVector();
			
			data = new PublishedData( dataMap, lastUpdateMap, subscribers );
			
			m_keyToData.put( inDataKey , data );
			
			//TODO notify publisher and figure out if this should be done inside the lock
		}
		
		m_dataKeyMasterLock.unlock();
		
		return data;
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
					return;

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
		
//		lock.writeLock().lock();
		
		inTransaction.execute();
		IPersistentMap<String, DataType<?>> inst = inTransaction.getStateInstruction();
		
//		lock.writeLock().unlock();
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
	
}
