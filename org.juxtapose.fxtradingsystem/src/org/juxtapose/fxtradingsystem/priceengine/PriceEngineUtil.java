package org.juxtapose.fxtradingsystem.priceengine;

import java.util.HashMap;

import org.juxtapose.fxtradingsystem.FXDataConstants;

public class PriceEngineUtil
{
	public static HashMap<Integer, String> getPriceQuery( String inCcy1, String inCcy2 )
	{
		HashMap<Integer, String> query = new HashMap<Integer, String>();
		query.put( PriceEngineDataConstants.TYPE, PriceEngineDataConstants.STATE_TYPE_PRICE );
		query.put( FXDataConstants.FIELD_CCY1, inCcy1 );
		query.put( FXDataConstants.FIELD_CCY2, inCcy2 );
		
		return query;
	}
}
