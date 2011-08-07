package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

public class DataKey implements IDataKey
{
	HashMap<String, String> m_keyMap;
	String m_key;
	
	public DataKey( HashMap<String, String> inMap, String inKey )
	{
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
}
