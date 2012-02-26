package org.juxtapose.fasid.util;

import org.juxtapose.fasid.producer.IDataKey;



public interface IDataSubscriber
{
	public void updateData( IDataKey inKey, IPublishedData inData, boolean inFirstUpdate );
}
