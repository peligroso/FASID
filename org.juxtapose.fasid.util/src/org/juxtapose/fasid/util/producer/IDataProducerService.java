package org.juxtapose.fasid.util.producer;

import java.util.HashMap;

public interface IDataProducerService
{
	public int getServiceId();
	public String getKey( HashMap<String, String> inQuery );
	public void subscribe( String inKey );
	public void unSubscribe( String inKey );
}
