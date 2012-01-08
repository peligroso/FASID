package org.juxtapose.fasid.producer;

import java.util.List;
import java.util.Map;

import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataTypeRef;

public interface IDataProducer
{
	public void start();
	public void stop();
	
	public void initDataReferences( Map< Integer, DataTypeRef > inDataReferences );
	public void disposeReferenceLink( Integer inField );
	public void disposeReferenceLinks( List< Integer > inReferenceFields );
	public void disposeAllReferenceLinks( );
	public void referencedDataUpdated( Integer inFieldKey, IPublishedData inData );
}
