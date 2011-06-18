package org.juxtapose.bundle.stmtest;

import org.juxtapose.fasid.core.stm.STM;
import org.juxtapose.fasid.core.stm.Transaction;

public class TransactionCommit 
{
	public static void main( String... args )
	{
		STM stm = new STM();
		
		
		stm.commit( new Transaction( "datakey" ){
			@Override
			public void execute() 
			{
				
			}
			
		});
	}
	
	
}
