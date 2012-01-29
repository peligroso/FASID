package org.juxtapose.fasid.stm.osgi;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.juxtapose.fasid.producer.executor.Executor;
import org.juxtapose.fasid.stm.IPublishedDataFactory;
import org.juxtapose.fasid.stm.NonBlockingSTM;
import org.osgi.service.component.ComponentContext;

/**
 * @author Pontus Jörgne
 * 17 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class STMActivator extends NonBlockingSTM
{
	public static String PROP_DATA_FACTORY_CLASS = "PROP_DATA_FACTORY_CLASS";
	
	/**
	 * @param inContext
	 */
	public void activate( ComponentContext inContext )
	{
		Object temp = inContext.getProperties().get( PROP_DATA_FACTORY_CLASS );
		
		if( temp != null )
		{
			String classStr = (String)temp;
			try
			{
				Class<?> c = Class.forName( classStr );
				IPublishedDataFactory dataFactory =  (IPublishedDataFactory)c.newInstance();
				
				setDataFactory( dataFactory );
			} 
			catch (ClassNotFoundException e)
			{
				logError( e.getMessage() );
			} 
			catch (InstantiationException e)
			{
				logError( e.getMessage() );
			} 
			catch (IllegalAccessException e)
			{
				logError( e.getMessage() );
			}
		}
		
		init( new Executor( 10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() ));
	}
}
