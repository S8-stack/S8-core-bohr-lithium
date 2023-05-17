package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.s8.io.bohr.atom.BOHR_Keywords;
import com.s8.io.bohr.atom.S8BuildException;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.exceptions.LiBuildException;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeComposer;
import com.s8.io.bytes.alpha.ByteOutflow;

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




	private final LiBranch branch;


	

	public LiOutbound(LiBranch branch) {
		super();
		this.branch = branch;
		this.codebase = branch.codebase;
	}



	/**
	 * 
	 * @param type
	 * @return
	 * @throws LiIOException
	 */
	public LiTypeComposer getComposer(Class<?> type) throws LiIOException {
		String runtimeName = type.getName();
		return getComposer(runtimeName);
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws LiIOException
	 */
	public LiTypeComposer getComposer(String runtimeTypeName) throws LiIOException {
		LiTypeComposer composer = composers.computeIfAbsent(runtimeTypeName, name -> {
			LiType nType = codebase.getTypeByRuntimeName(name);
			try {
				return new LiTypeComposer(nType, ++typeCode);
			} 
			catch (LiBuildException e) {
				e.printStackTrace();
				return null;
			}
		});
		if(composer != null) { return composer; }
		else {
			throw new LiIOException("failed to build composer");
		}
	}




	/**
	 * 
	 * @param outflow
	 * @throws IOException 
	 * @throws S8Exception 
	 * @throws S8BuildException 
	 */
	public void compose(ByteOutflow outflow) throws S8BuildException, S8Exception, IOException {

		outflow.putUInt8(BOHR_Keywords.OPEN_SEQUENCE);

		outflow.putUInt8(BOHR_Keywords.OPEN_JUMP);

		if(hasUnpublishedChanges) {
			LiVertex vertex;
			while((vertex = unpublishedVertices.poll()) != null) {
				vertex.publish(outflow);
			}


			// expose if necessary
			if(!unpublishedSlotExposure.isEmpty()) {
				unpublishedSlotExposure.forEach(slot -> {
					String id = branch.exposure[slot].id;
					try {
						publish_EXPOSE_NODE(outflow, id, slot);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				});
			}



			hasUnpublishedChanges = false;
		}

		outflow.putUInt8(BOHR_Keywords.CLOSE_JUMP);

		outflow.putUInt8(BOHR_Keywords.CLOSE_SEQUENCE);
	}





	public LiBranch getBranch() {
		return branch;
	}




	/**
	 * 
	 * @param outflow
	 * @param index
	 * @param slot
	 * @throws IOException
	 */
	public static void publish_EXPOSE_NODE(ByteOutflow outflow, String index, int slot) throws IOException {

		/* UPDATE_AND_EXPOSE_NODE */
		outflow.putUInt8(BOHR_Keywords.EXPOSE_NODE);

		/* pass index */
		outflow.putStringUTF8(index);

		/* pass index */
		outflow.putUInt8(slot);	
	}

}
