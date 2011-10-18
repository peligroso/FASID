package org.juxtapose.fasid.stm.osgi;

import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.osgi.service.component.ComponentContext;

/**
 * @author Pontus Jörgne
 * 3 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public abstract class DataProducerService implements IDataProducerService, IDataSubscriber
{
	protected ISTM stm;
	private int ID;
	
	public void activate( ComponentContext inContext )
	{
		Object id = inContext.getProperties().get( "ID" );
		if( id == null )
			throw new NullPointerException("ID property is missing from data producer service");
		
		init( (Integer)id );
	}
	
	public void bindSTM( ISTM inSTM )
	{
		stm = inSTM;
	}
	
	
	protected void init( int inID )
	{
		ID = inID;
		
		stm.registerProducer( this, Status.OK );
		
		stm.subscribeToData(STMUtil.PRODUCER_SERVICE_KEY, this);
	}

	@Override
	public Integer getServiceId()
	{
		return ID;
	}
}
