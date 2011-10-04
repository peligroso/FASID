package org.juxtapose.fasid.stm.exp;

import static org.juxtapose.fasid.util.producerservices.ProducerServiceUtil.STM_SERVICE_KEY;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.IPublishedData;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class STMUtil {

	public static String PRODUCER_SERVICES = "PRODUCER_SERVICES";
	
	public static IDataKey PRODUCER_SERVICE_KEY = ProducerUtil.createDataKey( STM_SERVICE_KEY, PRODUCER_SERVICES );
	
	/**
	 * @param inData
	 * @param inTransaction
	 * @return
	 */
	public static boolean validateProducerToData( IPublishedData inData, Transaction inTransaction )
	{
		IDataProducer dataProd = inData.getProducer();
		if( dataProd != null )
		{
			IDataProducer transactionProducer = inTransaction.producedBy();
			if( transactionProducer != null && transactionProducer == dataProd )
				return true;
			else
				return false;
		}
		return true;
	}
	
}
