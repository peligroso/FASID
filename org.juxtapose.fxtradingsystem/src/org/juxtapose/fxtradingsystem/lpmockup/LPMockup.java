package org.juxtapose.fxtradingsystem.lpmockup;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LPMockup
{
	ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor( 1 );
	
	HashMap<String, Instrument> keyToInstrument = new HashMap<String, Instrument>();
	
	Random rand = new Random();
	
	public LPMockup()
	{
		
	}
	
	public void start()
	{
		Thread updateThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for( String key : keyToInstrument.keySet() )
				{
					Instrument inst = keyToInstrument.get( key );
					Instrument newInstrument = new Instrument( inst.ccy1, inst.ccy2, inst.period );
					newInstrument.addPriceUpdate( rand );
					
					keyToInstrument.put( key, newInstrument );
					
					try
					{
						Thread.sleep( rand.nextInt() * 1000 );
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
			}
		});
		
		updateThread.start();
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

			}
		});
	}
	
	public void post( Runnable inRunnable )
	{
		exec.execute( inRunnable );
	}
}

