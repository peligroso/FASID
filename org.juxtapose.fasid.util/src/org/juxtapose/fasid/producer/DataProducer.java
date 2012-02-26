package org.juxtapose.fasid.producer;

import java.util.HashMap;
import java.util.Map;

import org.juxtapose.fasid.stm.DataProducerDependencyController;
import org.juxtapose.fasid.stm.STMTransaction;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.stm.ReferenceLink;
import org.juxtapose.fasid.stm.TemporaryController;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeRef;

/**
 * @author Pontus J�rgne
 * Jan 8, 2012
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public abstract class DataProducer extends TemporaryController implements IDataProducer, IDataSubscriber
{
	private final HashMap<String, TemporaryController> dependencies = new HashMap<String, TemporaryController>();
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
	
	private void disposeAllDependencies()
	{
		for( TemporaryController controller : dependencies.values() )
		{
			controller.dispose();
		}
		
		dependencies.clear();
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
	 * @see org.juxtapose.fasid.producer.IDataProducer#removeDependency(java.lang.String)
	 */
	public TemporaryController removeDependency( String inDataKey )
	{
		TemporaryController controller = dependencies.remove( inDataKey );
		if( controller == null )
			stm.logError( "Tried to remove not existing dependency" );
		
		return controller;
	}

	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#addDataReferences(java.util.Map)
	 * initDataReference is always done within STM sync and IDataKey lock.
	 */
	public void addDataReferences( Integer inFieldKey, ReferenceLink inLink )
	{
		assert keyToReferensLinks.get( inFieldKey ) == null : "Reference already exists";
		keyToReferensLinks.put( inFieldKey, inLink );
	}
	
	public void addDependency( String inKey, TemporaryController inController )
	{
		if( dependencies.containsKey( inKey ))
		{
			stm.logError( "Dependency for "+inKey+" is already added to "+dataKey );
			return;
		}
		dependencies.put( inKey, inController );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.TemporaryController#stop()
	 * Stop is only called via TemporaryController.dispose from STM
	 */
	protected void stop()
	{
		disposeAllDependencies();
		disposeAllReferenceLinks();
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.producer.IDataProducer#referencedDataUpdated(java.lang.Integer, org.juxtapose.fasid.util.IPublishedData)
	 * TODO Can this method be package private to ensure always called from ReferenceLink
	 */
	public void referencedDataUpdated( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData )
	{
		stm.commit( new STMTransaction( dataKey, this, 0, 0 )
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
	protected void referenceDataCall( final Integer inFieldKey, final ReferenceLink inLink, final IPublishedData inData, STMTransaction inTransaction )
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
	
	protected void setStatus( final Status inStatus )
	{
		stm.commit( new STMTransaction( dataKey, DataProducer.this, 0, 0 )
		{
			@Override
			public void execute()
			{
				setStatus( inStatus );
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.IDataSubscriber#updateData(org.juxtapose.fasid.producer.IDataKey, org.juxtapose.fasid.util.IPublishedData, boolean)
	 */
	public void updateData( IDataKey inKey, final IPublishedData inData, boolean inFirstUpdate )
	{
		
	}
	
}
