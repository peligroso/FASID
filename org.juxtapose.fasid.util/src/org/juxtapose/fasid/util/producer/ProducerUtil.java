package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

/**
 * @author Pontus Jörgne
 * 7 aug 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class ProducerUtil
{
	public static Integer SINGLE_VALUE_DATA_KEY = 1;
	public static String AND = "&";
	public static String EQUALS = "=";
	public static String SERVICE_KEY_DELIM = ":";
	
	
	/**
	 * @param inServiceKey
	 * @param inKeyValues
	 * @return
	 */
	public static IDataKey createDataKey( Integer inServiceKey, Integer[] inKeys, String[] inValues )
	{
		if( inKeys.length != inValues.length )
			throw new IllegalArgumentException("Key-value pairs must be even ");
		
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		StringBuilder sb = new StringBuilder(inServiceKey);
		sb.append(SERVICE_KEY_DELIM);
		
		for( int i = 0; i < inKeys.length; i++ )
		{
			Integer key = inKeys[i];
			String value = inValues[i+1];
			
			map.put( key, value );
			if( i != 0 )
				sb.append(AND);
			
			sb.append( key );
			sb.append( EQUALS );
			sb.append( value );
		}
		
		return new DataKey( inServiceKey, map, sb.toString() ); 
	}
	
	/**
	 * @param inSingleValue
	 * @return
	 */
	public static IDataKey createDataKey( Integer inServiceKey, String inSingleValue )
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put( SINGLE_VALUE_DATA_KEY, inSingleValue );
		String key = inServiceKey+SERVICE_KEY_DELIM+SINGLE_VALUE_DATA_KEY+EQUALS+inSingleValue;
		
		return new DataKey( inServiceKey, map, key ); 
	}
}
