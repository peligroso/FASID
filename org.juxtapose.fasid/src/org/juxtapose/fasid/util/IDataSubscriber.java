package org.juxtapose.fasid.util;


public interface IDataSubscriber
{
	public void updateData( String inKey, IPublishedData inData, boolean inFirstUpdate );
}
