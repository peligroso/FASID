package org.juxtapose.fasid.util;

import java.util.Set;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.data.DataType;

import com.trifork.clj_ds.IPersistentMap;


public interface IPublishedData {

	public IDataProducer getProducer();
	
	public DataType<?> getValue( int inKey );
	public boolean isDeltaValue( int inKey );
	public IPublishedData addSubscriber(IDataSubscriber inSubscriber);
	public IPublishedData removeSubscriber(IDataSubscriber inSubscriber);
	public boolean hasSubscribers();
	public IPersistentMap<Integer, DataType<?>> getDataMap();
	public IPublishedData setUpdatedData(IPersistentMap<Integer, DataType<?>> stateInstruction, Set<Integer> deltaState, Status inStatus, boolean completeUpdate);
	public void updateSubscribers(IDataKey dataKey);
	public IPublishedData putDataValue( Integer inKey, DataType<?> inValue )throws Exception;
	public Status getStatus();
	public long getSequenceID();
	public boolean isCompleteVersion();
	public Set<Integer> getDeltaSet();
}
