package org.juxtapose.fasid.core.util;

import org.juxtapose.fasid.core.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

public class PublishedData{

	IPersistentMap<String, DataType<?>> m_dataMap = PersistentHashMap.emptyMap();
	IPersistentMap<String, DataType<?>> m_lastDeltaMap = PersistentHashMap.emptyMap();
	
	//TODO change to subscriber interface
	IPersistentVector<IDataSubscriber> m_subscribers = PersistentVector.emptyVector();
	
	public void init()
	{
		
	}
	
	private void updateSubscribers()
	{
		String s = "!Hej";
		Double d = new Double(2);
		
		s.getBytes();
		d.byteValue();
	}
	
	public void addSubscriber( IDataSubscriber inSubscriber )
	{
		m_subscribers.assocN(m_subscribers.count(), inSubscriber );
		inSubscriber.updateData( this );
	}
	
	
}
