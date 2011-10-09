package org.juxtapose.fasid.producer;

import java.util.HashMap;

public interface IDataProducerService
{
	public Integer getServiceId();
	/**
	 * @param inQuery query for key
	 * @return datakey or null if no datakey can be created from query
	 */
	public IDataKey getDataKey( HashMap<Integer, String> inQuery );
	public IDataProducer getDataProducer( IDataKey inDataKey );

}
