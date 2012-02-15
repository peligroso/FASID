package org.juxtapose.fasid.stm.osgi;

import org.juxtapose.fasid.producer.executor.Executor;
import org.juxtapose.fasid.stm.BlockingSTM;
import org.juxtapose.fasid.stm.IPublishedDataFactory;
import org.osgi.service.component.ComponentContext;

public class BSTMActivator extends BlockingSTM
{
	public static String PROP_DATA_FACTORY_CLASS = "PROP_DATA_FACTORY_CLASS";
	
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
		init( new Executor( 5, 3, 2, 2 ));
	}

}
