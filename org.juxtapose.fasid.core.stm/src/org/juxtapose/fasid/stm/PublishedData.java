package org.juxtapose.fasid.stm;

import java.util.HashMap;

import org.juxtapose.fasid.core.util.IDataSubscriber;
import org.juxtapose.fasid.core.util.IPublishedData;
import org.juxtapose.fasid.core.util.data.DataType;
import org.juxtapose.fasid.core.util.data.DataTypeNull;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;

/**
 * @author Pontus
 * This class belongs to the the STM
 * only STM may create or modify a PublishedData on pub/sub requests
 *
 */
final class PublishedData implements IPublishedData
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
	
	/**
	 * @param inSubscriber
	 * @return
	 */
	public PublishedData addSubscriber( IDataSubscriber inSubscriber )
	{
		IPersistentVector<IDataSubscriber> newSub = m_subscribers.assocN(m_subscribers.count(), inSubscriber );
		return new PublishedData( m_dataMap, m_lastUpdateMap, newSub );
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
		
		return new PublishedData( newMap, m_lastUpdateMap, m_subscribers );
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
		
		return new PublishedData( newDataMap, m_lastUpdateMap, m_subscribers );
	}

	
	
}
