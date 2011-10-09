package org.juxtapose.fxtradingsystem.priceengine;

import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fxtradingsystem.ProducerServiceConstants;

public class PriceEngine extends DataProducerService implements IPriceEngine, IDataProducerService, IDataSubscriber
{

	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
	{
		String ccy1 = inQuery.get( PriceEngineDataConstants.CCY1 );
		String ccy2 = inQuery.get( PriceEngineDataConstants.CCY2 );
		
		return ProducerUtil.createDataKey( getServiceId(), new Integer[]{PriceEngineDataConstants.CCY1, PriceEngineDataConstants.CCY2}, 
															new String[]{ccy1, ccy2} );
	}

	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		String ccy1 = inDataKey.getValue( PriceEngineDataConstants.CCY1 );
		String ccy2 = inDataKey.getValue( PriceEngineDataConstants.CCY2 );
		
		return new SpotPriceProducer(inDataKey.getKey(), ccy1, ccy2, m_stm);
	}

	@Override
	public void updateData( String iKey, IPublishedData inData, boolean inFirstUpdate)
	{
		DataType<?> dataValue = inFirstUpdate ? inData.getValue( ProducerServiceConstants.ORDER_MANAGER ) : inData.getDeltaValue( ProducerServiceConstants.ORDER_MANAGER );
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
