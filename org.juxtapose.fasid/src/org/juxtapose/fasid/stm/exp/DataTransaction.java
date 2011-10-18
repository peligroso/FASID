package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.data.DataType;

public abstract class DataTransaction extends Transaction
{

	protected DataTransaction(String inDataKey)
	{
		super(inDataKey);
	}
	
	public void addValue( Integer inKey, DataType<?> inData )
	{
		super.addValue(inKey, inData);
	}

	public void removeValue( Integer inKey )throws Exception
	{
		super.removeValue( inKey );
	}

}
