package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.stm.impl.Transaction;

public abstract class DataTransaction extends Transaction
{

	protected DataTransaction(String inDataKey)
	{
		super(inDataKey);
	}

}
