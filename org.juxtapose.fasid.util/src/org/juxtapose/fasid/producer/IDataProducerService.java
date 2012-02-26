package org.juxtapose.fasid.producer;

import java.util.HashMap;

import org.juxtapose.fasid.util.IDataRequestSubscriber;

public interface IDataProducerService
{
	public Integer getServiceId();
	/**
	 * @param inQuery query for key
	 * @return datakey or null if no datakey can be created from query
	 */
	public void getDataKey( IDataRequestSubscriber inSubscriber, Long inTag, HashMap<Integer, String> inQuery );
	public IDataProducer getDataProducer( IDataKey inDataKey );

}
