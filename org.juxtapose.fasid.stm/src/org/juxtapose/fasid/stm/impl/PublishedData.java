package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;

import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeNull;
import org.juxtapose.fasid.util.producer.IDataProducer;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * This class belongs to the the STM
 * only STM may create or modify a PublishedData on pub/sub requests
 *
 */
final class PublishedData implements IPublishedData
{
	final IPersistentMap<String, DataType<?>> m_dataMap;
	final IPersistentMap<String, DataType<?>> m_lastUpdateMap;
	
	final IPersistentVector<IDataSubscriber> m_subscribers;
	
	final IDataProducer m_producer;
	
	PublishedData( IPersistentMap<String, DataType<?>> inData, IPersistentMap<String, DataType<?>> inLastUpdate, IPersistentVector<IDataSubscriber> inSubscribers, IDataProducer inProducer ) 
	{
		m_dataMap = inData;
		m_lastUpdateMap = inLastUpdate;
		m_subscribers = inSubscribers;
		m_producer = inProducer;
	}
	
	private void updateSubscribers()
	{
		
	}
	
	/**
	 * @param inSubscriber
	 * @return
	 */
	public PublishedData addSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.assocN(m_subscribers.count(), inSubscriber );
		return new PublishedData( m_dataMap, m_lastUpdateMap, newSub, m_producer );
	}
	
	/**
	 * @param inSubscriber
	 * @return
	 */
	public PublishedData removeSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.cons( inSubscriber );
		return new PublishedData( m_dataMap, m_lastUpdateMap, newSub, m_producer );
	}
	
	/**
	 * @return
	 */
	public boolean hasSubscribers()
	{
		return m_subscribers.length() > 0;
	}
	
	/**
	 * @param inKey
	 * @param inValue
	 * @return
	 * @throws Exception
	 */
	public PublishedData putDataValue( String inKey, DataType<?> inValue )throws Exception
	{
		IPersistentMap<String, DataType<?>> newMap;
		
		if( inValue instanceof DataTypeNull )
			newMap = m_dataMap.without( inKey );
		else
			newMap = m_dataMap.assoc( inKey, inValue );
		
		return new PublishedData( newMap, m_lastUpdateMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inStateTransitionMap
	 * @return
	 * @throws Exception
	 */
	public PublishedData putDataValues( HashMap<String, DataType<?>> inStateTransitionMap )throws Exception
	{
		IPersistentMap<String, DataType<?>> newDataMap = m_dataMap;
		
		for( String key : inStateTransitionMap.keySet() )
		{
			DataType<?> value = inStateTransitionMap.get( key );
			if( value instanceof DataTypeNull )
				newDataMap = newDataMap.without( key );
			else
				newDataMap = newDataMap.assoc( key, value );
		}
		
		return new PublishedData( newDataMap, m_lastUpdateMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inDataMap
	 * @return
	 */
	public PublishedData setDataMap( IPersistentMap<String, DataType<?>> inDataMap )
	{
		return new PublishedData( inDataMap, m_lastUpdateMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inDataMap
	 * @return
	 */
	public PublishedData setUpdatedData( IPersistentMap<String, DataType<?>> inDataMap, IPersistentMap<String, DataType<?>> inDeltaMap )
	{
		return new PublishedData( inDataMap, inDeltaMap, m_subscribers, m_producer );
	}
	
	
	/**
	 * @return
	 */
	protected IPersistentMap<String, DataType<?>> getDataMap()
	{
		return m_dataMap;
	}
	
	/**
	 * @return
	 */
	protected IPersistentMap<String, DataType<?>> getLastUpdateMap()
	{
		return m_lastUpdateMap;
	}
	
	public IDataProducer getProducer()
	{
		return m_producer;
	}

	
	
}
