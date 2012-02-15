package org.juxtapose.fasid.util.subscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.juxtapose.fasid.producer.IDataKey;
import org.juxtapose.fasid.stm.ISTM;
import org.juxtapose.fasid.util.IDataSubscriber;
import org.juxtapose.fasid.util.IPublishedData;

/**
 * @author Pontus J�rgne
 * Feb 2, 2012
 * Copyright (c) Pontus J�rgne. All rights reserved
 */
public class DataSequencer implements IDataSubscriber
{
	ConcurrentHashMap<Long, Sequence> queue = new ConcurrentHashMap<Long, Sequence>();
	AtomicReference<Sequence> polePosition = new AtomicReference<Sequence>(Sequence.INIT_SEQUENCE);

	final ISequencedDataSubscriber subscriber;
	final ISTM stm;
	final IDataKey key;

	/**
	 * @param inSubscriber
	 * @param inSTM
	 * @param inKey
	 */
	public DataSequencer( ISequencedDataSubscriber inSubscriber, ISTM inSTM, IDataKey inKey )
	{
		subscriber = inSubscriber;
		stm = inSTM;
		key = inKey;
		
		stm.subscribeToData( key, this );
	}
	/**
	 * @return
	 */
	 private boolean updatePolePosition()
	 {
		 Sequence next = polePosition.get();
		 if( next.type == Sequence.TYPE_NO_OBJ )
		 {
			 Sequence nextSeq = queue.remove( next.id );
			 if( nextSeq != null )
			 {
				 return polePosition.compareAndSet( next, nextSeq );
			 }
		 }
		 return false;
	 }
	 
	 private boolean trySequence( Sequence inPoleObj, int inType, Long inID, Sequence inTrySequence )
	 {
		 if( inPoleObj.type == inType && inPoleObj.id.equals( inID ))
		 {
			 if( polePosition.compareAndSet( inPoleObj, inTrySequence ) )
			 {
				 subscriber.dataUpdated( this );
				 assert polePosition.get().type == Sequence.TYPE_NO_OBJ : "Object has not been remove from pole position";

				 while( updatePolePosition() )
				 {
					 subscriber.dataUpdated( this );
					 assert polePosition.get().type == Sequence.TYPE_NO_OBJ : "Object has not been remove from pole position";
				 }
				 return true;
			 }
		 }
		 return false;
	 }

	/* (non-Javadoc)
	 * @see org.juxtapose.fasid.util.IDataSubscriber#updateData(java.lang.String, org.juxtapose.fasid.util.IPublishedData, boolean)
	 */
	public void updateData(String inKey, IPublishedData inData, boolean inFirstUpdate)
	{
		Sequence syncObj = new Sequence( inData.getSequenceID(), inData, Sequence.TYPE_OBJ);

		Sequence poleObj = polePosition.get();
		
		if( trySequence( poleObj, Sequence.TYPE_NO_OBJ, syncObj.id, syncObj ) )
			return;
		
		if( trySequence( Sequence.INIT_SEQUENCE, Sequence.TYPE_INIT, Sequence.INIT_SEQUENCE.id, syncObj ) )
			return;
		
		else
		{
			do
			{
				poleObj = polePosition.get();
				queue.remove( syncObj.id );

				if( trySequence( poleObj, Sequence.TYPE_NO_OBJ, syncObj.id, syncObj ) )
					return;
				else if( poleObj.id >= syncObj.id )
				{
					return;
				}
				else
				{
					queue.put( syncObj.id, syncObj );
				}
			}while( polePosition.get() != poleObj );
		}
	}

	 /**
	 * @return
	 */
	public IPublishedData get()
	 {
		 Sequence ret = polePosition.get();
		 Sequence inBetweenSequence = new Sequence( ret.id+1, null, Sequence.TYPE_NO_OBJ );
		 polePosition.set( inBetweenSequence );

		 return ret.object;
	 }

}


