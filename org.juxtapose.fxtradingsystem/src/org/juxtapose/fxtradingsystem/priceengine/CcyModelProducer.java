
package org.juxtapose.fxtradingsystem.priceengine;

import java.util.Iterator;
import java.util.Map.Entry;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

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
		stm.commit( new DataTransaction( PriceEngineKeyConstants.CCY_MODEL_KEY.getKey(), CcyModelProducer.this )
		{
			@Override
			public void execute()
			{
				setStatus( Status.INITIALIZING );
				addReference(PriceEngineDataConstants.FIELD_EUR, new DataTypeRef( PriceEngineKeyConstants.CCY_EUR_KEY ) );
				addReference(PriceEngineDataConstants.FIELD_SEK, new DataTypeRef( PriceEngineKeyConstants.CCY_SEK_KEY ) );
				//						addValue(FXDataConstants.CCY2, new DataTypeString(ccy2) );
				//						addValue(FXDataConstants.FIELD_SEQUENCE, new DataTypeLong(seq) );
				//						
				//						addPriceUpdate( rand, this );
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
	
	public void referencedDataUpdated( final Integer inFieldKey, final IPublishedData inData )
	{
		super.referencedDataUpdated( inFieldKey, inData );
		checkStatus();
	}
	
	public void checkStatus()
	{
		IPublishedData data = stm.getData( dataKey.getKey() );
		if( data != null )
		{
			if( data.getStatus() == Status.INITIALIZING )
			{
				Iterator<Entry<Integer, DataType<?>>> iterator = data.getDataMap().iterator();
				
				while( iterator.hasNext() )
				{
					Entry<Integer, DataType<?>> entry = iterator.next();
					
					IPublishedData ref = ((DataTypeRef)entry.getValue()).getReferenceData();
					
					if( ref != null )
					{
						if( ref.getStatus() != Status.OK )
							return;
					}
					else
					{
						return;
					}
				}
				
				stm.commit( new DataTransaction( PriceEngineKeyConstants.CCY_MODEL_KEY.getKey(), CcyModelProducer.this )
				{
					@Override
					public void execute()
					{
						setStatus( Status.OK );
					}
				});
			}
		}
			
		
	}

}
