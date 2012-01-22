package org.juxtapose.fasid.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.juxtapose.fasid.stm.exp.DataTransaction;
import org.juxtapose.fasid.stm.exp.ISTM;
import org.juxtapose.fasid.stm.impl.ReferenceLink;
import org.juxtapose.fasid.stm.impl.TemporaryController;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

/**
 * @author Pontus Jörgne
 * Jan 8, 2012
 * Copyright (c) Pontus Jörgne. All rights reserved
 */
public abstract class DataProducer extends TemporaryController implements IDataProducer
{
	private final HashMap<Integer, ReferenceLink> keyToReferensLinks = new HashMap<Integer, ReferenceLink>();
	protected final IDataKey dataKey;
	protected final ISTM stm;

	/**
	 * @param inKey
	 * @param inSTM
	 */
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
	private void disposeAllReferenceLinks( )
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
	public ReferenceLink removeReferenceLink( Integer inField )
	{
		return keyToReferensLinks.remove( inField );
	}
	
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#addDataReferences(java.util.Map)
	 * initDataReference is always done within STM sync and IDataKey lock.
	 */
	public void addDataReferences( Integer inFieldKey, ReferenceLink inLink )
	{
		keyToReferensLinks.put( inFieldKey, inLink );
	}
	
	protected void stop()
	{
		disposeAllReferenceLinks();
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#referencedDataUpdated(java.lang.Integer, org.juxtapose.fasid.util.IPublishedData)
	 * TODO Should be package private to ensure always called from ReferenceLink
	 */
	public void referencedDataUpdated( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData )
	{
		stm.commit( new DataTransaction( dataKey.getKey(), this )
		{
			@Override
			public void execute()
			{
				DataType<?> dataAtKey = get( inFieldKey );
				if( dataAtKey == null || !(dataAtKey instanceof DataTypeRef) )
				{
					//Reference has been removed in publishedDataObject
					return;
				}
				IDataKey key = (IDataKey)dataAtKey.get();
				if( !key.equals( inLink.getRef().get() ) )
				{
					//Reference has been replaced by another reference
					return;
				}
				DataTypeRef newRef = new DataTypeRef( inLink.getRef().get(), inData );
				updateReferenceValue(inFieldKey, newRef);
				
				referenceDataCall( inFieldKey, inLink, inData, this );
			}
		});
		
		postReferenceDataCall( inFieldKey, inLink, inData );
	}
	
	/**
	 * @param inFieldKey
	 * @param inLink
	 * @param inData
	 * @param inTransaction
	 * To be overridden by subclasses that to continue the work on a transaction after the referenced Data has been updated
	 */
	protected void referenceDataCall( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData, DataTransaction inTransaction )
	{
		
	}
	
	/**
	 * @param inFieldKey
	 * @param inLink
	 * @param inData
	 * To be overridden by subclass that needs to take action after referenced Data has been updated and transaction completed
	 */
	protected void postReferenceDataCall( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData )
	{
		
	}
	
}
