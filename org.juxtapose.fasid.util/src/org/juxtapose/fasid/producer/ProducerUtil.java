package org.juxtapose.fasid.producer;

import java.util.HashMap;

/**
 * @author Pontus J�rgne
 * 7 aug 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public class ProducerUtil
{
	public static Integer FIELD_SINGLE_VALUE_DATA_KEY = 1;
	public static String AND = "&";
	public static String EQUALS = "=";
	public static String SERVICE_KEY_DELIM = ":";
	
	
	/**
	 * @param inServiceKey
	 * @param inKeyValues
	 * @return
	 */
	public static IDataKey createDataKey( Integer inServiceKey, String inType, Integer[] inKeys, String[] inValues )
	{
		if( inKeys.length != inValues.length )
			throw new IllegalArgumentException("Key-value pairs must be even ");
		
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		StringBuilder sb = new StringBuilder(inServiceKey.toString());
		sb.append(SERVICE_KEY_DELIM);
		sb.append(inType);
		sb.append(SERVICE_KEY_DELIM);
		
		for( int i = 0; i < inKeys.length; i++ )
		{
			Integer key = inKeys[i];
			String value = inValues[i];
			
			map.put( key, value );
			if( i != 0 )
				sb.append(AND);
			
			sb.append( key );
			sb.append( EQUALS );
			sb.append( value );
		}
		
		return new DataKey( inServiceKey, inType, map, sb.toString() ); 
	}
	
	/**
	 * @param inSingleValue
	 * @return
	 */
	public static IDataKey createDataKey( Integer inServiceKey, String inType, String inSingleValue )
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put( FIELD_SINGLE_VALUE_DATA_KEY, inSingleValue );
		String key = inServiceKey+SERVICE_KEY_DELIM+inType+SERVICE_KEY_DELIM+FIELD_SINGLE_VALUE_DATA_KEY+EQUALS+inSingleValue;
		
		return new DataKey( inServiceKey, inType, map, key ); 
	}
	
	public static IDataKey createDataKey( Integer inServiceKey, String inType )
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		String key = inServiceKey+SERVICE_KEY_DELIM+inType;
		
		return new DataKey( inServiceKey, inType, map, key ); 
	}
}
