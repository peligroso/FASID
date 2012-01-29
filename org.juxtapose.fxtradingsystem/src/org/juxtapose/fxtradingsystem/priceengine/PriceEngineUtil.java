package org.juxtapose.fxtradingsystem.priceengine;

import java.math.BigDecimal;
import java.util.HashMap;

import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fxtradingsystem.FXDataConstants;

public class PriceEngineUtil
{
	public static HashMap<Integer, String> getPriceQuery( String inCcy1, String inCcy2 )
	{
		HashMap<Integer, String> query = new HashMap<Integer, String>();
		query.put( PriceEngineDataConstants.FIELD_TYPE, PriceEngineDataConstants.STATE_TYPE_PRICE );
		query.put( FXDataConstants.FIELD_CCY1, inCcy1 );
		query.put( FXDataConstants.FIELD_CCY2, inCcy2 );
		
		return query;
	}
	
	/**
	 * @param inData
	 * @return
	 */
	public static BigDecimal[] getBidAskFromData( IPublishedData inData )
	{
		if( inData == null )
			return null;
		
		DataType<?> bid = inData.getValue( FXDataConstants.FIELD_BID );
		DataType<?> ask = inData.getValue( FXDataConstants.FIELD_ASK );
		
		if( bid == null || ask == null )
			return null;

		BigDecimal bidVal = (BigDecimal)bid.get();
		BigDecimal askVal = (BigDecimal)ask.get();
		
		return new BigDecimal[]{bidVal, askVal};
	}
	
	/**
	 * @param inData
	 * @return
	 */
	public static Long getDecimals( IPublishedData inData )
	{
		if( inData == null )
			return null;
		
		DataTypeLong dec = (DataTypeLong)inData.getValue( FXDataConstants.FIELD_DECIMALS );
		
		if( dec == null )
			return null;

		return dec.get();
	}
}
