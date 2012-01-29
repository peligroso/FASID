package org.juxtapose.fasid.stm.osgi;

import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.KeyConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.producerservices.DataInitializer;
import org.juxtapose.fasid.util.producerservices.IDataInitializerListener;
import org.osgi.service.component.ComponentContext;

/**
 * @author Pontus Jörgne
 * 3 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public abstract class DataProducerService implements IDataProducerService, IDataSubscriber, IDataInitializerListener
{
	protected ISTM stm;
	
	protected DataInitializer initializer;
	
	public void activate( ComponentContext inContext )
	{
		init();
	}
	
	public void bindSTM( ISTM inSTM )
	{
		stm = inSTM;
	}
	
	
	protected void init()
	{
		initializer = createDataInitializer();
		
		if( initializer != null )
		{
			stm.registerProducer( this, Status.INITIALIZING );
			initializer.init();
		}
		else
		{
			stm.registerProducer( this, Status.OK );
			stm.subscribeToData( KeyConstants.PRODUCER_SERVICE_KEY, this);
		}
			
	}
	
	public DataInitializer createDataInitializer( )
	{
		return null;
	}
	
	public void dataInitialized()
	{
		stm.registerProducer( this, Status.OK );
		stm.subscribeToData( KeyConstants.PRODUCER_SERVICE_KEY, this);
	}

	@Override
	public abstract Integer getServiceId();
}
