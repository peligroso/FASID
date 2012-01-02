package org.juxtapose.fxtradingsystem.priceengine;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;

public class PriceEngineDataConstants extends FXDataConstants
{
	public static final int BASE = FXDataConstants.getBase();
	public static final int TYPE = BASE+1;
	
	public static final String STATE_TYPE_CCYMODEL = "CCY_MODEL";
	public static final String STATE_TYPE_PRICE = "PRICE";
	public static final String STATE_TYPE_CCY = "CCY";
	
	public static final String EUR = "EUR";
	public static final IDataKey CCY_EUR_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, EUR );
	public static final String USD = "USD";
	public static final IDataKey CCY_USD_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, USD );
	public static final String GBP = "GBP";
	public static final IDataKey CCY_GBP_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, GBP );
	public static final String AUD = "AUD";
	public static final IDataKey CCY_AUD_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, AUD );
	public static final String CHF = "CHF";
	public static final IDataKey CCY_CHF_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, CHF );
	public static final String NZD = "NZD";
	public static final IDataKey CCY_NZD_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, NZD );
	public static final String JPY = "JPY";
	public static final IDataKey CCY_JPY_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, JPY );
	public static final String NOK = "NOK";
	public static final IDataKey CCY_NOK_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, NOK );
	public static final String SEK = "SEK";
	public static final IDataKey CCY_SEK_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, SEK );
	public static final String DKK = "DKK";
	public static final IDataKey CCY_DKK_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, DKK );
	public static final String TRY = "TRY";
	public static final IDataKey CCY_TRY_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, TRY );
	public static final String RUB = "RUB";
	public static final IDataKey CCY_RUB_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, RUB );
	public static final String CAD = "CAD";
	public static final IDataKey CCY_CAD_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, CAD );
	public static final String MXN = "MXN";
	public static final IDataKey CCY_MXN_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCY, MXN );
	
	
	
	
	public static final IDataKey CCY_MODEL_KEY = ProducerUtil.createDataKey( FXProducerServiceConstants.PRICE_ENGINE, STATE_TYPE_CCYMODEL );
}
