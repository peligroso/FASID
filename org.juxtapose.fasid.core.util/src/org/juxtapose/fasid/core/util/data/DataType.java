package org.juxtapose.fasid.core.util.data;

public abstract class DataType<T> {
	
	final T m_value;
	
	DataType( T inValue )
	{
		m_value = inValue;
	}
	
	public abstract T get();

}
