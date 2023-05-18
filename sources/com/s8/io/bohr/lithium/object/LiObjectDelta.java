package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bytes.alpha.ByteOutflow;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class LiObjectDelta {

	public final String id;



	public LiObjectDelta(String index) {
		super();
		this.id = index;
	}


	/**
	 * 
	 * @param shell
	 * @return
	 * @throws NdIOException 
	 * @throws IOException 
	 */
	public abstract void operate(LiBranch branch, BuildScope scope) throws LiIOException;

	
	/**
	 * 
	 * @param outbound
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException;

	
}
