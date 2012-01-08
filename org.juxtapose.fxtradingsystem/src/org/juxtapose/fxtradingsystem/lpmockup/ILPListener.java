package org.juxtapose.fxtradingsystem.lpmockup;

public interface ILPListener
{
	public void priceUpdate( String inCcy1, String inCcy2, String inPeriod, Double inBid, Double inAsk );
}
