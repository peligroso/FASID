package org.juxtapose.fasid.stm;

import java.util.HashMap;

import org.juxtapose.fasid.util.data.DataType;


/**
 * @author Pontus
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
	private HashMap<String, DataType<?>> m_stateInstruction = new HashMap<String, DataType<?>>();
	
	/**
	 * @param inDataKey
	 */
	public Transaction( String inDataKey ) 
	{
		m_dataKey = inDataKey;
	}
	
	public abstract void execute();
	
	/**
	 * @param inKey
	 * @param inData
	 */
	public void addValue( String inKey, DataType<?> inData )
	{
		m_stateInstruction.put( inKey, inData );
	}

	public void removeValue( String inKey )
	{
		
	}
	
	/**
	 * @return
	 */
	protected HashMap<String, DataType<?>> getStateInstruction()
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
