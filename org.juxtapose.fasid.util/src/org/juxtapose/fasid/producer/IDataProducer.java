package org.juxtapose.fasid.producer;

import org.juxtapose.fasid.stm.ReferenceLink;
import org.juxtapose.fasid.util.IPublishedData;

public interface IDataProducer
{
	public void init();
	public void dispose();
	
	public void addDataReferences( Integer inFieldKey, ReferenceLink inLink );
	public ReferenceLink removeReferenceLink( Integer inField );
	public void referencedDataUpdated( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData );
}
