package org.juxtapose.fasid.producer;

public interface IDataKey
{
	public String getKey( );
	public String getValue( String inKey );
	public Integer getService();
}
