package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.io.bohr.atom.BOHR_Keywords;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.branches.LiVertex;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bytes.alpha.ByteOutflow;


/**
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class ExposeLiObjectDelta extends LiObjectDelta {
	
	public final int slot;


	public ExposeLiObjectDelta(String index, int slot) {
		super(index);
		this.slot = slot;
	}

	@Override
	public void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException {

		outflow.putUInt8(BOHR_Keywords.EXPOSE_NODE);

		/* define index */
		outflow.putStringUTF8(id);

		/* define slot */
		outflow.putUInt8(slot);
	}



	@Override
	public void operate(LiGraph graph, BuildScope scope) throws LiIOException {
		
		/* retrieve vertex */
		LiVertex vertex = graph.getVertex(id);
		
		graph.expose(slot, vertex.object);
	}
	

}

