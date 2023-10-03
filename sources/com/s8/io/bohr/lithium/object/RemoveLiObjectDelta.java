package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.type.BuildScope;


/**
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class RemoveLiObjectDelta extends LiObjectDelta {

	public RemoveLiObjectDelta(String index) {
		super(index);
	}

	@Override
	public void operate(LiGraph graph, BuildScope scope) throws S8IOException {
		// TODO
	}

	@Override
	public void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException {
		
		/* remove node */
		outflow.putUInt8(BOHR_Keywords.REMOVE_NODE);

		/* define index */
		outflow.putStringUTF8(id);
	}
	
}
