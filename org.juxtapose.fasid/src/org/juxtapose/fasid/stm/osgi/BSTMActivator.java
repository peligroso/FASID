package org.juxtapose.fasid.stm.osgi;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.juxtapose.fasid.producer.executor.Executor;
import org.juxtapose.fasid.stm.impl.BlockingSTM;
import org.osgi.service.component.ComponentContext;

public class BSTMActivator extends BlockingSTM
{
	public void activate( ComponentContext inContext )
	{
		init( new Executor( 10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() ));
	}

}
