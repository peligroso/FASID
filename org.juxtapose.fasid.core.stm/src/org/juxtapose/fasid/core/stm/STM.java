package org.juxtapose.fasid.core.stm;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.core.util.IDataPublisher;
import org.juxtapose.fasid.core.util.IDataSubscriber;
import org.juxtapose.fasid.core.util.PublishedData;
import org.juxtapose.fasid.core.util.Status;

public class STM 
{
	private ConcurrentHashMap<String, PublishedData> m_keyToData = new ConcurrentHashMap<String, PublishedData>();
	
	private ConcurrentHashMap<String, IDataPublisher> m_keyToPublisher = new ConcurrentHashMap<String, IDataPublisher>();
	
	private ConcurrentHashMap<String, ReentrantLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantLock>();
	
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
		
	}
	
}
