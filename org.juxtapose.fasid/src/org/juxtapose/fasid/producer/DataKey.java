package org.juxtapose.fasid.producer;

import java.util.HashMap;

/**
 * @author Pontus J�rgne
 * 7 aug 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public class DataKey implements IDataKey
{
	private final HashMap<Integer, String> m_keyMap;
	private final Integer m_producerServiceKey;
	private final String m_key;
	
	/**
	 * @param inProducerServiceKey
	 * @param inMap
	 * @param inKey
	 */
	public DataKey( Integer inProducerServiceKey, HashMap<Integer, String> inMap, String inKey )
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
	public String getValue( Integer inKey )
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
	public Integer getService()
	{
		return m_producerServiceKey;
	}
}
