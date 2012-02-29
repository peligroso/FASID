package org.juxtapose.fxtradingsystem.ordermanager;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.juxtapose.fxtradingsystem.FXDataConstants;

public class ClientConnector
{
	final OrderManager manager;
	
	BlockingQueue<RFQMessage> incomming;
	
	Random rand = new Random();
	
	long tag = 0;
	
	long timeBetweenRFQ = 1000 * 1000000;
	
	long timeFromLastRFQ = 0;
	
	int maxRFQs = 1;
	
	public ClientConnector( OrderManager inManager )
	{
		manager = inManager;
		
		incomming = new LinkedBlockingQueue<RFQMessage>();
		
		startRFQThread();
	}
	
	private void startRFQThread()
	{
		Thread rfqThread = new Thread( new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					int i = 0;
					for(;;)
					{
						RFQMessage inCommingMess = incomming.poll( 1000, TimeUnit.MILLISECONDS );
						
						if( inCommingMess != null )
						{
							if( inCommingMess.messageType == RFQMessage.TYPE_PRICING )
							{
								if( rand.nextInt(5) == 1 )
								{
									RFQMessage dr = new RFQMessage( RFQMessage.TYPE_DR, inCommingMess.tag, inCommingMess.bidPrice, inCommingMess.askPrice );
									manager.sendDR( dr );
									
									System.out.println("sending dr ");
									break;
								}
							}
						}
						
						long time = System.nanoTime();
						if( (timeFromLastRFQ == 0 || (time - timeFromLastRFQ) > timeBetweenRFQ) && i < maxRFQs )
						{
							sendRFQ();
						}
						
						i++;
					}
				} catch ( Throwable t )
				{
					t.printStackTrace();
				}
			}
			
		}, "RFQ Requestor");
		
		rfqThread.start();
	}
	
	private void sendRFQ( )
	{
//		if( tag > 0 )
//			return;
		RFQMessage rfq = new RFQMessage( "EUR", "SEK", FXDataConstants.STATE_INSTRUMENT_SPOT, "SP", "SP", tag++ );
		manager.sendRFQ( rfq );
	}
	
	public void updateRFQ( RFQMessage inMessage )
	{
		try
		{
			incomming.put( inMessage );
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
