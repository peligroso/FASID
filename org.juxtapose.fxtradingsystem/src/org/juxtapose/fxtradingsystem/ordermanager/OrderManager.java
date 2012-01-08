package org.juxtapose.fxtradingsystem.ordermanager;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.*;

import java.math.BigDecimal;
import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.KeyConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineUtil;

public class OrderManager extends DataProducerService implements IOrderManager, IDataProducerService, IDataSubscriber
{
	volatile String priceKey = null;
	
	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
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
	public void updateData( String inKey, IPublishedData inData, boolean inFirstUpdate )
	{
		if( inKey == KeyConstants.PRODUCER_SERVICE_KEY.getKey() )
		{
			DataType<?> dataValue = inData.getValue( FXProducerServiceConstants.PRICE_ENGINE );
			if( dataValue != null )
			{
				System.out.println( "Price engine is registered with status: "+dataValue);
				
				if( dataValue.get() == Status.OK.toString() )
				{
					HashMap<Integer, String> query = PriceEngineUtil.getPriceQuery( STATE_EUR, STATE_SEK );
					IDataKey dataKey = stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, query );
					priceKey = dataKey.getKey();

					stm.subscribeToData( dataKey, this );
				}
			}
			else
			{
				System.out.println( "Price engine is not registered");
			}
		}
		if( inKey.equals( priceKey ) )
		{
			Status status = inData.getStatus();
			if( status == Status.OK )
			{
				DataTypeBigDecimal bid = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_BID );
				DataTypeBigDecimal ask = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_ASK );
				DataTypeBigDecimal spread = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_SPREAD );
				
				long sequence = inData.getSequenceID();
				
				BigDecimal validateSpread = ask.get().subtract( bid.get() );
				boolean valid = validateSpread.equals( spread.get() );
				if( ! valid )
				{
					System.err.println( "Price is not valid : "+validateSpread+" != "+spread.get() );
				}
				else
				{
					System.out.println( "Price is "+bid.get().toPlainString()+" / "+ask.get().toPlainString()+" sequence "+sequence );
				}
			}
			else
			{
				long sequence = inData.getSequenceID();
				System.out.println( "PriceStatus is "+status+sequence );
			}
			return;
		}
		
	}
	
	@Override
	public Integer getServiceId()
	{
		return FXProducerServiceConstants.ORDER_MANAGER;
	}

}
