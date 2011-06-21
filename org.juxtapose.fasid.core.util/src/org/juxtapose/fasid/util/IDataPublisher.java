package org.juxtapose.fasid.util;

import java.util.HashMap;

public interface IDataPublisher {

	public String getKey( HashMap<String, String> inQuery );
	public void subscribe( String inKey );
	public void unSubscribe( String inKey );
}
