package org.juxtapose.fasid.stm.impl;

import java.util.HashMap;

import org.juxtapose.fasid.util.data.DataType;

/**
 * @author Pontus J�rgne
 * 28 jun 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public interface ITransaction {

	abstract HashMap<String, DataType<?>> getStateInstruction();
	abstract String getDataKey();
	
	public void execute();
}
