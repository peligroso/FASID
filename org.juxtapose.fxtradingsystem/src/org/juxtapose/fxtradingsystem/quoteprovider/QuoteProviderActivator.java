package org.juxtapose.fxtradingsystem.quoteprovider;

import org.osgi.service.component.ComponentContext;

public abstract class QuoteProviderActivator
{
	public static String NAME = "NAME";
	
	public void activate( ComponentContext inContext )
	{
		Object temp = inContext.getProperties().get( NAME );
		
		String name = null;
		
		if( temp != null )
		{
			name = (String)temp;
		}
		
		init( name );
	}
	
	public abstract void init( String inName );
}
