package org.juxtapose.fxtradingsystem;

import org.juxtapose.fasid.util.DataConstants;

public class FXDataConstants extends DataConstants
{
	public static final int BASE = DataConstants.getBase();
	
	private static int inc = BASE;
	
	public static final int FIELD_CCY1 = new Integer(++inc);
	public static final int FIELD_CCY2 = new Integer(++inc);
	public static final int FIELD_PERIOD = new Integer(++inc);
	public static final int FIELD_BID = new Integer(++inc);
	public static final int FIELD_ASK = new Integer(++inc);
	public static final int FIELD_SPREAD = new Integer(++inc);
	public static final int FIELD_DECIMALS = new Integer(++inc);
	public static final int FIELD_PIP = new Integer(++inc);
	public static final int FIELD_BASE_CCY = new Integer(++inc);
	public static final int FIELD_QUOTE_CCY = new Integer(++inc);
	public static final int FIELD_STATIC_DATA = new Integer(++inc);
	
	public static final int FIELD_EUR = new Integer(++inc);
	public static final int FIELD_USD = new Integer(++inc);
	public static final int FIELD_GBP = new Integer(++inc);
	public static final int FIELD_AUD = new Integer(++inc);
	public static final int FIELD_CHF = new Integer(++inc);
	public static final int FIELD_NZD = new Integer(++inc);
	public static final int FIELD_JPY = new Integer(++inc);
	public static final int FIELD_NOK = new Integer(++inc);
	public static final int FIELD_SEK = new Integer(++inc);
	public static final int FIELD_DKK = new Integer(++inc);
	public static final int FIELD_TRY = new Integer(++inc);
	public static final int FIELD_RUB = new Integer(++inc);
	public static final int FIELD_CAD = new Integer(++inc);
	public static final int FIELD_MXN = new Integer(++inc);
	
	public static String STATE_PERIOD_SP = "SP";
	public static String STATE_PERIOD_1W = "1W";
	public static String STATE_PERIOD_1M = "1M";
	public static String STATE_PERIOD_3M = "3M";
	public static String STATE_PERIOD_6M = "6M";
	public static String STATE_PERIOD_9M = "9M";
	public static String STATE_PERIOD_1Y = "1Y";
	
	static
	{
		inc = new Integer(++inc);
	}
	public static int getBase()
	{
		return inc;
	}
}
