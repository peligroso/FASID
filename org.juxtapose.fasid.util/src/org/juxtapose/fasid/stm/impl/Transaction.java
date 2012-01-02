package org.juxtapose.fasid.stm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeNull;
import org.juxtapose.fasid.util.data.DataTypeRef;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.PersistentHashMap;


/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 * 
 * Transaction represents a set of instructions that will take a published data from ¨
 * one state to another in an atomic operation.
 * The commit method will be implemented by the programmer to create the new state instructions
 * Transaction should only exist in a single thread context
 * The data key will be write -locked or CAS referenced during execute
 * 
 * Transactions only live inside one thread and should always be declared anonymous. 
 */
public abstract class Transaction
{
	private String m_dataKey;	
	private IPersistentMap<Integer, DataType<?>> m_stateInstruction;
	
	private Map<Integer, DataType<?>> m_deltaState = new HashMap<Integer, DataType<?>>();
	
	private Map<Integer, DataTypeRef> dataReferences = new HashMap<Integer, DataTypeRef>();
	
	private IDataProducer m_producer = null;
	
	/**
	 * @param inDataKey
	 */
	protected Transaction( String inDataKey ) 
	{
		m_dataKey = inDataKey;
	}
	
	/**
	 * @param inDataKey
	 * @param inProducer
	 */
	protected Transaction( String inDataKey, IDataProducer inProducer ) 
	{
		m_dataKey = inDataKey;
		m_producer = inProducer;
	}
	
	/**
	 * @param inMap
	 */
	protected void putInitDataState( IPersistentMap<Integer, DataType<?>> inMap )
	{
		m_stateInstruction = inMap;
	}
	
	
	public abstract void execute();
	
	private boolean validateStack()
	{
		for (StackTraceElement element : Thread.currentThread().getStackTrace() )
		{
			if( element.getClassName().equals( STM.class.getClass().getName() ) &&
					element.getMethodName().equals( STM.COMMIT_METHOD ) )
				return true;
		}
		
		return false;
	}
	/**
	 * @param inKey
	 * @param inData
	 */
	public void addValue( Integer inKey, DataType<?> inData )
	{
		assert validateStack() : "Transaction.addValue was not from called from within a STM commit as required";
		assert !( inData instanceof DataTypeRef ) : "Reference values should be added via addReference method";
		
		m_stateInstruction = m_stateInstruction.assoc( inKey, inData );
		m_deltaState.put(inKey, inData);
	}
	
	/**
	 * @param inKey
	 * @param inDataRef
	 */
	public void addReference( Integer inKey, DataTypeRef inDataRef )
	{
		assert validateStack() : "Transaction.addValue was not from called from within a STM commit as required";
		
		dataReferences.put( inKey, inDataRef );
		
		m_stateInstruction = m_stateInstruction.assoc( inKey, inDataRef );
		m_deltaState.put(inKey, inDataRef);
	}

	/**
	 * @param inKey
	 * @throws Exception
	 */
	public void removeValue( Integer inKey )throws Exception
	{
		assert validateStack() : "Transaction.removeValue was not from called from within a STM commit as required";
		m_stateInstruction = m_stateInstruction.without( inKey );
		
		m_deltaState.put(inKey, new DataTypeNull( null ));
	}
	
	/**
	 * @return
	 */
	protected IPersistentMap<Integer, DataType<?>> getStateInstruction()
	{
		return m_stateInstruction;
	}
	
	/**
	 * @return
	 */
	protected Map<Integer, DataType<?>> getDeltaState()
	{
		return m_deltaState;
	}
	
	/**
	 * @return
	 */
	protected String getDataKey()
	{
		return m_dataKey;
	}
	
	public IDataProducer producedBy()
	{
		return m_producer;
	}
	
	/**
	 * @return
	 */
	public Map<Integer, DataTypeRef> getReferences()
	{
		return dataReferences;
	}
}
