package org.juxtapose.fxtradingsystem.quoteprovider;

public class Message
{
	public String ccy1;
	public String ccy2;
	public String period;
	
	public Message( String inCcy1, String inCcy2, String inPeriod )
	{
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		period = inPeriod;
	}
}
