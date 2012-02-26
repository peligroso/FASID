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
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineUtil;

public class RFQProducer extends DataProducer implements IDataRequestSubscriber
{
	final static long priceTag = 0;
	
	public RFQProducer(IDataKey inKey, ISTM inSTM)
	{
		super( inKey, inSTM );		
	}

	@Override
	protected void start()
	{
		HashMap<Integer, String> query = PriceEngineUtil.getSpotPriceQuery( dataKey.getValue( FXDataConstants.FIELD_CCY1 ), dataKey.getValue( FXDataConstants.FIELD_CCY2 ) );
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
		if( inTransaction.getStatus() != Status.OK )
		{
			if( inData.getStatus() == Status.OK )
				inTransaction.setStatus( Status.OK );
		}
	}
	

}
