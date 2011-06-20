package org.juxtapose.fasid.stm;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.PersistentMBean;

import org.juxtapose.fasid.util.IDataPublisher;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

/**
 * @author Pontus
 *
 *Software Transactional Memory
 */
public class STM 
{
	private static boolean USE_LOCKING = false;
	
	private ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();	
	private ConcurrentHashMap<String, IDataPublisher> m_keyToPublisher = new ConcurrentHashMap<String, IDataPublisher>();
	private ConcurrentHashMap<String, ReentrantReadWriteLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
	
	private ReentrantLock m_dataKeyMasterLock = new ReentrantLock();
	
	
	private PublishedData createPublishedData( String inDataKey )
	{
		PublishedData data;
		
		m_dataKeyMasterLock.lock();
		
		data = m_keyToData.get( inDataKey );
		
		if( data == null )
		{
			IPersistentMap<String, DataType<?>> dataMap = PersistentHashMap.emptyMap();
			IPersistentMap<String, DataType<?>> lastUpdateMap = PersistentHashMap.emptyMap();
			IPersistentVector<IDataSubscriber> subscribers = PersistentVector.emptyVector();
			
			data = new PublishedData( dataMap, lastUpdateMap, subscribers );
		}
		
		m_dataKeyMasterLock.unlock();
		
		return data;
	}
	
	public Status subscribe( String inPublisherKey, HashMap<String, String> inQuery, IDataSubscriber inSubscriber )
	{
		IDataPublisher publisher = m_keyToPublisher.get( inPublisherKey );
		
		if( publisher == null )
			return Status.NA;
		
		String key = publisher.subscribe( inQuery );
		
		if( key == null )
			return Status.NA;
		
		PublishedData data = m_keyToData.get( key );
		
		data.addSubscriber( inSubscriber );
		
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

				inTransaction.execute();

				newData = existingData.putDataValues( inTransaction.getStateInstruction() );

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
		HashMap<String, DataType<?>> inst = inTransaction.getStateInstruction();
		
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
