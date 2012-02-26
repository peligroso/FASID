package org.juxtapose.fxtradingsystem.priceengine;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_EUR;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_SEK;

import java.math.BigDecimal;
import java.util.HashMap;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.STMTransaction;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.marketdata.MarketDataConstants;

public abstract class MultipleSourcedPrice extends DataProducer implements IDataRequestSubscriber
{
	final long reutersTag = 0;
	final long bloombergTag = 1;
	
	IDataKey reutersDataKey;
	IDataKey bloombergDataKey;
	
	final String ccy1;
	final String ccy2;
	final String period;
	
	public MultipleSourcedPrice( IDataKey inKey, String inCcy1, String inCcy2, String inPeriod, ISTM inSTM )
	{
		super( inKey, inSTM );
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		period = inPeriod;
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
		
		HashMap<Integer, String> queryB = new HashMap<Integer, String>();
		queryB.putAll( query );
		queryB.put( MarketDataConstants.FIELD_SOURCE, "BLOOMBERG" );
		
		stm.getDataKey( FXProducerServiceConstants.MARKET_DATA, this, bloombergTag, queryB );
	}

	@Override
	public void deliverKey(IDataKey inDataKey, Long inTag)
	{
		if( inTag == reutersTag )
		{
			reutersDataKey = inDataKey;
			stm.subscribeToData( reutersDataKey, this );
		}
		else if( inTag == bloombergTag )
		{
			bloombergDataKey = inDataKey;
			stm.subscribeToData( bloombergDataKey, this );
		}
		
	}

	@Override
	public void queryNotAvailible(Long inTag)
	{
		setStatus( Status.ERROR );
		stm.logError( "could not retrieve datakey from market data" );
		return;
	}

	@Override
	public void updateData(String inKey, final IPublishedData inData, boolean inFirstUpdate)
	{
		if( reutersDataKey == null || bloombergDataKey == null )
			return;
		
		if( inData.getStatus() == Status.OK )
		{
			stm.commit( new STMTransaction( dataKey.getKey(), MultipleSourcedPrice.this, 0, 0 )
			{
				@Override
				public void execute()
				{
					
					IPublishedData reutData = stm.getData( reutersDataKey.getKey() );
					IPublishedData bloomData = stm.getData( bloombergDataKey.getKey() );
					
					if( reutData == null || bloomData == null )
					{
						dispose();
						return;
					}
					BigDecimal[] reutBidAsk = PriceEngineUtil.getBidAskFromData( reutData );
					BigDecimal[] bloomBidAsk = PriceEngineUtil.getBidAskFromData( bloomData );
					
					if( reutBidAsk == null || bloomBidAsk == null )
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
					
					processBidAsk( bid, ask, this );
				}
			});
		}
	}
	
	@Override
	public void stop()
	{
		stm.unsubscribeToData( reutersDataKey, this );
		stm.unsubscribeToData( bloombergDataKey, this );
	}
	
	protected abstract void processBidAsk( BigDecimal inBid, BigDecimal inAsk, STMTransaction inTransaction );
	

}
