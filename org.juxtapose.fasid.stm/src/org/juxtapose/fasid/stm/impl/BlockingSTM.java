package org.juxtapose.fasid.stm.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.juxtapose.fasid.util.DataConstants;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.Status;
import org.juxtapose.fasid.util.data.DataType;
import org.juxtapose.fasid.util.data.DataTypeString;
import org.juxtapose.fasid.util.lock.HashStripedLock;
import org.juxtapose.fasid.util.producer.IDataKey;
import org.juxtapose.fasid.util.producer.IDataProducer;
import org.juxtapose.fasid.util.producer.IDataProducerService;

import com.trifork.clj_ds.IPersistentMap;
import com.trifork.clj_ds.IPersistentVector;
import com.trifork.clj_ds.PersistentHashMap;
import com.trifork.clj_ds.PersistentVector;

public class BlockingSTM extends STM
{
	public final boolean FAIR_LOCKING = true; 
	private ConcurrentHashMap<String, ReentrantLock> m_keyToLock = new ConcurrentHashMap<String, ReentrantLock>();
	
	/**Used for creation and deletion of DataKey locks**/
	protected HashStripedLock m_dataKeyMasterLock = new HashStripedLock( 256 );
	
	/**
	 * @param inDataKey
	 * @return
	 */
	protected PublishedData createPublishedData( IDataKey inDataKey, Status initState )
	{
		PublishedData data;
		
		m_dataKeyMasterLock.lock( inDataKey.getKey() );
		
		ReentrantLock newLock = new ReentrantLock( FAIR_LOCKING );
		ReentrantLock lock = m_keyToLock.putIfAbsent( inDataKey.getKey(), newLock );
		lock = lock == null ? newLock : lock;
		
		lock.lock();
		
		m_dataKeyMasterLock.unlock( inDataKey.getKey() );
		
		data = m_keyToData.get( inDataKey );
		
		if( data == null )
		{
			IDataProducerService producerService = m_idToProducerService.get( inDataKey.getService() );
			if( producerService == null )
			{
				System.err.print( "Key: "+inDataKey+" not valid, producer service does not exist"  );
				lock.unlock();
				return null;
			}
			IPersistentMap<String, DataType<?>> dataMap = PersistentHashMap.create( DataConstants.DATA_STATUS, new DataTypeString( initState.toString()));
			IPersistentMap<String, DataType<?>> lastUpdateMap = PersistentHashMap.emptyMap();
			IPersistentVector<IDataSubscriber> subscribers = PersistentVector.emptyVector();
			
			IDataProducer producer = producerService.getDataProducer( inDataKey );
			
			data = new PublishedData( dataMap, lastUpdateMap, subscribers, producer );
			
			m_keyToData.put( inDataKey.getKey() , data );
			
			lock.unlock();
			
			if( producer != null )
				producer.start();
		}
		else
		{
			lock.unlock();
		}
		
		
		return data;
	}
	
	/**
	 * @param inDataKey
	 */
	protected void removePublishedData( String inDataKey )
	{
		m_dataKeyMasterLock.lock( inDataKey );
		
		ReentrantLock lock = m_keyToLock.get( inDataKey );
		if( lock != null )
		{
			lock.lock();
			
			m_keyToData.remove( inDataKey );
			
			lock.unlock();
		}
		
		m_keyToLock.remove( inDataKey );
		
		m_dataKeyMasterLock.unlock( inDataKey );
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.impl.STM#commit(org.juxtapose.fasid.stm.impl.Transaction)
	 */
	public void commit( Transaction inTransaction )
	{
		String dataKey = inTransaction.getDataKey();
		ReentrantLock lock = m_keyToLock.get( dataKey );
		
		if( lock == null )
		{
			//data has been removed due to lack of interest, transaction is discarded
			return;
		}
		
		lock.lock();
		
		PublishedData existingData = m_keyToData.get( dataKey );
		if( existingData == null )
		{
			//data has been removed due to lack of interest, transaction is discarded
			return;
		}
		
		inTransaction.putInitDataState( existingData.getDataMap() );
		inTransaction.execute();
		IPersistentMap<String, DataType<?>> inst = inTransaction.getStateInstruction();
		IPersistentMap<String, DataType<?>> delta = inTransaction.getDeltaState();
		
		existingData.setUpdatedData( inst, delta );
		
		lock.unlock();
	}
	
	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.stm.impl.STM#subscribe(org.juxtapose.fasid.util.producer.IDataKey, org.juxtapose.fasid.util.IDataSubscriber)
	 */
	public void subscribeToData( IDataKey inDataKey, IDataSubscriber inSubscriber )
	{
		IDataProducerService producerService = m_idToProducerService.get( inDataKey.getService() );
		
		if( producerService == null )
			return;
		
		PublishedData data = m_keyToData.get( inDataKey.getKey() );
		
		if( data == null )
			createPublishedData( inDataKey, Status.ON_REQUEST );
		
		data = data.addSubscriber( inSubscriber );
		
		m_keyToData.put( inDataKey.getKey(), data );
		
//		String key = producerService.subscribe( inQuery );
//		
//		if( key == null )
//			return Status.NA;
//		
//		PublishedData data = m_keyToData.get( key );
//		
//		data.addSubscriber( inSubscriber );
//		
		return;
		
		//...
		
	}

}
