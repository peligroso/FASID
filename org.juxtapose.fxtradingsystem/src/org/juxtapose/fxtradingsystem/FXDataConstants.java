package org.juxtapose.fxtradingsystem;

import org.juxtapose.fasid.util.DataConstants;

public class FXDataConstants extends DataConstants
{
	public static final int BASE = DataConstants.getBase();
	
	public static final int FIELD_CCY1 = BASE;
	public static final int FIELD_CCY2 = BASE+1;
	public static final int FIELD_BID = BASE+2;
	public static final int FIELD_ASK = BASE+3;
	public static final int FIELD_SPREAD = BASE+4;
	public static final int FIELD_SEQUENCE = BASE+5;
	public static final int FIELD_DECIMALS = BASE+6;
	public static final int FIELD_PIP = BASE+7;
	public static final int FIELD_BASE_CCY = BASE+8;
	public static final int FIELD_QUOTE_CCY = BASE+9;
	
	public static int getBase()
	{
		return BASE+9;
	}
}
