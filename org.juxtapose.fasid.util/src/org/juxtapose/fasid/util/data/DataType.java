package org.juxtapose.fasid.util.data;

/**
 * @author Pontus Jörgne
 * Dec 30, 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 * @param <T>
 */
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
	
	public boolean equals( DataType<?> inObject )
	{
		return get().equals( inObject.get() );
	}

}
