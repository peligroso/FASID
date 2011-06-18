package org.juxtapose.fasid.core.util;

import org.juxtapose.fasid.core.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

public class PublishedData
{
	final IPersistentMap<String, DataType<?>> m_dataMap;
	final IPersistentMap<String, DataType<?>> m_lastUpdateMap;
	
	final IPersistentVector<IDataSubscriber> m_subscribers;
	
	PublishedData( IPersistentMap<String, DataType<?>> inData, IPersistentMap<String, DataType<?>> inLastUpdate, IPersistentVector<IDataSubscriber> inSubscribers ) 
	{
		m_dataMap = inData;
		m_lastUpdateMap = inLastUpdate;
		m_subscribers = inSubscribers;
	}
	
	private void updateSubscribers()
	{
		
	}
	
	public PublishedData addSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.assocN(m_subscribers.count(), inSubscriber );
		return new PublishedData( m_dataMap, m_lastUpdateMap, newSub );
	}
	
	
}
