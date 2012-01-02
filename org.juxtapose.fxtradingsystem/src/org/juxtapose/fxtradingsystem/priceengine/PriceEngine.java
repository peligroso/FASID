package org.juxtapose.fxtradingsystem.priceengine;

import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;

/**
 * @author Pontus Jörgne
 * 17 okt 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class PriceEngine extends DataProducerService implements IPriceEngine, IDataProducerService, IDataSubscriber
{

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducerService#getDataKey(java.util.HashMap)
	 */
	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
	{
		String type = inQuery.get( PriceEngineDataConstants.TYPE );
		if( type == null )
		{
			stm.logError( "No type defined for dataKey "+inQuery );
			return null;
		}
		if( type.equals( PriceEngineDataConstants.STATE_TYPE_CCYMODEL ))
			return PriceEngineDataConstants.CCY_MODEL_KEY;
		
		else if( type.equals( PriceEngineDataConstants.STATE_TYPE_PRICE ))
		{
			String ccy1 = inQuery.get( FXDataConstants.FIELD_CCY1 );
			String ccy2 = inQuery.get( FXDataConstants.FIELD_CCY2 );
		
			return ProducerUtil.createDataKey( getServiceId(), PriceEngineDataConstants.STATE_TYPE_PRICE, new Integer[]{FXDataConstants.FIELD_CCY1, FXDataConstants.FIELD_CCY2},new String[]{ccy1, ccy2} );
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducerService#getDataProducer(org.juxtapose.fasid.producer.IDataKey)
	 */
	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		String type = inDataKey.getType();
		
		if( type == PriceEngineDataConstants.STATE_TYPE_CCYMODEL )
		{
			return new CcyModelProducer( stm );
		}
		
		String ccy1 = inDataKey.getValue( FXDataConstants.FIELD_CCY1 );
		String ccy2 = inDataKey.getValue( FXDataConstants.FIELD_CCY2 );
		
		return new SpotPriceProducer(inDataKey.getKey(), ccy1, ccy2, stm);
	}

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.IDataSubscriber#updateData(java.lang.String, org.juxtapose.fasid.util.IPublishedData, boolean)
	 */
	@Override
	public void updateData( String iKey, IPublishedData inData, boolean inFirstUpdate)
	{
		DataType<?> dataValue = inFirstUpdate ? inData.getValue( FXProducerServiceConstants.ORDER_MANAGER ) : inData.getDeltaValue( FXProducerServiceConstants.ORDER_MANAGER );
		if( dataValue != null )
		{
			stm.logInfo( "OrderService is registered with status: "+dataValue);
		}
		else
		{
			stm.logInfo( "OrderService is not registered");
		}
		
	}
}
