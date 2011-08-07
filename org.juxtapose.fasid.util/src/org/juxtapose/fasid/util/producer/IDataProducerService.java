package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

public interface IDataProducerService
{
	public String getServiceId();
	/**
	 * @param inQuery query for key
	 * @return datakey or null if no datakey can be created from query
	 */
	public IDataKey getDataKey( HashMap<String, String> inQuery );
	public IDataProducer getDataProducer( IDataKey inDataKey );

}
