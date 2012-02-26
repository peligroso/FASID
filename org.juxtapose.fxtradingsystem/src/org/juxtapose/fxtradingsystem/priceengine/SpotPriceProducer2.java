package org.juxtapose.fxtradingsystem.priceengine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.DataTransaction;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fxtradingsystem.FXDataConstants;

/**
 * @author Pontus Jörgne
 * 6 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class SpotPriceProducer2 extends MultipleSourcedPrice implements IDataSubscriber
{
	/**
	 * @param inKey
	 * @param inCcy1
	 * @param inCcy2
	 * @param inSTM
	 */
	public SpotPriceProducer2( IDataKey inKey, String inCcy1, String inCcy2, ISTM inSTM )
	{
		super( inKey, inCcy1, inCcy2, FXDataConstants.STATE_PERIOD_SP, inSTM );
	}
	
	public void linkStaticData()
	{
		stm.commit( new DataTransaction( dataKey.getKey(), SpotPriceProducer2.this, 1, 0 )
		{
			@Override
			public void execute()
			{
				addReference( PriceEngineDataConstants.FIELD_STATIC_DATA, new DataTypeRef( PriceEngineKeyConstants.CCY_SEK_KEY ) );
			}
		});
	}
	
	public void start()
	{
		linkStaticData();
		subscribe();
	}

	@Override
	protected void processBidAsk(BigDecimal inBid, BigDecimal inAsk, DataTransaction inTransaction)
	{
		DataTypeRef ref = (DataTypeRef)inTransaction.get( PriceEngineDataConstants.FIELD_STATIC_DATA );
		
		if( ref == null )
		{
			inTransaction.dispose();
			return;
		}
		
		Long dec = PriceEngineUtil.getDecimals( ref.getReferenceData() );
		
		if( dec == null )
		{
			inTransaction.dispose();
			return;
		}
		
		BigDecimal bid = inBid.round( new MathContext( dec.intValue(), RoundingMode.DOWN) );
		BigDecimal ask = inAsk.round( new MathContext( dec.intValue(), RoundingMode.UP) );
		
		final DataTypeBigDecimal spread = new DataTypeBigDecimal( ask.subtract( bid ) );
		
		inTransaction.putValue(FXDataConstants.FIELD_BID, new DataTypeBigDecimal( bid ) );
		inTransaction.putValue(FXDataConstants.FIELD_ASK, new DataTypeBigDecimal( ask ) );
		inTransaction.putValue(FXDataConstants.FIELD_SPREAD, spread );
		
	}
}
