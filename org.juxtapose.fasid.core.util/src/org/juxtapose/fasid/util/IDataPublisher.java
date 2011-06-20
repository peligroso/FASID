package org.juxtapose.fasid.util;

import java.util.HashMap;

public interface IDataPublisher {

	public String subscribe( HashMap<String, String> inQuery );
	public Status unSubscribe( String inKey );
}
