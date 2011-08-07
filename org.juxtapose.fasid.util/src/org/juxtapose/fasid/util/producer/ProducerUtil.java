package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

public class ProducerUtil
{
	public static String AND = "&";
	public static String EQUALS = "=";
	
	public static IDataKey createDataKey( String[] inKeyValues )
	{
		if( inKeyValues.length % 2 != 0 )
			throw new IllegalArgumentException("Key-value pairs must be even ");
		
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		
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
		
		return new DataKey( map, sb.toString() ); 
	}
}
