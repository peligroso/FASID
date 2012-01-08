package org.juxtapose.fxtradingsystem.priceengine;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;

/**
 * @author Pontus Jörgne
 * 6 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class SpotPriceProducer extends DataProducer
{
	final String ccy1;
	final String ccy2;
	
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
	
	public void start()
	{
		final Random rand = new Random();
		
//		stm.commit( new DataTransaction( key )
//		{
//			@Override
//			public void execute()
//			{
//				addValue(PriceEngineDataConstants.CCY1, new DataTypeString(ccy1) );
//				addValue(PriceEngineDataConstants.CCY2, new DataTypeString(ccy2) );
//				addValue(DataConstants.DATA_STATUS, new DataTypeString(Status.ON_REQUEST.toString()) );
//				
////				addPriceUpdate( rand, this );
//			}
//		});
		
		ReentrantLock eursekLock = new ReentrantLock( true );
		
		for( int i = 0; i < 20; i++ )
		{
			final boolean first = i == 0;
			stm.execute( new Runnable()
			{
				@Override
				public void run()
				{
					stm.commit( new DataTransaction( dataKey.getKey() )
					{
						@Override
						public void execute()
						{
							if ( first )
								setStatus( Status.OK );
							
							addValue( FXDataConstants.FIELD_CCY1, new DataTypeString( ccy1 ) );
							addValue( FXDataConstants.FIELD_CCY2, new DataTypeString( ccy2 ) );
							
							addPriceUpdate( rand, this );
						}
					});
				}
			}, eursekLock);
		}
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
		
		inTransaction.addValue(FXDataConstants.FIELD_BID, bid );
		inTransaction.addValue(FXDataConstants.FIELD_ASK, ask );
		inTransaction.addValue(FXDataConstants.FIELD_SPREAD, spread );
	}


	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}
}
