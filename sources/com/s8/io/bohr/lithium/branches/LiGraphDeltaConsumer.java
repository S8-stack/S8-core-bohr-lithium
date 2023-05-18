package com.s8.io.bohr.lithium.branches;

import com.s8.io.bohr.lithium.exceptions.LiIOException;


/**
 * 
 * @author pierreconvert
 *
 */
public interface LiGraphDeltaConsumer {

	
	/**
	 * 
	 * @param delta
	 * @throws LiIOException
	 */
	public void pushDelta(LiGraphDelta delta) throws LiIOException;
	
	
}
