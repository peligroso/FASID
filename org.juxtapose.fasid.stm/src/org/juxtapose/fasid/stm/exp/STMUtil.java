package org.juxtapose.fasid.stm.exp;

import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.ProducerUtil;
import static org.juxtapose.fasid.util.producerservices.ProducerServiceUtil.STM_SERVICE_KEY;

/**
 * @author Pontus Jörgne
 * 28 jun 2011
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public class STMUtil {

	public static String PRODUCER_SERVICES = "PRODUCER_SERVICES";
	
	public static IDataKey PRODUCER_SERVICE_KEY = ProducerUtil.createDataKey( STM_SERVICE_KEY, PRODUCER_SERVICES );
	
}
