package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeComposer;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LiOutbound {


	private final LiCodebase codebase;

	private long typeCode = 0;

	/**
	 * 
	 */
	private final Map<String, LiTypeComposer> composers = new HashMap<>();


	public LiOutbound(LiCodebase codebase) {
		super();
		this.codebase = codebase;
	}



	/**
	 * 
	 * @param type
	 * @return
	 * @throws S8IOException
	 */
	public LiTypeComposer getComposer(Class<?> type) throws S8IOException {
		String runtimeName = type.getName();
		return getComposer(runtimeName);
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws S8IOException
	 */
	public LiTypeComposer getComposer(String runtimeTypeName) throws S8IOException {
		LiTypeComposer composer = composers.computeIfAbsent(runtimeTypeName, name -> {
			LiType nType = codebase.getTypeByRuntimeName(name);
			try {
				return new LiTypeComposer(nType, ++typeCode);
			} 
			catch (S8BuildException e) {
				e.printStackTrace();
				return null;
			}
		});
		if(composer != null) { return composer; }
		else {
			throw new S8IOException("failed to build composer");
		}
	}



	/**
	 * 
	 * @param outflow
	 * @param deltas
	 * @throws IOException
	 */
	private void composeSequence(ByteOutflow outflow, List<LiGraphDelta> deltas) throws IOException {
		outflow.putUInt8(BOHR_Keywords.OPEN_SEQUENCE);
		for(LiGraphDelta delta : deltas){
			delta.serialize(this, outflow);
		}
		outflow.putUInt8(BOHR_Keywords.CLOSE_SEQUENCE);
	}
	
	

	/**
	 * 
	 * @param outflow
	 * @throws IOException
	 */
	public void pushFrame(ByteOutflow outflow, List<LiGraphDelta> deltas) throws IOException {
		outflow.putByteArray(BOHR_Keywords.FRAME_HEADER);
		composeSequence(outflow, deltas);
		outflow.putByteArray(BOHR_Keywords.FRAME_FOOTER);
	}

}
