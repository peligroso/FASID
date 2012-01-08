package org.juxtapose.fxtradingsystem.lpmockup;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LPMockup
{
	ThreadPoolExecutor exec = new ThreadPoolExecutor( 1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
	
	HashMap<String, Instrument> keyToInstrument = new HashMap<String, Instrument>();
	
	public LPMockup()
	{
		
	}
	
	public void subscribeToInstrument( final String inCcy1, final String inCcy2, final String inPeriod, final ILPListener inListener )
	{
		post( new Runnable()
		{
			@Override
			public void run()
			{
				Instrument inst = new Instrument( inCcy1, inCcy2, inPeriod );
				Instrument saved = keyToInstrument.get( inst.getKey() );

				if( saved != null )
					inst = saved;

				inst.addSubscriber( inListener );
			}
		});
	}
	
	public void post( Runnable inRunnable )
	{
		exec.execute( inRunnable );
	}
}

