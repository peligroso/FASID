package org.juxtapose.fxtradingsystem.quoteprovider;

import java.io.Serializable;

public class QuoteMessage extends Message implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public QuoteMessage( String inCcy1, String inCcy2, String inPeriod, Double inBid, Double inAsk )
	{
		super( inCcy1, inCcy2, inPeriod );
		bid = inBid;
		ask = inAsk;
	}
	
	public Double bid;
	public Double ask;

}
