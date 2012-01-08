package org.juxtapose.fasid.stm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeNull;
import org.juxtapose.fasid.util.data.DataTypeRef;

import com.trifork.clj_ds.IPersistentMap;


/**
 * @author Pontus J�rgne
 * 28 jun 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 * 
 * Transaction represents a set of instructions that will take a published data from �
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
	
	private Map<Integer, DataTypeRef> addedDataReferences = new HashMap<Integer, DataTypeRef>();
	private List<Integer> removedDataReferences = new ArrayList<Integer>();
	
	private IDataProducer m_producer = null;
	
	private Status status;
	
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
	protected void putInitDataState( IPersistentMap<Integer, DataType<?>> inMap, Status inStatus )
	{
		m_stateInstruction = inMap;
		status = inStatus;
	}
	
	
	public abstract void execute();
	
	/**
	 * @param inKey
	 * @param inData
	 */
	public void addValue( Integer inKey, DataType<?> inData )
	{
		assert STMUtil.validateTransactionStack() : "Transaction.addValue was not from called from within a STM commit as required";
		assert !( inData instanceof DataTypeRef ) : "Reference values should be added via addReference method";
		
		m_stateInstruction = m_stateInstruction.assoc( inKey, inData );
		m_deltaState.put(inKey, inData);
	}
	
	public void updateReferenceValue( Integer inKey, DataTypeRef inDataTypeRef )
	{
		assert STMUtil.validateTransactionStack() : "Transaction.updateReferenceValue was not from called from within a STM commit as required";
		assert m_stateInstruction.valAt( inKey) != null : "Tried to update non existing Reference";
		assert m_stateInstruction.valAt( inKey ) instanceof DataTypeRef : "Tried to update Reference that was not of reference type";
		
		m_stateInstruction = m_stateInstruction.assoc( inKey, inDataTypeRef );
		m_deltaState.put(inKey, inDataTypeRef);
	}
	
	/**
	 * @param inKey
	 * @param inDataRef
	 */
	public void addReference( Integer inKey, DataTypeRef inDataRef )
	{
		assert STMUtil.validateTransactionStack() : "Transaction.addValue was not from called from within a STM commit as required";
		
		addedDataReferences.put( inKey, inDataRef );
		
		m_stateInstruction = m_stateInstruction.assoc( inKey, inDataRef );
		m_deltaState.put(inKey, inDataRef);
	}

	/**
	 * @param inKey
	 * @throws Exception
	 */
	public void removeValue( Integer inKey )throws Exception
	{
		assert STMUtil.validateTransactionStack() : "Transaction.removeValue was not from called from within a STM commit as required";
		assert m_deltaState.containsKey( inKey ) : "Transaction may not add and remove the same field value: "+inKey;
		
		m_stateInstruction = m_stateInstruction.without( inKey );
		
		DataType<?> removedData = m_deltaState.put(inKey, new DataTypeNull( null ));
		
		if( removedData instanceof DataTypeRef )
		{
			removedDataReferences.add( inKey );
		}
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
	public Map<Integer, DataTypeRef> getAddedReferences()
	{
		return addedDataReferences;
	}
	
	/**
	 * @return
	 */
	public List<Integer> getRemovedReferences()
	{
		return removedDataReferences;
	}
	
	public void setStatus( Status inStatus )
	{
		status = inStatus;
	}
	
	public Status getStatus()
	{
		return status;
	}
}
