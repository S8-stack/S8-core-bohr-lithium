package com.s8.io.bohr.lithium.object;

import java.io.IOException;

import com.s8.io.bohr.atom.BOHR_Keywords;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bytes.alpha.ByteOutflow;


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
	public void operate(LiGraph graph, BuildScope scope) throws LiIOException {
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
