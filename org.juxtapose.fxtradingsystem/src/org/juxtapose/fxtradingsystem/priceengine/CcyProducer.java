package org.juxtapose.fxtradingsystem.priceengine;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_EUR;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_USD;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;

/**
 * @author Pontus Jörgne
 * Dec 11, 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class CcyProducer extends DataProducer
{
	final String ccy;
	
	/**
	 * @param inSTM
	 * @param inCcy
	 */
	public CcyProducer(ISTM inSTM, IDataKey inKey, String inCcy )
	{
		super( inKey, inSTM );
		ccy = inCcy;
	}
	
	@Override
	public void start()
	{
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
						setStatus( Status.OK );
						addValue(FXDataConstants.FIELD_PIP, new DataTypeLong(10000L) );
						addValue(FXDataConstants.FIELD_DECIMALS, new DataTypeLong(5L) );
						if( ccy.equals(STATE_EUR))
							addValue(FXDataConstants.FIELD_BASE_CCY, new DataTypeString(STATE_USD) );
						else
							addValue(FXDataConstants.FIELD_BASE_CCY, new DataTypeString(STATE_EUR) );
					}
				});
			}
		});

	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

}
