package org.juxtapose.fasid.stm.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	final IPersistentMap<Integer, DataType<?>> m_dataMap;
	final Map<Integer, DataType<?>> m_deltaMap;
	
	final IPersistentVector<IDataSubscriber> m_subscribers;
	
	final IDataProducer m_producer;
	
	PublishedData( IPersistentMap<Integer, DataType<?>> inData, Map<Integer, DataType<?>> inLastUpdate, IPersistentVector<IDataSubscriber> inSubscribers, IDataProducer inProducer ) 
	{
		m_dataMap = inData;
		m_deltaMap = Collections.unmodifiableMap( inLastUpdate );
		m_subscribers = inSubscribers;
		m_producer = inProducer;
	}
	
	protected void updateSubscribers()
	{
		for( int i = 0; i < m_subscribers.length(); i++ )
		{
			IDataSubscriber subscriber = m_subscribers.nth( i );
			subscriber.updateData( this, false );
		}
	}
	
	/**
	 * @param inSubscriber
	 * @return
	 */
	public PublishedData addSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.assocN(m_subscribers.count(), inSubscriber );
		return new PublishedData( m_dataMap, m_deltaMap, newSub, m_producer );
	}
	
	/**
	 * @param inSubscriber
	 * @return
	 */
	public PublishedData removeSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.cons( inSubscriber );
		return new PublishedData( m_dataMap, m_deltaMap, newSub, m_producer );
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
	public PublishedData putDataValue( Integer inKey, DataType<?> inValue )throws Exception
	{
		IPersistentMap<Integer, DataType<?>> newMap;
		
		if( inValue instanceof DataTypeNull )
			newMap = m_dataMap.without( inKey );
		else
			newMap = m_dataMap.assoc( inKey, inValue );
		
		return new PublishedData( newMap, m_deltaMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inStateTransitionMap
	 * @return
	 * @throws Exception
	 */
	public PublishedData putDataValues( HashMap<Integer, DataType<?>> inStateTransitionMap )throws Exception
	{
		IPersistentMap<Integer, DataType<?>> newDataMap = m_dataMap;
		
		for( Integer key : inStateTransitionMap.keySet() )
		{
			DataType<?> value = inStateTransitionMap.get( key );
			if( value instanceof DataTypeNull )
				newDataMap = newDataMap.without( key );
			else
				newDataMap = newDataMap.assoc( key, value );
		}
		
		return new PublishedData( newDataMap, m_deltaMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inDataMap
	 * @return
	 */
	public PublishedData setDataMap( IPersistentMap<Integer, DataType<?>> inDataMap )
	{
		return new PublishedData( inDataMap, m_deltaMap, m_subscribers, m_producer );
	}
	
	/**
	 * @param inDataMap
	 * @return
	 */
	public PublishedData setUpdatedData( IPersistentMap<Integer, DataType<?>> inDataMap, Map<Integer, DataType<?>> inDeltaMap )
	{
		return new PublishedData( inDataMap, inDeltaMap, m_subscribers, m_producer );
	}
	
	
	/**
	 * @return
	 */
	public IPersistentMap<Integer, DataType<?>> getDataMap()
	{
		return m_dataMap;
	}
	
	/**
	 * @return
	 */
	public Map<Integer, DataType<?>> getDeltaMap()
	{
		return m_deltaMap;
	}
	
	public IDataProducer getProducer()
	{
		return m_producer;
	}
	
	/**
	 * @param inKey
	 * @return
	 */
	public DataType<?> getValue( int inKey )
	{
		return m_dataMap.valAt( inKey );
	}

	
	
}
