
package org.juxtapose.fxtradingsystem.priceengine;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.stm.impl.DataTransaction;
import org.juxtapose.fasid.stm.impl.ISTM;
import org.juxtapose.fasid.stm.impl.ReferenceLink;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

/**
 * @author Pontus J�rgne
 * 17 okt 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
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
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#referencedDataUpdated(java.lang.Integer, org.juxtapose.fasid.util.IPublishedData)
	 */
	public void postReferenceDataCall( Integer inFieldKey, ReferenceLink inLink, IPublishedData inData )
	{
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
