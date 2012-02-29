package org.juxtapose.fxtradingsystem.ordermanager;

import org.juxtapose.fasid.util.subscriber.DataSequencer;

public class RFQContext
{
	public final DataSequencer sequencer;
	public final RFQProducer producer;
	
	public RFQContext( DataSequencer inSequencer, RFQProducer inProducer )
	{
		sequencer = inSequencer;
		producer = inProducer;
	}
}
