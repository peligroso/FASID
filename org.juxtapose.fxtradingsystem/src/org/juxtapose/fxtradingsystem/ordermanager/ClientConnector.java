package org.juxtapose.fxtradingsystem.ordermanager;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.juxtapose.fxtradingsystem.FXDataConstants;

public class ClientConnector
{
	final OrderManager manager;
	
	BlockingQueue<RFQMessage> incomming;
	
	Random rand = new Random();
	
	long tag = 0;
	
	long timeBetweenRFQ = 1000 * 1000000;
	
	long timeFromLastRFQ = 0;
	
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
					for(;;)
					{
						RFQMessage inCommingMess = incomming.poll();
						
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
						if( timeFromLastRFQ == 0 || (time - timeFromLastRFQ) > timeBetweenRFQ )
						{
							sendRFQ();
							break;
						}
						Thread.sleep(100);
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
}
