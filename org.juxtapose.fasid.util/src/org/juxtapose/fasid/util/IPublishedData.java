package org.juxtapose.fasid.util;

import java.util.Map;

import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.producer.IDataProducer;

import com.trifork.clj_ds.IPersistentMap;

public interface IPublishedData {

	public IDataProducer getProducer();
	
	public IPersistentMap<Integer, DataType<?>> getDataMap();
	public Map<Integer, DataType<?>> getDeltaMap();
	
	public DataType<?> getValue( int inKey );
}
