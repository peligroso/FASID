package org.juxtapose.fxtradingsystem.marketdata;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.impl.DataTransaction;
import org.juxtapose.fasid.stm.impl.ISTM;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeBigDecimal;
import org.juxtapose.fasid.util.data.DataTypeLong;
import org.juxtapose.fxtradingsystem.quoteprovider.QPMessage;
import org.juxtapose.fxtradingsystem.quoteprovider.QuoteProvider;

/**
 * @author Pontus Jörgne
 * Jan 22, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class MarketDataProducer extends DataProducer
{
	final String source;
	final String ccy1;
	final String ccy2;
	final String period;
	
	/**
	 * @param inKey
	 * @param inSTM
	 */
	public MarketDataProducer( IDataKey inKey, ISTM inSTM )
	{
		super( inKey, inSTM );
		
		source = dataKey.getValue( MarketDataConstants.FIELD_SOURCE );
		ccy1 = dataKey.getValue( MarketDataConstants.FIELD_CCY1 );
		ccy2 = dataKey.getValue( MarketDataConstants.FIELD_CCY2 );
		period = dataKey.getValue( MarketDataConstants.FIELD_PERIOD );
	}
	@Override
	protected void start()
	{
		if( source == null || ccy1 == null || ccy2 == null || period == null )
		{
			stm.logError( "Missing required field in MarketDataProducer" );
			return;
		}
		
		try
		{
			startListener();
			startSubscription();
		}catch( Exception e )
		{
			stm.logError( e.getMessage(), e );
		}
	}
	
	public void startListener( )throws Exception
	{
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");


		Context context = new InitialContext(properties);

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = (Destination) context.lookup(QuoteProvider.SENDER_PREFIX+source);
		MessageConsumer receiver = session.createConsumer(destination);
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				try
				{
					String textMess = ((TextMessage)message).getText();
					final QPMessage mess = new QPMessage( textMess );
					
					if( mess.type.equals( QPMessage.QUOTE ) )
					{
						if( ccy1.equals(  mess.ccy1 ) && ccy2.equals( mess.ccy2 ) && period.equals( mess.period ))
						{
							stm.execute( new Runnable() {
								public void run()
								{
									parseQuote( mess );
								}
							});
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
	
	public void startSubscription( ) throws Exception
	{
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
		properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");


		Context context = new InitialContext(properties);

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

		Connection connection = factory.createConnection();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = (Destination) context.lookup(QuoteProvider.RECIEVER_PREFIX+source);

		connection.start();
		MessageProducer sender = session.createProducer(destination);

		QPMessage subMessage = new QPMessage( QPMessage.SUBSCRIBE, ccy1, ccy2, period);//ccy1, ccy2, period);
		TextMessage message = session.createTextMessage( subMessage.toString() );

		sender.send(message);

		session.close();
		connection.close();

	}
	
	protected void stop()
	{
		super.stop();
	}
	
	public void parseQuote( final QPMessage inQuote )
	{
		stm.commit( new DataTransaction(dataKey.getKey(), this)
		{
			@Override
			public void execute()
			{
				putValue( MarketDataConstants.FIELD_BID, new DataTypeBigDecimal( inQuote.bid ));
				putValue( MarketDataConstants.FIELD_ASK, new DataTypeBigDecimal( inQuote.ask ));
				
				Long timeStamp = System.nanoTime();
				
				putValue( MarketDataConstants.FIELD_TIMESTAMP, new DataTypeLong( timeStamp ));
				
				if( getStatus() != Status.OK )
					setStatus( Status.OK );
			}
		});
	}
	
}
