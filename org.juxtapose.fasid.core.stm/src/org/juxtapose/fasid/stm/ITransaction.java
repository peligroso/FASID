package org.juxtapose.fasid.stm;

import java.util.HashMap;

import org.juxtapose.fasid.util.data.DataType;

public interface ITransaction {

	abstract HashMap<String, DataType<?>> getStateInstruction();
	abstract String getDataKey();
	
	public void execute();
}
