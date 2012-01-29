package org.juxtapose.fxtradingsystem.quoteprovider;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;
import org.exolab.jms.message.ObjectMessageImpl;



/**
 * @author Pontus J�rgne
 * Jan 22, 2012
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public class QuoteProvider extends QuoteProviderActivator
{
	public static String RECIEVER_PREFIX = "SUBSCRIBE_";
	public static String SENDER_PREFIX = "PUBLISH_";
	
	private String name;
	private String senderTopic;
	private String recieverTopic;
	
	List<QPMessage> subscribedInstruments = new Vector<QPMessage>();
	
	Random rand = new Random();
	
	public void init( String inName )
	{
		name = inName;
		
		senderTopic = SENDER_PREFIX+inName;
		
		recieverTopic = RECIEVER_PREFIX+inName;
		
		createDestination( recieverTopic );
		createDestination( senderTopic );
		
		startReciever();
		startSender();
		
	}

	/**
	 * @param inDestination
	 */
	private void createDestination( String inDestination )
	{
		 String url = "tcp://localhost:3035/";
		 String user = "admin";
		 String password = "openjms";
		 
		 try
		 {
			 JmsAdminServerIfc admin = AdminConnectionFactory.create(url, user, password);

			 Boolean isQueue = Boolean.TRUE;
			 if (!admin.addDestination(inDestination, isQueue)) {
//				 System.err.println("Failed to create queue " + inDestination);
			 }
		 } 
		 catch (Exception e1)
		 {
			 e1.printStackTrace();
		 }
	}
	
	public void startSender()
	{
		Runnable run = new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
					properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");


					Context context = new InitialContext(properties);

					ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

					Connection connection = factory.createConnection();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

					Destination destination = (Destination) context.lookup(senderTopic);
					
					connection.start();
				    MessageProducer sender = session.createProducer(destination);
				    
				    for(;;)
				    {
				    	for( QPMessage sub : subscribedInstruments.toArray( new QPMessage[]{} ) )
				    	{
				    		double bid = rand.nextDouble();
				    		double ask = rand.nextDouble();

				    		QPMessage quoteMessage = new QPMessage( QPMessage.QUOTE, sub.ccy1, sub.ccy2, sub.period, bid, ask );
				    		TextMessage message = session.createTextMessage( quoteMessage.toString() );

				    		sender.send(message);

				    		int sleepTime = rand.nextInt( 1000 );
				    		Thread.sleep( sleepTime );
				    	}
				    }
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}

		};
		
		Thread thread = new Thread(run);
		thread.start();
	}
	
	public void startReciever( )
	{
		Runnable run = new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
					properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");


					Context context = new InitialContext(properties);

					ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

					Connection connection = factory.createConnection();

					Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					
					Destination destination = (Destination) context.lookup(recieverTopic);
					
					MessageConsumer receiver = session.createConsumer(destination);
				    receiver.setMessageListener(new MessageListener() {
				        public void onMessage(Message message) {
				        	try
				        	{
				        		TextMessage textMessage = (TextMessage)message;
				        		String messageStr = textMessage.getText();
				        		
				        		QPMessage mess = new QPMessage( messageStr );
				        		
				        		if( mess.type.equals( QPMessage.SUBSCRIBE ) )
				        		{
				        			subscribedInstruments.add( mess );
				        		}
				        		else if( mess.type.equals( QPMessage.UNSUBSCRIBE ) )
				        		{
				        			Iterator<QPMessage> iter = subscribedInstruments.iterator();
				        			while( iter.hasNext() )
				        			{
				        				QPMessage sub = iter.next();
				        				if( sub.ccy1.equals( mess.ccy1 ) && sub.ccy2.equals(  mess.ccy2 ) && sub.period.equals(  mess.period ) )
				        				{
				        					iter.remove();
				        				}
				        			}
				        		}
				        		
				        	}catch (Exception e)
				        	{
				        		e.printStackTrace();
				        	}
				        }
				    });

				    // start the connection to enable message delivery
				    connection.start();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		Thread thread = new Thread(run);
		thread.start();
	}
}