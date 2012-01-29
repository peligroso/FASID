package org.juxtapose.fxtradingsystem.marketdata;

import java.util.HashMap;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.producer.IDataProducer;
import org.juxtapose.fasid.producer.ProducerUtil;
import org.juxtapose.fasid.stm.osgi.DataProducerService;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fxtradingsystem.FXDataConstants;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;
import org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants;

public class MarketData extends DataProducerService implements IMarketDataService
{

	@Override
	public IDataKey getDataKey(HashMap<Integer, String> inQuery)
	{
		String type = inQuery.get( PriceEngineDataConstants.FIELD_TYPE );
		
		if( MarketDataConstants.STATE_TYPE_INSTRUMENT.equals( type ) )
		{
			String source = inQuery.get( MarketDataConstants.FIELD_SOURCE );
			String ccy1 = inQuery.get( MarketDataConstants.FIELD_CCY1 );
			String ccy2 = inQuery.get( MarketDataConstants.FIELD_CCY2 );
			String period = inQuery.get( MarketDataConstants.FIELD_PERIOD );

			if( source == null || ccy1 == null || ccy2 == null || period == null )
			{
				stm.logError( "Missing attribute for dataKey "+inQuery );
				return null;
			}
			
			return ProducerUtil.createDataKey( getServiceId(), MarketDataConstants.STATE_TYPE_INSTRUMENT, new Integer[]{MarketDataConstants.FIELD_SOURCE, FXDataConstants.FIELD_CCY1, FXDataConstants.FIELD_CCY2, FXDataConstants.FIELD_PERIOD},new String[]{source, ccy1, ccy2, period} );
		}
		return null;
	}

	@Override
	public IDataProducer getDataProducer(IDataKey inDataKey)
	{
		return new MarketDataProducer( inDataKey, stm );
	}

	@Override
	public void updateData(String inKey, IPublishedData inData, boolean inFirstUpdate)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getServiceId()
	{
		return FXProducerServiceConstants.MARKET_DATA;
	}

}
