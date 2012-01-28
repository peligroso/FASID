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
	
	/**
	 * @param inDataKey
	 * @param inDataProducer the producer that creates the transaction and is responsable for updating the data should always include itself in the method call for validation purpose, e.g. new DataTransaction(dataKey, <b>this</b> ) 
	 */
	protected DataTransaction(String inDataKey, IDataProducer inDataProducer )
	{
		super(inDataKey, inDataProducer );
	}
	
	public void putValue( Integer inKey, DataType<?> inData )
	{
		super.putValue(inKey, inData);
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
	
	public DataType<?> get( Integer inFieldKey )
	{
		return super.get( inFieldKey );
	}

}
