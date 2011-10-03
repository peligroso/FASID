package org.juxtapose.fxtradingsystem.priceengine;

import java.util.HashMap;

import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.stm.exp.STMUtil;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;
import org.juxtapose.fxtradingsystem.ProducerServiceConstants;

public class PriceEngine extends DataProducerService implements IPriceEngine, IDataProducerService, IDataSubscriber
{

	@Override
	public IDataKey getDataKey(HashMap<String, String> inQuery)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateData(IPublishedData inData, boolean inFirstUpdate)
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
