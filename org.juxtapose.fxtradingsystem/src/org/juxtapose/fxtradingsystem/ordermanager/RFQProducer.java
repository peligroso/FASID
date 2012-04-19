package org.juxtapose.fxtradingsystem.ordermanager;

import java.util.HashMap;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.stm.ReferenceLink;
import org.juxtapose.fasid.stm.STMTransaction;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeBoolean;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineUtil;

public class RFQProducer extends DataProducer implements IDataRequestSubscriber
{
	final static long priceTag = 0;
	final String ccy1;
	final String ccy2;
	final String nearPeriod;
	final String farPeriod;
	
	public RFQProducer( IDataKey inKey, ISTM inSTM, String inCcy1, String inCcy2, String inNearPeriod, String inFarPeriod )
	{
		super( inKey, inSTM );
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		nearPeriod = inNearPeriod;
		farPeriod = inFarPeriod;
	}

	@Override
	protected void start()
	{
		stm.commit( new STMTransaction( dataKey, RFQProducer.this, 1, 0 )
		{
			@Override
			public void execute()
			{
				putValue( FXDataConstants.FIELD_CCY1, new DataTypeString( ccy1 ) );
				putValue( FXDataConstants.FIELD_CCY2, new DataTypeString( ccy2 ) );
				
				if( nearPeriod != null )
					putValue( FXDataConstants.FIELD_NEAR_SWAP, new DataTypeString( nearPeriod ));
				if( farPeriod != null )
					putValue( FXDataConstants.FIELD_FAR_SWAP, new DataTypeString( farPeriod ));
			}
		});
		
		HashMap<Integer, String> query;
		
		if( nearPeriod == null && farPeriod == null )
			query = PriceEngineUtil.getSpotPriceQuery( ccy1, ccy2 );
		else if( farPeriod == null )
			query = PriceEngineUtil.getFwdPriceQuery( ccy1, ccy2, nearPeriod );
		else
			query = PriceEngineUtil.getSwapPriceQuery( ccy1, ccy2, nearPeriod, farPeriod );
		
		stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, RFQProducer.this, priceTag, query );
	}

	@Override
	public void deliverKey( final IDataKey inDataKey, Long inTag)
	{
		if( inTag.equals( priceTag ))
		{
			stm.commit( new STMTransaction( dataKey, RFQProducer.this, 1, 0 )
			{
				@Override
				public void execute()
				{
					addReference( FXDataConstants.FIELD_PRICE, new DataTypeRef( inDataKey ) );
				}
			});
		}
	}

	@Override
	public void queryNotAvailible(Long inTag)
	{
		setStatus( Status.NA );
		stm.logError( "could not retrieve datakey from price engine" );
		return;
	}
	
	protected void referenceDataCall( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData, STMTransaction inTransaction )
	{
		if( inFieldKey == FXDataConstants.FIELD_PRICE )
		{
			DataTypeBoolean priced = (DataTypeBoolean)inTransaction.get( FXDataConstants.FIELD_FIRST_UPDATE );
			
			if( priced == null )
			{
				inTransaction.putValue( FXDataConstants.FIELD_FIRST_UPDATE, new DataTypeBoolean(true) );
			}
			else if( priced.get() )
			{
				inTransaction.putValue( FXDataConstants.FIELD_FIRST_UPDATE, new DataTypeBoolean(false) );
			}
		}
		
		if( inTransaction.getStatus() != Status.OK )
		{
			if( inData.getStatus() == Status.OK )
			{
				inTransaction.setStatus( Status.OK );
			}
		}
	}
	

}
