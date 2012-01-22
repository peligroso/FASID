package org.juxtapose.fxtradingsystem.quoteprovider;

import java.io.Serializable;

public class UnsubscribeMessage extends Message implements Serializable
{
	public UnsubscribeMessage(String inCcy1, String inCcy2, String inPeriod)
	{
		super( inCcy1, inCcy2, inPeriod );
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
