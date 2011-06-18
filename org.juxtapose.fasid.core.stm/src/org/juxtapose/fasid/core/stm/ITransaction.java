package org.juxtapose.fasid.core.stm;

import java.util.HashMap;

import org.juxtapose.fasid.core.util.data.DataType;

public interface ITransaction {

	abstract HashMap<String, DataType<?>> getStateInstruction();
	abstract String getDataKey();
	
	public void execute();
}
