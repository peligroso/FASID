package org.juxtapose.fxtradingsystem.ordermanager;

import java.math.BigDecimal;
import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants;

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
		if( inKey.equals( priceKey ) )
		{
			DataTypeString status = (DataTypeString)inData.getValue( DataConstants.FIELD_DATA_STATUS );
			if( Status.valueOf( status.get() ) == Status.OK )
			{
				DataTypeBigDecimal bid = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_BID );
				DataTypeBigDecimal ask = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_ASK );
				DataTypeBigDecimal spread = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_SPREAD );
				
				DataTypeLong sequence = (DataTypeLong)inData.getValue( FXDataConstants.FIELD_SEQUENCE );
				
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
				DataType<?> seq = (DataTypeLong)inData.getValue( FXDataConstants.FIELD_SEQUENCE );
				if( seq != null )
					System.out.println( "PriceStatus is "+status+seq.get() );
				else
					System.out.println( "PriceStatus is "+status );
			}
			return;
		}
		DataType<?> dataValue = inData.getValue( FXProducerServiceConstants.PRICE_ENGINE );
		if( dataValue != null )
		{
			System.out.println( "Price engine is registered with status: "+dataValue);
			
			HashMap<Integer, String> query = new HashMap<Integer, String>();
			query.put( PriceEngineDataConstants.TYPE, PriceEngineDataConstants.STATE_TYPE_PRICE);
			query.put( FXDataConstants.FIELD_CCY1, "EUR");
			query.put( FXDataConstants.FIELD_CCY2, "SEK");
			
			IDataKey dataKey = stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, query);
			priceKey = dataKey.getKey();
			
			stm.subscribeToData(dataKey, this);
		}
		else
		{
			System.out.println( "Price engine is not registered");
		}

	}


}
