package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

public abstract class DataTransaction extends Transaction
{

	/**
	 * @param inDataKey
	 * Constructor is set private.DataTransaction(String inDataKey, IDataProducer inDataProducer ) should be used to ensure the update comes from a valid producer
	 */
	private DataTransaction(String inDataKey)
	{
		super(inDataKey);
	}
	
	protected DataTransaction(String inDataKey, IDataProducer inDataProducer )
	{
		super(inDataKey, inDataProducer );
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
	
	public void setStatus( Status inStatus )
	{
		super.setStatus( inStatus );
	}

}
