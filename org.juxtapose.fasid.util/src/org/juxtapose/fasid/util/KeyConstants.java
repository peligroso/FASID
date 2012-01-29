package org.juxtapose.fasid.util;

import static org.juxtapose.fasid.util.producerservices.ProducerServiceConstants.STM_SERVICE_KEY;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.stm.impl.STMUtil;

public class KeyConstants
{
	public static IDataKey PRODUCER_SERVICE_KEY = ProducerUtil.createDataKey( STM_SERVICE_KEY, STMUtil.PRODUCER_SERVICES, STMUtil.PRODUCER_SERVICES );
}
