package org.juxtapose.fxtradingsystem.priceengine;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeString;

/**
 * @author Pontus Jörgne
 * 6 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class SpotPriceProducer implements IDataProducer
{
	final String ccy1;
	final String ccy2;
	final ISTM stm;
	final String key;
	
	/**
	 * @param inKey
	 * @param inCcy1
	 * @param inCcy2
	 * @param inSTM
	 */
	public SpotPriceProducer( final String inKey, final String inCcy1, final String inCcy2, final ISTM inSTM )
	{
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		stm = inSTM;
		key = inKey;
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
			final long seq = i;
			stm.execute( new Runnable()
			{
				@Override
				public void run()
				{
					stm.commit( new DataTransaction( key )
					{
						@Override
						public void execute()
						{
							if( first )
								addValue(DataConstants.DATA_STATUS, new DataTypeString(Status.OK.toString()) );
							addValue(PriceEngineDataConstants.CCY1, new DataTypeString(ccy1) );
							addValue(PriceEngineDataConstants.CCY2, new DataTypeString(ccy2) );
							addValue(PriceEngineDataConstants.SEQUENCE, new DataTypeLong(seq) );
							
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
		
		inTransaction.addValue(PriceEngineDataConstants.BID, bid );
		inTransaction.addValue(PriceEngineDataConstants.ASK, ask );
		inTransaction.addValue(PriceEngineDataConstants.SPREAD, spread );
	}


	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}
}
