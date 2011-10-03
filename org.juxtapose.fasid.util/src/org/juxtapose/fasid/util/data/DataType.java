package org.juxtapose.fasid.util.data;

public abstract class DataType<T> {
	
	final T m_value;
	
	DataType( T inValue )
	{
		m_value = inValue;
	}
	
	public T get()
	{
		return m_value;
	}
	
	public String toString()
	{
		return get().toString();
	}

}
