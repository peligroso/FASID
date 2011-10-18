package org.juxtapose.fxtradingsystem.priceengine;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import static org.juxtapose.fasid.util.PrimeNumbers.*;

public class PriceEngineDataConstants extends FXDataConstants
{
	public static final int BASE = 100;
	public static final int TYPE = BASE+1;
	
	
	public static final int EUR = PRIME_50;
	public static final int USD = PRIME_51;
	public static final int GBP = PRIME_52;
	public static final int AUD = PRIME_53;
	public static final int CHF = PRIME_54;
	public static final int NZD = PRIME_55;
	public static final int JPY = PRIME_56;
	public static final int NOK = PRIME_57;
	public static final int SEK = PRIME_58;
	public static final int DKK = PRIME_59;
	public static final int TRY = PRIME_60;
	public static final int RUB = PRIME_61;
	public static final int CAD = PRIME_62;
	public static final int MXN = PRIME_63;
	
	
	
	public static final String STATE_TYPE_CCYMODEL = "CCY_MODEL";
	public static final String STATE_TYPE_PRICE = "PRICE";
	
	public static final IDataKey CCY_MODEL_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCYMODEL );
}
