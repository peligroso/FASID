package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.stm.impl.Transaction;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.ProducerUtil;
import static org.juxtapose.fasid.util.producerservices.ProducerServiceUtil.STM_SERVICE_KEY;

/**
 * @author Pontus J�rgne
 * 28 jun 2011
 * Copyright (c) Pontus J�rgne. All rights reserved
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
