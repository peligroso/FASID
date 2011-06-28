package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.util.data.DataType;

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
 */
public abstract class Transaction
{
	private String m_dataKey;	
	private IPersistentMap<String, DataType<?>> m_stateInstruction;
	
	private IPersistentMap<String, DataType<?>> m_deltaState = PersistentHashMap.emptyMap();
	
	/**
	 * @param inDataKey
	 */
	protected Transaction( String inDataKey ) 
	{
		m_dataKey = inDataKey;
	}
	
	protected void putInitDataState( IPersistentMap<String, DataType<?>> inMap )
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
	public void addValue( String inKey, DataType<?> inData )
	{
		assert validateStack() : "Transaction.addValue was not from called from within a STM commit as required";
		m_stateInstruction = m_stateInstruction.assoc( inKey, inData );
		
		m_deltaState = m_deltaState.assoc(inKey, inData);
		
	}

	public void removeValue( String inKey )
	{
		assert validateStack() : "Transaction.removeValue was not from called from within a STM commit as required";
	}
	
	/**
	 * @return
	 */
	protected IPersistentMap<String, DataType<?>> getStateInstruction()
	{
		return m_stateInstruction;
	}
	
	/**
	 * @return
	 */
	protected IPersistentMap<String, DataType<?>> getDeltaState()
	{
		return m_stateInstruction;
	}
	
	/**
	 * @return
	 */
	protected String getDataKey()
	{
		return m_dataKey;
	}
}
