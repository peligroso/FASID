package org.juxtapose.fasid.util;

import java.util.concurrent.atomic.AtomicLong;

public class SequentialDataSubscriber implements IDataSubscriber
{

	AtomicLong sequenceId = new AtomicLong(0);
	
	@Override
	public void updateData(String inKey, IPublishedData inData, boolean inFirstUpdate)
	{
		if( sequenceId.compareAndSet( 0, inData.getSequenceID() ))
		{
			
		}
		else
		{
			while(  sequenceId.compareAndSet( inData.getSequenceID()-1, inData.getSequenceID() ));;
		}
	}

}
