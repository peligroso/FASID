package org.juxtapose.fasid.util.data;

import java.math.BigDecimal;

public class DataTypeBigDecimal extends DataType<BigDecimal>{

	public DataTypeBigDecimal(BigDecimal inValue) 
	{
		super( inValue );
	}
	
	public DataTypeBigDecimal( double inValue) 
	{
		super( new BigDecimal( inValue ) );
	}


}
