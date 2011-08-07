package org.juxtapose.fasid.stm.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

public class BlockingSTM extends STM
{
	private ConcurrentHashMap<String, ReentrantReadWriteLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
	
	/**
	 * @param inDataKey
	 * @return
	 */
	protected PublishedData createPublishedData( String inDataKey, Status initState )
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
	
	public void commit( Transaction inTransaction )
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
}
