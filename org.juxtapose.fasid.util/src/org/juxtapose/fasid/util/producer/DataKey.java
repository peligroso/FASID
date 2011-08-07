package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

/**
 * @author Pontus Jörgne
 * 7 aug 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class DataKey implements IDataKey
{
	private final HashMap<String, String> m_keyMap;
	private final String m_producerServiceKey;
	private final String m_key;
	
	/**
	 * @param inProducerServiceKey
	 * @param inMap
	 * @param inKey
	 */
	public DataKey( String inProducerServiceKey, HashMap<String, String> inMap, String inKey )
	{
		m_producerServiceKey = inProducerServiceKey;
		m_keyMap = inMap;
		m_key = inKey;
	}
	
	public String toString()
	{
		return m_key.toString();
	}
	
	public String getKey()
	{
		return m_key;
	}
	
	
	/**
	 * @param inKey
	 * @return
	 */
	public String getValue( String inKey )
	{
		return m_keyMap.get( inKey );
	}
	
	
	/**
	 * @param inKey
	 * @return
	 */
	public boolean equals( Object inKey )
	{
		if( ! (inKey instanceof IDataKey) )
			return false;
		
		return m_key.equals( ((IDataKey)inKey ).getKey() );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return m_key.hashCode();
	}

	@Override
	public String getService()
	{
		return m_producerServiceKey;
	}
}
