
package org.juxtapose.fxtradingsystem.priceengine;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeData;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;

/**
 * @author Pontus Jörgne
 * 17 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class CcyModelProducer extends DataProducer
{
	/**
	 * @param inSTM
	 */
	public CcyModelProducer( ISTM inSTM )
	{
		super( PriceEngineKeyConstants.CCY_MODEL_KEY, inSTM);
	}
	
	@Override
	public void start()
	{
		stm.execute( new Runnable()
		{
			@Override
			public void run()
			{
				stm.commit( new DataTransaction( PriceEngineKeyConstants.CCY_MODEL_KEY.getKey() )
				{
					@Override
					public void execute()
					{
						setStatus( Status.OK );
						addReference(PriceEngineDataConstants.FIELD_EUR, new DataTypeRef( PriceEngineKeyConstants.CCY_EUR_KEY ) );
						addReference(PriceEngineDataConstants.FIELD_SEK, new DataTypeRef( PriceEngineKeyConstants.CCY_SEK_KEY ) );
//						addValue(FXDataConstants.CCY2, new DataTypeString(ccy2) );
//						addValue(FXDataConstants.FIELD_SEQUENCE, new DataTypeLong(seq) );
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
