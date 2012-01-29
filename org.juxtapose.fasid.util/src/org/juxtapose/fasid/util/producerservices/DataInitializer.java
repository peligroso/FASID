package org.juxtapose.fasid.util.producerservices;

import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.impl.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;

/**
 * @author Pontus Jörgne
 * Jan 7, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * Class to initiate subscription and hence creation of predefined data, effectively making them available throughout the life of the producer.
 * Running during startup in single thread context and does not need sync.
 * DataInitializer should only be used to initialize services.
 */
public class DataInitializer implements IDataSubscriber
{
	private final HashMap<String, IDataKey> keys = new HashMap<String, IDataKey>();
	private final ISTM stm;
	private volatile boolean allOK = false;
	private final IDataInitializerListener listener;
	
	public DataInitializer( ISTM inSTM, IDataInitializerListener inListener )
	{
		stm = inSTM;
		listener = inListener;
	}
	
	public DataInitializer( ISTM inSTM, IDataInitializerListener inListener, IDataKey... inKeys )
	{
		stm = inSTM;
		listener = inListener;
		
		for( IDataKey key : inKeys )
		{
			addDataKey( key );
		}
	}
	
	public void addDataKey( IDataKey inKey )
	{
		keys.put( inKey.getKey(), inKey );
	}
	
	public void init()
	{
		for( IDataKey key : keys.values() )
		{
			stm.subscribeToData( key, this );
		}
	}
	@Override
	public void updateData(String inKey, IPublishedData inData, boolean inFirstUpdate)
	{
		if( allOK )
			return;
		
		synchronized( keys )
		{
			if( inData.getStatus() == Status.OK )
			{
				keys.remove( inKey );
			}
			if( keys.isEmpty() )
			{
				allOK = true;
			}
		}
		
		if( allOK )
		{
			listener.dataInitialized();
		}
	}

}
