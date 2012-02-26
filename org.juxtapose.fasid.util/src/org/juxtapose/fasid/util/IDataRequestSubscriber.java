package org.juxtapose.fasid.util;

import org.juxtapose.fasid.producer.IDataKey;

public interface IDataRequestSubscriber extends IDataSubscriber
{
	public void deliverKey( IDataKey inDataKey, Long inTag );
	public void queryNotAvailible( Long inTag );
}
