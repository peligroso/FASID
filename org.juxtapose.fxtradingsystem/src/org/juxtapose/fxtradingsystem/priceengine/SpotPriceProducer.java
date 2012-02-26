package org.juxtapose.fxtradingsystem.priceengine;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_EUR;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_SEK;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.DataProducerDependencyController;
import org.juxtapose.fasid.stm.DataTransaction;
import org.juxtapose.fasid.stm.DependencyTransaction;
import org.juxtapose.fasid.stm.STMTransaction;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.marketdata.MarketDataConstants;

/**
 * @author Pontus Jörgne
 * 6 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public final class SpotPriceProducer extends DataProducer implements IDataRequestSubscriber
{
	final String ccy1;
	final String ccy2;

	final long reutersTag = 0;
	final long bloombergTag = 1;
	
	IDataKey reutersDataKey;
	IDataKey bloombergDataKey;

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

	public void linkStaticData()
	{
		stm.commit( new STMTransaction( dataKey.getKey(), SpotPriceProducer.this, 1, 0 )
		{
			@Override
			public void execute()
			{
				addReference( PriceEngineDataConstants.FIELD_STATIC_DATA, new DataTypeRef( PriceEngineKeyConstants.CCY_SEK_KEY ) );
			}
		});
	}

	public void subscribe()
	{
		HashMap<Integer, String> query = new HashMap<Integer, String>();
		query.put( MarketDataConstants.FIELD_TYPE, MarketDataConstants.STATE_TYPE_INSTRUMENT );
		query.put( MarketDataConstants.FIELD_CCY1, ccy1 );
		query.put( MarketDataConstants.FIELD_CCY2, ccy2 );
		query.put( MarketDataConstants.FIELD_PERIOD, FXDataConstants.STATE_PERIOD_SP );
		query.put( MarketDataConstants.FIELD_SOURCE, "REUTERS" );

		PriceEngineUtil.getSpotPriceQuery( STATE_EUR, STATE_SEK );
		stm.getDataKey( FXProducerServiceConstants.MARKET_DATA, this, reutersTag, query );

		query.put( MarketDataConstants.FIELD_SOURCE, "BLOOMBERG" );

		stm.getDataKey( FXProducerServiceConstants.MARKET_DATA, this, bloombergTag, query );
	}

	public void start()
	{
		linkStaticData();
		subscribe();
	}

	/**
	 * @param inRand
	 * @param inTransaction
	 */
	public void addPriceUpdate( final Random inRand, STMTransaction inTransaction )
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
		stm.unsubscribeToData( reutersDataKey, this );
		stm.unsubscribeToData( bloombergDataKey, this );
	}

	@Override
	public void updateData(String inKey, final IPublishedData inData, boolean inFirstUpdate)
	{
		if( reutersDataKey == null || bloombergDataKey == null )
			return;
		
		if( inData.getStatus() == Status.OK )
		{
			stm.commit( new DataTransaction( dataKey.getKey(), SpotPriceProducer.this )
			{
				@Override
				public void execute()
				{
					DataTypeRef ref = (DataTypeRef)get( PriceEngineDataConstants.FIELD_STATIC_DATA );
					IPublishedData reutData = stm.getData( reutersDataKey.getKey() );
					IPublishedData bloomData = stm.getData( bloombergDataKey.getKey() );

					if( reutData == null || bloomData == null || ref == null )
					{
						dispose();
						return;
					}
					Long dec = PriceEngineUtil.getDecimals( ref.getReferenceData() );
					BigDecimal[] reutBidAsk = PriceEngineUtil.getBidAskFromData( reutData );
					BigDecimal[] bloomBidAsk = PriceEngineUtil.getBidAskFromData( bloomData );

					if( reutBidAsk == null || bloomBidAsk == null || dec == null )
					{
						dispose();
						return;
					}
					if( getStatus() == Status.ON_REQUEST)
						setStatus( Status.OK );

					BigDecimal bid = (reutBidAsk[0].add( bloomBidAsk[0] )).divide( new BigDecimal( 2 ) );
					BigDecimal ask = (reutBidAsk[1].add( bloomBidAsk[1] )).divide( new BigDecimal( 2 ) );

					DataTypeLong timeStamp = (DataTypeLong)inData.getValue( DataConstants.FIELD_TIMESTAMP );

					putValue( MarketDataConstants.FIELD_TIMESTAMP, timeStamp);

					bid = bid.round( new MathContext( dec.intValue(), RoundingMode.DOWN) );
					ask = ask.round( new MathContext( dec.intValue(), RoundingMode.UP) );

					final DataTypeBigDecimal spread = new DataTypeBigDecimal( ask.subtract( bid ) );

					putValue(FXDataConstants.FIELD_BID, new DataTypeBigDecimal( bid ) );
					putValue(FXDataConstants.FIELD_ASK, new DataTypeBigDecimal( ask ) );
					putValue(FXDataConstants.FIELD_SPREAD, spread );

				}
			});
		}
	}

	@Override
	public void deliverKey(IDataKey inDataKey, Long inTag)
	{
		if( inTag == reutersTag )
		{
			reutersDataKey = inDataKey;
		}
		else if( inTag == bloombergTag )
		{
			bloombergDataKey = inDataKey;
		}
		
		if( reutersDataKey != null && bloombergDataKey != null )
		{
			stm.commit( new DependencyTransaction( dataKey.getKey(), SpotPriceProducer.this, 2, 0 )
			{

				@Override
				public void execute()
				{
					addDependency( reutersDataKey.getKey(), new DataProducerDependencyController( SpotPriceProducer.this, stm, reutersDataKey ) );
					addDependency( bloombergDataKey.getKey(), new DataProducerDependencyController( SpotPriceProducer.this, stm, bloombergDataKey ) );
				}
			});
		}
	}

	@Override
	public void queryNotAvailible(Long inTag)
	{
		setStatus( Status.NA );
		stm.logError( "could not retrieve datakey from market data" );
		return;
		
	}


}

