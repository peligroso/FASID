package org.juxtapose.fasid.util.producer;

public interface IDataKey
{
	public String getKey( );
	public String getValue( String inKey );
	public Integer getService();
}
