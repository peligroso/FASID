package org.juxtapose.fxtradingsystem.ordermanager;

import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fxtradingsystem.ProducerServiceConstants;

public class OrderManager extends DataProducerService implements IOrderManager, IDataProducerService, IDataSubscriber
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
		DataType<?> dataValue = inData.getValue( ProducerServiceConstants.PRICE_ENGINE );
		if( dataValue != null )
		{
			System.out.println( "Price engine is registered with status: "+dataValue);
		}
		else
		{
			System.out.println( "Price engine is not registered");
		}

	}


}
