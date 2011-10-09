package org.juxtapose.fasid.producer.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Pontus Jörgne
 * 9 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class Executor extends ThreadPoolExecutor implements IExecutor
{	
	public Executor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue )
	{
		super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	/**
	 * @param inRunnable
	 * @param inSequenceKey
	 */
	public void execute( final Runnable inRunnable, final String inSequenceKey )
	{
		execute( new Runnable(){

			@Override
			public void run()
			{
				synchronized (inSequenceKey.intern())
				{
					inRunnable.run();
				}
			}
			
		});
	}
	
	/**
	 * @param inRunnable
	 * @param inSequenceLock
	 */
	public void execute( final Runnable inRunnable, final ReentrantLock inSequenceLock )
	{
		execute( new Runnable(){

			@Override
			public void run()
			{
				inSequenceLock.lock();
				{
					inRunnable.run();
				}
				inSequenceLock.unlock();
			}
			
		});
	}
}
