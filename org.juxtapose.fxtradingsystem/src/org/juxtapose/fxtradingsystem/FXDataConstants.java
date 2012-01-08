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
	public static final int FIELD_DECIMALS = BASE+5;
	public static final int FIELD_PIP = BASE+6;
	public static final int FIELD_BASE_CCY = BASE+7;
	public static final int FIELD_QUOTE_CCY = BASE+8;
	
	public static final int FIELD_EUR = BASE+9;
	public static final int FIELD_USD = BASE+10;
	public static final int FIELD_GBP = BASE+11;
	public static final int FIELD_AUD = BASE+12;
	public static final int FIELD_CHF = BASE+13;
	public static final int FIELD_NZD = BASE+14;
	public static final int FIELD_JPY = BASE+15;
	public static final int FIELD_NOK = BASE+16;
	public static final int FIELD_SEK = BASE+17;
	public static final int FIELD_DKK = BASE+18;
	public static final int FIELD_TRY = BASE+19;
	public static final int FIELD_RUB = BASE+20;
	public static final int FIELD_CAD = BASE+21;
	public static final int FIELD_MXN = BASE+22;
	
	public static int getBase()
	{
		return BASE+23;
	}
}
