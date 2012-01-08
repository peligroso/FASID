package org.juxtapose.fxtradingsystem.lpmockup;

import java.util.ArrayList;
import java.util.Random;

public class Instrument
{
	public String ccy1;
	public String ccy2;
	public String period;
	
	public Double bid;
	public Double ask;
	
	public ArrayList<ILPListener> subscribers = new ArrayList<ILPListener>();
	
	public Instrument( String inCcy1, String inCcy2, String inPeriod )
	{
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		period = inPeriod;
	}
	
	public void addSubscriber( ILPListener inListener )
	{
		subscribers.add( inListener );
	}
	
	public void addPriceUpdate( final Random inRand )
	{
		bid = inRand.nextDouble();
		ask = inRand.nextDouble();
	}
	
	public void updateSubscribers()
	{
		for( ILPListener listener : subscribers )
		{
			listener.priceUpdate( ccy1, ccy2, period, bid, ask );
		}
	}
	
	public String getKey()
	{
		return ccy1+ccy2+period;
	}
}
