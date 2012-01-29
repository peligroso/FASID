package org.juxtapose.fasid.stm;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.executor.IExecutor;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;

public interface ISTM extends IExecutor
{
	public void registerProducer( final IDataProducerService inProducerService, final Status initState );
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber );
	public void unsubscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber );
	public void commit( DataTransaction inTransaction );
	public IDataKey getDataKey(Integer inProducerService, HashMap<Integer, String> inQuery);
	public void logInfo( String inMessage );
	public void logError( String inMessage );
	public void logError( String inMessage, Throwable inThrowable );
	public void logWarning( String inMessage );
	public void logDebug( String inMessage );
	public IPublishedData createEmptyData( Status inStatus, IDataProducer inProducer, IDataSubscriber inSubscriber );
	public IPublishedData getData( String inKey );
}
