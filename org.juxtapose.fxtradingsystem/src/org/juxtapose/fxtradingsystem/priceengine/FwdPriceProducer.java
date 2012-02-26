package org.juxtapose.fxtradingsystem.priceengine;

import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_EUR;
import static org.juxtapose.fxtradingsystem.priceengine.PriceEngineDataConstants.STATE_SEK;

import java.util.HashMap;

import org.juxtapose.fasid.producer.DataProducer;
import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.DataTransaction;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.IDataRequestSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataTypeRef;
import org.juxtapose.fxtradingsystem.FXProducerServiceConstants;

public class FwdPriceProducer extends DataProducer implements IDataRequestSubscriber
{
	long spotTag = 0;
	long swapTag = 1; 
	
	IDataKey spotDataKey;
	IDataKey swapDataKey;
	
	final String ccy1;
	final String ccy2;
	final String period;
	
	public FwdPriceProducer( IDataKey inKey, String inCcy1, String inCcy2, String inPeriod, ISTM inSTM )
	{
		super( inKey, inSTM );
		ccy1 = inCcy1;
		ccy2 = inCcy2;
		period = inPeriod;
	}
	
	public void linkData()
	{
		HashMap<Integer, String> querySp = PriceEngineUtil.getSpotPriceQuery( STATE_EUR, STATE_SEK );
		stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, this, spotTag, querySp );
		
		HashMap<Integer, String> querySw = PriceEngineUtil.getFwdPriceQuery( STATE_EUR, STATE_SEK, period );
		stm.getDataKey( FXProducerServiceConstants.PRICE_ENGINE, this, swapTag, querySw );
	}
	
	@Override
	protected void start()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateData(String inKey, IPublishedData inData, boolean inFirstUpdate)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliverKey(IDataKey inDataKey, Long inTag)
	{
		if( inTag.equals( spotTag ) )
		{
			spotDataKey = inDataKey;
		}
		else if( inTag.equals( swapDataKey ))
		{
			swapDataKey = inDataKey;
		}
		
		if( swapDataKey != null && spotDataKey != null )
		{
			stm.commit( new DataTransaction( dataKey.getKey(), FwdPriceProducer.this, 2, 0 )
			{
				@Override
				public void execute()
				{
					addReference( PriceEngineDataConstants.FIELD_SPOT, new DataTypeRef( spotDataKey ) );
					addReference( PriceEngineDataConstants.FIELD_NEAR_SWAP, new DataTypeRef( swapDataKey ) );
				}
			});
		}
	}
	

	@Override
	public void queryNotAvailible(Long inTag)
	{
		setStatus( Status.ERROR );
		stm.logError( "could not retrieve datakey from market data" );
		return;
		
	}

}
