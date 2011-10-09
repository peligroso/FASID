package org.juxtapose.fasid.producer.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public interface IExecutor extends ExecutorService
{
	public void execute( final Runnable inRunnable, final String inSequenceKey );
	public void execute( final Runnable inRunnable, final ReentrantLock inSequenceLock );
}
