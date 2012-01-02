package org.juxtapose.fxtradingsystem.aggregator;

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
import org.juxtapose.fxtradingsystem.priceengine.SpotPriceProducer;

public class Aggregator extends DataProducerService implements IAggregator, IDataProducerService, IDataSubscriber
{
	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
	{
		String ccy1 = inQuery.get( FXDataConstants.FIELD_CCY1 );
		String ccy2 = inQuery.get( FXDataConstants.FIELD_CCY2 );
		
		return null;
//		return ProducerUtil.createDataKey( getServiceId(), new Integer[]{FXDataConstants.FIELD_CCY1, FXDataConstants.FIELD_CCY2},new String[]{ccy1, ccy2} );
	}

	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		String ccy1 = inDataKey.getValue( FXDataConstants.FIELD_CCY1 );
		String ccy2 = inDataKey.getValue( FXDataConstants.FIELD_CCY2 );
		
		return new SpotPriceProducer(inDataKey.getKey(), ccy1, ccy2, stm);
	}

	@Override
	public void updateData( String iKey, IPublishedData inData, boolean inFirstUpdate)
	{
		DataType<?> dataValue = inFirstUpdate ? inData.getValue( FXProducerServiceConstants.ORDER_MANAGER ) : inData.getDeltaValue( FXProducerServiceConstants.ORDER_MANAGER );
		if( dataValue != null )
		{
			System.out.println( "OrderService is registered with status: "+dataValue);
		}
		else
		{
			System.out.println( "OrderService is not registered");
		}
		
	}
}
