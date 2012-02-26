package org.juxtapose.fxtradingsystem.ordermanager;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.KeyConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fasid.util.subscriber.DataSequencer;
import org.juxtapose.fasid.util.subscriber.ISequencedDataSubscriber;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.marketdata.IMarketDataSubscriber;
import org.juxtapose.fxtradingsystem.marketdata.QPMessage;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineUtil;

public class OrderManager extends DataProducerService implements IOrderManager, IDataProducerService, IDataRequestSubscriber, ISequencedDataSubscriber
{
	volatile String priceKey = null;
	
	AtomicLong sequenceId = new AtomicLong(-1);
	
	final long spotPriceQueryTag = 1;
	

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
					HashMap<Integer, String> query = PriceEngineUtil.getSpotPriceQuery( STATE_EUR, STATE_SEK );
					stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, this, spotPriceQueryTag, query );
				}
			}
			else
			{
				System.out.println( "Price engine is not registered");
			}
		}
		if( inKey.equals( priceKey ))
		{
			processData( inData );
		}
	}
	
	@Override
	public Integer getServiceId()
	{
		return FXProducerServiceConstants.ORDER_MANAGER;
	}

	@Override
	public void dataUpdated(DataSequencer inSequencer)
	{
		IPublishedData data = inSequencer.get();
		
		processData( data );
		
	}
	
	private void processData( IPublishedData inData )
	{
		Status status = inData.getStatus();
		if( status == Status.OK )
		{
			long now = System.nanoTime();
			
			DataTypeBigDecimal bid = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_BID );
			DataTypeBigDecimal ask = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_ASK );
			DataTypeBigDecimal spread = (DataTypeBigDecimal)inData.getValue( FXDataConstants.FIELD_SPREAD );
			
			Long tou = (Long)inData.getValue( DataConstants.FIELD_TIMESTAMP ).get();
			
			long updateProcessingTime = now-tou;

			long sequence = inData.getSequenceID();

			BigDecimal validateSpread = ask.get().subtract( bid.get() );
			boolean valid = validateSpread.equals( spread.get() );
			if( ! valid )
			{
				System.err.println( "Price is not valid : "+validateSpread+" != "+spread.get() );
			}
			else
			{
				System.out.println( "Price is "+bid.get().toPlainString()+" / "+ask.get().toPlainString()+" sequence "+sequence+" updatetime: "+updateProcessingTime );
			}
		}
		else
		{
			long sequence = inData.getSequenceID();
			System.out.println( "PriceStatus is "+status+" "+sequence+inData.getDataMap() );
		}
	}
	
	private void startRFQListener()
	{
		Thread rfqThread = new Thread( new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					for(;;)
					{
						
					}
				} catch ( Throwable t )
				{
					t.printStackTrace();
				}
			}
			
		}, "RFQ Listener");
	}

	@Override
	public void deliverKey(IDataKey inDataKey, Long inTag)
	{
		if( inTag.equals( spotPriceQueryTag ))
		{
			IDataKey dataKey = inDataKey;
			
			priceKey = dataKey.getKey();

//			stm.subscribeToData( dataKey, this );
			new DataSequencer( this, stm, dataKey );
		}
		
	}


	@Override
	public void getDataKey(IDataRequestSubscriber inSubscriber, Long inTag, HashMap<Integer, String> inQuery)
	{
		inSubscriber.queryNotAvailible( inTag );
	}

	@Override
	public void queryNotAvailible(Long inTag)
	{
		// TODO Auto-generated method stub
		
	}

}
