package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

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
	
	public void addReference( Integer inKey, DataTypeRef inDataRef )
	{
		super.addReference( inKey, inDataRef );
	}

}
