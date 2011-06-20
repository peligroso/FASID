package org.juxtapose.fasid.util.data;

import java.math.BigDecimal;

public class DataTypeBigDecimal extends DataType<BigDecimal>{

	public DataTypeBigDecimal(BigDecimal inValue) 
	{
		super( inValue );
	}

	@Override
	public BigDecimal get() {
		return m_value;
	}

}
