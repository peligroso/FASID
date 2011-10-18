package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;

/**
 * @author Pontus J�rgne
 * 17 okt 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public interface IPublishedDataFactory
{
	public IPublishedData createData( Status inStatus, IDataProducer inProducer );
	public IPublishedData createData( Status inStatus, IDataProducer inProducer, IDataSubscriber inSubscriber );
}
