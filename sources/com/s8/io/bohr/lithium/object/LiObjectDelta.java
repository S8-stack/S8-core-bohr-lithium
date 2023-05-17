package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class LiObjectDelta {

	public final String index;



	public LiObjectDelta(String index) {
		super();
		this.index = index;
	}


	/**
	 * 
	 * @param shell
	 * @return
	 * @throws NdIOException 
	 * @throws IOException 
	 */
	public abstract void consume(LiBranch branch, BuildScope scope) throws LiIOException;

	
	/**
	 * 
	 * @param outbound
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException;

	
	/**
	 * 
	 * @param weight
	 */
	public abstract void computeFootprint(MemoryFootprint weight);
}
