package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.branches.LiVertex;
import com.s8.io.bohr.lithium.type.BuildScope;


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
	public void operate(LiGraph graph, BuildScope scope) throws S8IOException {

		/* retrieve vertex */
		LiVertex vertex = graph.getVertex(id);

		/* graph expose  */
		graph.expose(slot, vertex != null ? vertex.object : null);	

	}


}

