package org.juxtapose.fasid.stm.osgi;

import org.juxtapose.fasid.stm.impl.NonBlockingSTM;
import org.osgi.service.component.ComponentContext;

public class STMActivator extends NonBlockingSTM
{
	public void activate( ComponentContext inContext )
	{
		init();
	}
}
