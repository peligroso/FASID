package org.juxtapose.fxtradingsystem.priceengine;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.*;

/**
 * @author Pontus Jörgne
 * Dec 11, 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class CcyProducer implements IDataProducer
{
	final ISTM stm;
	final String ccy;
	
	/**
	 * @param inSTM
	 * @param inCcy
	 */
	public CcyProducer(ISTM inSTM, String inCcy )
	{
		stm = inSTM;
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
				stm.commit( new DataTransaction( PriceEngineDataConstants.CCY_MODEL_KEY.getKey() )
				{
					@Override
					public void execute()
					{
						addValue(DataConstants.FIELD_DATA_STATUS, new DataTypeString(Status.OK.toString()) );
						addValue(FXDataConstants.FIELD_PIP, new DataTypeLong(10000L) );
						addValue(FXDataConstants.FIELD_DECIMALS, new DataTypeLong(5L) );
						if( ccy.equals(EUR))
							addValue(FXDataConstants.FIELD_BASE_CCY, new DataTypeString(USD) );
						else
							addValue(FXDataConstants.FIELD_BASE_CCY, new DataTypeString(EUR) );
//						addValue(FXDataConstants.CCY2, new DataTypeString(ccy2) );
//						addValue(FXDataConstants.FIELD_SEQUENCE, new DataTypeLong(seq) );
//						
//						addPriceUpdate( rand, this );
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
