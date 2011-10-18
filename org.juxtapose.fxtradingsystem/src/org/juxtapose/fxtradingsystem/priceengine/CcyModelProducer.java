
package org.juxtapose.fxtradingsystem.priceengine;

import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeData;

/**
 * @author Pontus Jörgne
 * 17 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class CcyModelProducer implements IDataProducer
{
	final ISTM stm;
	
	/**
	 * @param inSTM
	 */
	public CcyModelProducer( ISTM inSTM )
	{
		stm = inSTM;
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
//						addValue(DataConstants.DATA_STATUS, new DataTypeString(Status.OK.toString()) );
//						addValue(FXDataConstants.CCY1, new DataTypeString(ccy1) );
//						addValue(FXDataConstants.CCY2, new DataTypeString(ccy2) );
//						addValue(FXDataConstants.SEQUENCE, new DataTypeLong(seq) );
//						
//						addPriceUpdate( rand, this );
					}
				});
			}
		});
	}
	
//	public DataTypeData getCcyData( int inCcy1 )
//	{
//		IPublishedData data = stm.createEmptyData(Status.OK, this, null);
//		data.putDataValue(inKey, inValue);
//	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}

}
