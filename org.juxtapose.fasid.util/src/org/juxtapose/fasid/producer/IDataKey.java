package org.juxtapose.fasid.producer;

public interface IDataKey
{
	public String getKey( );
	public String getValue( Integer inKey );
	public Integer getService();
	public String getType( );
}
