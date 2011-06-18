package org.juxtapose.fasid.core.stm;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.juxtapose.fasid.core.util.IDataPublisher;
import org.juxtapose.fasid.core.util.IDataSubscriber;
import org.juxtapose.fasid.core.util.PublishedData;
import org.juxtapose.fasid.core.util.Status;
import org.juxtapose.fasid.core.util.data.DataType;

/**
 * @author Pontus
 *
 */
public class STM 
{
	private ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();
	
	private ConcurrentHashMap<String, IDataPublisher> m_keyToPublisher = new ConcurrentHashMap<String, IDataPublisher>();
	
	private ConcurrentHashMap<String, ReentrantReadWriteLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantReadWriteLock>();
	
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
	
	public void commit( Transaction inTransaction )
	{
		String dataKey = inTransaction.getDataKey();
		
		ReentrantReadWriteLock lock = m_keyToLock.get( dataKey );
		
//		lock.writeLock().lock();
		
		inTransaction.execute();
		HashMap<String, DataType<?>> inst = inTransaction.getStateInstruction();
		
//		lock.writeLock().unlock();
		
	}
	
}
