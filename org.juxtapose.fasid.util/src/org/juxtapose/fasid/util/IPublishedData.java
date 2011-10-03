package org.juxtapose.fasid.util;

import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.producer.IDataProducer;


public interface IPublishedData {

	public IDataProducer getProducer();
	
	public DataType<?> getValue( int inKey );
	public DataType<?> getDeltaValue( int inKey );
}
