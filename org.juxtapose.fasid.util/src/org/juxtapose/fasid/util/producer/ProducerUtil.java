package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

/**
 * @author Pontus Jörgne
 * 7 aug 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class ProducerUtil
{
	public static String DATA_KEY = "DATA_KEY";
	public static String AND = "&";
	public static String EQUALS = "=";
	public static String SERVICE_KEY_DELIM = ":";
	
	
	/**
	 * @param inServiceKey
	 * @param inKeyValues
	 * @return
	 */
	public static IDataKey createDataKey( String inServiceKey, String[] inKeyValues )
	{
		if( inKeyValues.length % 2 != 0 )
			throw new IllegalArgumentException("Key-value pairs must be even ");
		
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder(inServiceKey);
		sb.append(SERVICE_KEY_DELIM);
		
		for( int i = 0; i < inKeyValues.length; i+=2 )
		{
			String key = inKeyValues[i];
			String value = inKeyValues[i+1];
			
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
	public static IDataKey createDataKey( String inServiceKey, String inSingleValue )
	{
		return createDataKey( inServiceKey, new String[]{DATA_KEY, inSingleValue} );
	}
}
