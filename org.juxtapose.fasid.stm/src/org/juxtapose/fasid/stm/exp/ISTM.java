package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducerService;

public interface ISTM
{
	public void registerProducer( final IDataProducerService inProducerService, final Status initState );
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber );
	public void unsubscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber );
	public void commit( Transaction inTransaction );
}
