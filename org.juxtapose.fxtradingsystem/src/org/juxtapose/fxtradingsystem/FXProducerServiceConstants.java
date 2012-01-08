package org.juxtapose.fxtradingsystem;

import org.juxtapose.fasid.util.producerservices.ProducerServiceConstants;

public class FXProducerServiceConstants extends ProducerServiceConstants
{
	public static int BASE = ProducerServiceConstants.getBase();
	
	public static int PRICE_ENGINE = BASE;
	public static int ORDER_MANAGER = BASE+1;
	public static int AGGREGATOR = BASE+2;
}
