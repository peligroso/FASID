package org.juxtapose.fxtradingsystem.lpmockup;

import java.util.Random;

public class Instrument
{
	public String ccy1;
	public String ccy2;
	public String period;
	
	public Double bid;
	public Double ask;
	
	public Instrument( String inCcy1, String inCcy2, String inPeriod )
	{
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		period = inPeriod;
	}
	
	
	public void addPriceUpdate( final Random inRand )
	{
		bid = inRand.nextDouble();
		ask = inRand.nextDouble();
	}
	
	
	public String getKey()
	{
		return ccy1+ccy2+period;
	}
}
