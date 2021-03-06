package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;
import java.util.Map;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

/**
 * @author Pontus J�rgne
 * 17 okt 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public class PersistentDataFactory implements IPublishedDataFactory
{
	/**
	 * @param inStatus
	 * @param inProducer
	 * @return
	 */
	public IPublishedData createData( Status inStatus, IDataProducer inProducer )
	{
		IPersistentMap<Integer, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, new DataTypeString( inStatus.toString()) );
		Map<Integer, DataType<?>> deltaMap = new HashMap<Integer, DataType<?>>();
		IPersistentVector<IDataSubscriber> subscribers = PersistentVector.create( );
		
		return new PublishedData( dataMap, deltaMap, subscribers, inProducer );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.impl.IPublishedDataFactory#createData(org.juxtapose.fasid.util.Status, org.juxtapose.fasid.producer.IDataProducer, org.juxtapose.fasid.util.IDataSubscriber)
	 */
	public IPublishedData createData( Status inStatus, IDataProducer inProducer, IDataSubscriber inSubscriber )
	{
		IPersistentMap<Integer, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, new DataTypeString( inStatus.toString()) );
		Map<Integer, DataType<?>> deltaMap = new HashMap<Integer, DataType<?>>();
		IPersistentVector<IDataSubscriber> subscribers = PersistentVector.create( inSubscriber );
		
		return new PublishedData( dataMap, deltaMap, subscribers, inProducer );
	}
}
