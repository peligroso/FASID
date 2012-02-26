package org.juxtapose.fasid.stm;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeRef;

/**
 * @author Pontus Jörgne
 * Dec 30, 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * Class to holds subscription between the referenced publishedData and the data reference.
 */
public class ReferenceLink extends DataProducerDependencyController implements IDataSubscriber
{
	private final Integer hashKey;
	private final DataTypeRef ref;
	
	/**
	 * @param inParent
	 * @param inSTM
	 * @param inHashKey
	 * @param inRef
	 */
	public ReferenceLink( IDataProducer inProducer, ISTM inSTM, Integer inHashKey, DataTypeRef inRef )
	{
		super( inProducer, inSTM, inRef.get() );
		hashKey = inHashKey;
		ref = inRef;
	}
	
	
	@Override
	public void updateData( IDataKey inKey, final IPublishedData inData, boolean inFirstUpdate )
	{
		//Notify producer about delivered Data. ON_Request Data is not interesting
		if( inData.getStatus() != Status.ON_REQUEST )
		{
			parentProducer.referencedDataUpdated( hashKey, this, inData );
		}
	}
	
	public DataTypeRef getRef()
	{
		return ref;
	}
			
}

