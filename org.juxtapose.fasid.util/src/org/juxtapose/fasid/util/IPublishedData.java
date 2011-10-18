package org.juxtapose.fasid.util;

import java.util.Map;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;


public interface IPublishedData {

	public IDataProducer getProducer();
	
	public DataType<?> getValue( int inKey );
	public DataType<?> getDeltaValue( int inKey );
	public IPublishedData addSubscriber(IDataSubscriber inSubscriber);
	public IPublishedData removeSubscriber(IDataSubscriber inSubscriber);
	public boolean hasSubscribers();
	public IPersistentMap<Integer, DataType<?>> getDataMap();
	public IPublishedData setUpdatedData(IPersistentMap<Integer, DataType<?>> stateInstruction,Map<Integer, DataType<?>> deltaState);
	public void updateSubscribers(String dataKey);
	public IPublishedData putDataValue( Integer inKey, DataType<?> inValue )throws Exception;
}
