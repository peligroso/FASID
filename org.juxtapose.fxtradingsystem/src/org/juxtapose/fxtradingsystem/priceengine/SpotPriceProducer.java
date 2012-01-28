package org.juxtapose.fxtradingsystem.priceengine;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_EUR;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_SEK;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.marketdata.MarketDataConstants;

/**
 * @author Pontus Jörgne
 * 6 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class SpotPriceProducer extends DataProducer implements IDataSubscriber
{
	final String ccy1;
	final String ccy2;
	
	IDataKey marketDataKey;
	
	/**
	 * @param inKey
	 * @param inCcy1
	 * @param inCcy2
	 * @param inSTM
	 */
	public SpotPriceProducer( IDataKey inKey, String inCcy1, String inCcy2, ISTM inSTM )
	{
		super( inKey, inSTM );
		ccy1 = inCcy1;
		ccy2 = inCcy2;
	}
	
	public void subscribe()
	{
		HashMap<Integer, String> query = new HashMap<Integer, String>();
		query.put( MarketDataConstants.FIELD_TYPE, MarketDataConstants.STATE_TYPE_INSTRUMENT );
		query.put( MarketDataConstants.FIELD_CCY1, ccy1 );
		query.put( MarketDataConstants.FIELD_CCY2, ccy2 );
		query.put( MarketDataConstants.FIELD_PERIOD, FXDataConstants.STATE_PERIOD_SP );
		query.put( MarketDataConstants.FIELD_SOURCE, "REUTERS" );
		
		PriceEngineUtil.getPriceQuery( STATE_EUR, STATE_SEK );
		IDataKey dataKey = stm.getDataKey( FXProducerServiceConstants.MARKET_DATA, query );
		if( dataKey == null )
		{
			setStatus( Status.ERROR );
			stm.logError( "could not retrieve datakey from market data" );
			return;
		}
		
		stm.subscribeToData( dataKey, this );
	}
	
	public void start()
	{
		subscribe();
	}
	
	/**
	 * @param inRand
	 * @param inTransaction
	 */
	public void addPriceUpdate( final Random inRand, DataTransaction inTransaction )
	{
		DataTypeBigDecimal bid = new DataTypeBigDecimal( inRand.nextDouble() );
		DataTypeBigDecimal ask = new DataTypeBigDecimal( inRand.nextDouble() );
		
		
		final DataTypeBigDecimal spread = new DataTypeBigDecimal( ask.get().subtract( bid.get() ) );
		
		inTransaction.putValue(FXDataConstants.FIELD_BID, bid );
		inTransaction.putValue(FXDataConstants.FIELD_ASK, ask );
		inTransaction.putValue(FXDataConstants.FIELD_SPREAD, spread );
	}


	@Override
	public void stop()
	{
		stm.unsubscribeToData( marketDataKey, this );
	}

	@Override
	public void updateData(String inKey, final IPublishedData inData, boolean inFirstUpdate)
	{
		if( inData.getStatus() == Status.OK )
		{
			stm.commit( new DataTransaction( dataKey.getKey(), SpotPriceProducer.this )
			{
				@Override
				public void execute()
				{
					if( getStatus() == Status.ON_REQUEST)
						setStatus( Status.OK );
					
					DataType<?> bid = inData.getValue( FXDataConstants.FIELD_BID );
					DataType<?> ask = inData.getValue( FXDataConstants.FIELD_ASK );

					BigDecimal bidVal = (BigDecimal)bid.get();
					BigDecimal askVal = (BigDecimal)ask.get();

					final DataTypeBigDecimal spread = new DataTypeBigDecimal( askVal.subtract( bidVal ) );
					
					putValue(FXDataConstants.FIELD_BID, bid );
					putValue(FXDataConstants.FIELD_ASK, ask );
					putValue(FXDataConstants.FIELD_SPREAD, spread );
				}
			});
		}
	}
}
