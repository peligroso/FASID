package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataTypeRef;

/**
 * @author Pontus Jörgne
 * Dec 30, 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * Class to holds subscription between the referenced publishedData and the data reference.
 */
public class ReferenceLink implements IDataSubscriber
{
	private final Integer hashKey;
	private DataTypeRef ref;
	private final ISTM stm;
	private final DataProducer parentProducer;
	
	private boolean firstUpdate = true;
	
	/**
	 * @param inParent
	 * @param inSTM
	 * @param inHashKey
	 * @param inRef
	 */
	public ReferenceLink( DataProducer inProducer, ISTM inSTM, Integer inHashKey, DataTypeRef inRef )
	{
		stm = inSTM;
		hashKey = inHashKey;
		ref = inRef;
		parentProducer = inProducer;
		stm.subscribeToData( inRef.get(), this );
	}
	
	@Override
	public void updateData(String inKey, final IPublishedData inData, boolean inFirstUpdate)
	{
		//First update is the response to subscribeToData and will not require an update
		if( firstUpdate )
		{ 
			ref = new DataTypeRef( ref.get(), inData );
			firstUpdate = false;
		}
		else
		{
			//Notify producer
			parentProducer.referencedDataUpdated( hashKey, inData );			
		}
	}
	
	public DataTypeRef getRef()
	{
		return ref;
	}
			
	
	public void dispose()
	{
		stm.unsubscribeToData( ref.get(), this );
	}

}

