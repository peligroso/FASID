package org.juxtapose.fxtradingsystem.ordermanager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.IDataProducerService;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.producer.executor.IExecutor;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.KeyConstants;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeBoolean;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fasid.util.subscriber.DataSequencer;
import org.juxtapose.fasid.util.subscriber.ISequencedDataSubscriber;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;

/**
 * @author Pontus Jörgne
 * Feb 26, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class OrderManager extends DataProducerService implements IOrderManager, IDataProducerService, ISequencedDataSubscriber
{
	volatile String priceKey = null;
	
	AtomicLong sequenceId = new AtomicLong(-1);
	
	final long spotPriceQueryTag = 1;
	
	ClientConnector connector;
	
	ConcurrentHashMap<Long, RFQContext> idToRFQProducer = new ConcurrentHashMap<Long, RFQContext>(512);
	

	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		if( FXDataConstants.STATE_TYPE_RFQ.equals( inDataKey.getType() ))
		{
			String val = inDataKey.getValue( FXDataConstants.FIELD_ID );
			Long id = Long.parseLong( val );
			
			RFQContext ctx = idToRFQProducer.get( id );
			
			if( ctx ==  null )
			{
				stm.logError("Could not find rfq producer for key "+inDataKey);
				return null;
			}
			
			return ctx.producer;
		}
		return null;
	}

	@Override
	public void updateData( IDataKey inKey, IPublishedData inData, boolean inFirstUpdate )
	{
		if( inKey.equals( KeyConstants.PRODUCER_SERVICE_KEY ))
		{
			DataType<?> dataValue = inData.getValue( FXProducerServiceConstants.PRICE_ENGINE );
			if( dataValue != null )
			{
				System.out.println( "Price engine is registered with status: "+dataValue);
				
				if( dataValue.get() == Status.OK.toString() )
				{
					connector = new ClientConnector( this );
				}
			}
			else
			{
				System.out.println( "Price engine is not registered");
			}
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
		
		processData( data, inSequencer.getDataKey() );
		
	}
	
	private void processData( IPublishedData inData, IDataKey inKey)
	{
		Status status = inData.getStatus();
		if( status == Status.OK )
		{
			String idStr = inKey.getValue( FXDataConstants.FIELD_ID );
			
			final Long id = Long.parseLong( idStr );
			DataTypeRef priceRef = (DataTypeRef)inData.getValue( FXDataConstants.FIELD_PRICE );
			final DataTypeBigDecimal bid = (DataTypeBigDecimal)priceRef.getReferenceData().getValue( FXDataConstants.FIELD_BID );
			final DataTypeBigDecimal ask = (DataTypeBigDecimal)priceRef.getReferenceData().getValue( FXDataConstants.FIELD_ASK );
			final DataTypeBigDecimal spread = (DataTypeBigDecimal)priceRef.getReferenceData().getValue( FXDataConstants.FIELD_SPREAD );
			
			Long tou = (Long)priceRef.getReferenceData().getValue( DataConstants.FIELD_TIMESTAMP ).get();
			
			final long sequence = inData.getSequenceID();

			BigDecimal validateSpread = ask.get().subtract( bid.get() );
			boolean valid = validateSpread.equals( spread.get() );
			
			long now = System.nanoTime();
			
			final long updateProcessingTime = now-tou;
			
			final Long firstTakeTime;
			DataTypeBoolean firstTake = (DataTypeBoolean)inData.getValue( FXDataConstants.FIELD_FIRST_UPDATE );
			if( firstTake != null && firstTake.get() )
			{
				RFQContext context = idToRFQProducer.get( id );

				if( context != null )
				{
					firstTakeTime = now - context.startTime;
				}
				else
					firstTakeTime = null;
			}
			else
				firstTakeTime = null;
			
			if( ! valid )
			{
				System.err.println( "Price is not valid : "+validateSpread+" != "+spread.get() );
			}
			else
			{
//				stm.execute( new Runnable(){
//
//					@Override
//					public void run()
//					{
//						if( firstTakeTime != null )
//							System.out.println( "RoundTrip: "+firstTakeTime); 
//						System.out.println( "Price is "+bid.get().toPlainString()+" / "+ask.get().toPlainString()+" sequence "+sequence+" updatetime: "+updateProcessingTime+"   id: "+id );
//					}
//					
//				}, IExecutor.LOW );
			}
			
			RFQMessage message = new RFQMessage( RFQMessage.TYPE_PRICING, id, bid.get().doubleValue(), ask.get().doubleValue(), firstTakeTime, updateProcessingTime, sequence );
			
//			long start = System.nanoTime();
			
			connector.updateRFQ( message );
			
//			long end = System.nanoTime();
//			
//			System.err.println("Time it took for price update was: "+(end-start)+" nano");
			
		}
		else
		{
//			/long sequence = inData.getSequenceID();
//			System.out.println( "PriceStatus is "+status+" "+sequence+inData.getDataMap() );
		}
	}
	
	/**
	 * @param inMessage
	 */
	public void sendRFQ( final RFQMessage inMessage )
	{
		stm.execute( new Runnable(){

			@Override
			public void run()
			{
				long rfqID = inMessage.tag;
				
				String id = Long.toString( rfqID );
				
				IDataKey key = ProducerUtil.createDataKey( getServiceId(), FXDataConstants.STATE_TYPE_RFQ, 
						new Integer[]{FXDataConstants.FIELD_ID, FXDataConstants.FIELD_CCY1, FXDataConstants.FIELD_CCY2}, 
						new String[]{id, inMessage.ccy1, inMessage.ccy2 } );
				
				RFQProducer producer = new RFQProducer( key, stm );
				DataSequencer seq = new DataSequencer( OrderManager.this, stm, key );
				
				RFQContext ctx = new RFQContext( seq, producer, System.nanoTime() );
				idToRFQProducer.put( rfqID, ctx );
				
				seq.start();
			}
			
		}, IExecutor.HIGH );
	}
	
	/**
	 * @param inMessage
	 */
	public void sendDR( final RFQMessage inMessage )
	{
		stm.execute( new Runnable(){

			@Override
			public void run()
			{
				RFQContext ctx = idToRFQProducer.remove( inMessage.tag );
				if( ctx != null )
				{
					ctx.sequencer.stop();
				}
			}
			
		}, IExecutor.HIGH );
	}
	
	@Override
	public void getDataKey(IDataRequestSubscriber inSubscriber, Long inTag, HashMap<Integer, String> inQuery)
	{
		inSubscriber.queryNotAvailible( inTag );
	}


}
