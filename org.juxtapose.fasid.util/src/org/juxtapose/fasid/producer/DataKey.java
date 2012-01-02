package org.juxtapose.fasid.producer;

import java.util.HashMap;

/**
 * @author Pontus Jörgne
 * 7 aug 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class DataKey implements IDataKey
{
	private final HashMap<Integer, String> keyMap;
	private final Integer producerServiceKey;
	private final String type;
	private final String key;
	
	/**
	 * @param inProducerServiceKey
	 * @param inMap
	 * @param inKey
	 * DataKey are created via ProducerUtil
	 */
	protected DataKey( Integer inProducerServiceKey, String inType, HashMap<Integer, String> inMap, String inKey )
	{
		producerServiceKey = inProducerServiceKey;
		keyMap = inMap;
		key = inKey;
		type = inType;
	}
	
	public String toString()
	{
		return key.toString();
	}
	
	public String getKey()
	{
		return key;
	}
	
	
	/**
	 * @param inKey
	 * @return
	 */
	public String getValue( Integer inKey )
	{
		return keyMap.get( inKey );
	}
	
	
	/**
	 * @param inKey
	 * @return
	 */
	public boolean equals( Object inKey )
	{
		if( ! (inKey instanceof IDataKey) )
			return false;
		
		return key.equals( ((IDataKey)inKey ).getKey() );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return key.hashCode();
	}

	@Override
	public Integer getService()
	{
		return producerServiceKey;
	}
	
	public String getType()
	{
		return type;
	}
}
