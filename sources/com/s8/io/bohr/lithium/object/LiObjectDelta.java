package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.type.BuildScope;


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
	public abstract void operate(LiGraph graph, BuildScope scope) throws S8IOException;

	
	/**
	 * 
	 * @param outbound
	 * @param outflow
	 * @throws IOException
	 */
	public abstract void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException;

	
}
