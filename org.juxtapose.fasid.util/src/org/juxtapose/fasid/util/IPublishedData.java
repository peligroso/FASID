package org.juxtapose.fasid.util;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.data.DataType;


public interface IPublishedData {

	public IDataProducer getProducer();
	
	public DataType<?> getValue( int inKey );
	public DataType<?> getDeltaValue( int inKey );
}
