package org.juxtapose.fasid.producer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.stm.impl.ReferenceLink;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataTypeRef;

public abstract class DataProducer implements IDataProducer
{
	private final ConcurrentHashMap<Integer, ReferenceLink> keyToReferensLinks = new ConcurrentHashMap<Integer, ReferenceLink>();
	protected final IDataKey dataKey;
	protected final ISTM stm;
	
	public DataProducer( IDataKey inKey, ISTM inSTM )
	{
		dataKey = inKey;
		stm = inSTM;
	}
	/**
	 * @param inKey
	 * Needs external Synchronization on dataKey
	 */
	protected Map<Integer, ReferenceLink> getReferensList( String inKey )
	{
		return keyToReferensLinks;
	}
	
	/**
	 * @param inKey
	 * Needs external Synchronization on dataKey
	 */
	public void disposeAllReferenceLinks( )
	{
		for( Integer key : keyToReferensLinks.keySet() )
		{
			ReferenceLink link = keyToReferensLinks.get( key );
			link.dispose();
		}
		keyToReferensLinks.clear();
	}
	
	/**
	 * @param inKey
	 * Needs external Synchronization on dataKey
	 */
	public void disposeReferenceLink( Integer inField )
	{
		ReferenceLink link = keyToReferensLinks.remove( inField );
		if( link != null )
		{
			link.dispose();
		}
	}
	
	/**
	 * @param inReferenceFields
	 */
	public void disposeReferenceLinks( List< Integer > inReferenceFields )
	{
		for( Integer refKey : inReferenceFields )
		{
			disposeReferenceLink( refKey );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#initDataReferences(java.util.Map)
	 */
	public void initDataReferences( Map< Integer, DataTypeRef > inDataReferences )
	{
		for( Integer refKey : inDataReferences.keySet() )
		{
			DataTypeRef ref = inDataReferences.get( refKey );
			ReferenceLink refLink = new ReferenceLink( this, stm, refKey, ref );
			
			keyToReferensLinks.put( refKey, refLink );
		}
	}
	
	public void stop()
	{
		disposeAllReferenceLinks();
	}
	
	public void referencedDataUpdated( final Integer inFieldKey, final IPublishedData inData )
	{
		final ReferenceLink link = keyToReferensLinks.get( inFieldKey );
		
		if( link == null )
		{
			stm.logError( "Tried to update referenceData without a reference link" );
			return;
		}
		
		stm.commit( new DataTransaction( dataKey.getKey() )
		{
			@Override
			public void execute()
			{
				DataTypeRef newRef = new DataTypeRef( link.getRef().get(), inData );
				updateReferenceValue(inFieldKey, newRef);
			}
		});
	}
	
	
}
