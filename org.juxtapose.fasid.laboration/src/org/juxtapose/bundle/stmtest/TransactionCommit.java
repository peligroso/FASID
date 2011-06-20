package org.juxtapose.bundle.stmtest;

import org.juxtapose.fasid.stm.STM;
import org.juxtapose.fasid.stm.Transaction;

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
