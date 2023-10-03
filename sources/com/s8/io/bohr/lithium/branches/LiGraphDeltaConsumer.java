package com.s8.io.bohr.lithium.branches;

import com.s8.api.exceptions.S8IOException;


/**
 * 
 * @author pierreconvert
 *
 */
public interface LiGraphDeltaConsumer {

	
	/**
	 * 
	 * @param delta
	 * @throws S8IOException
	 */
	public void pushDelta(LiGraphDelta delta) throws S8IOException;
	
	
}
