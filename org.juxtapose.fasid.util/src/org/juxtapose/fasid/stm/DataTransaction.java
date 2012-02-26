package org.juxtapose.fasid.stm;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.data.DataTypeRef;

public abstract class DataTransaction extends STMTransaction
{

	public DataTransaction( IDataKey inDataKey, IDataProducer inProducer )
	{
		super( inDataKey, inProducer, 0, 0 );
	}

	public void updateReferenceValue( Integer inKey, DataTypeRef inDataTypeRef )
	{
		throw new IllegalAccessError("Cannot update reference value from DataTransaction ");
	}
	
	public void addReference( Integer inKey, DataTypeRef inDataRef )
	{
		throw new IllegalAccessError("Cannot add reference value from DataTransaction ");
	}

}
