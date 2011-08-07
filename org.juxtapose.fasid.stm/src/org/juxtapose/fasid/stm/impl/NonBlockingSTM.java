package org.juxtapose.fasid.stm.impl;

import org.juxtapose.fasid.util.Status;

public class NonBlockingSTM extends STM
{

	@Override
	public void commit(Transaction inTransaction)
	{
		String dataKey = inTransaction.getDataKey();

		PublishedData existingData;
		PublishedData newData;

		try
		{
			do
			{
				existingData = m_keyToData.get( dataKey );
				if( existingData == null )
				{
					//data has been removed due to lack of interest, transaction is discarded
					return;
				}

				inTransaction.putInitDataState( existingData.getDataMap() );
				inTransaction.execute();

				newData = existingData.setUpdatedData( inTransaction.getStateInstruction(), inTransaction.getDeltaState() );

			}
			while( !m_keyToData.replace( dataKey, existingData, newData ) );

		}catch( Exception e){}
		
	}

	@Override
	protected PublishedData createPublishedData(String inDataKey, Status initState)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void removePublishedData(String inDataKey)
	{
		// TODO Auto-generated method stub
		
	}

}
