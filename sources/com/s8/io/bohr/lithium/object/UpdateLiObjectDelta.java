package com.s8.io.bohr.lithium.object;

import java.io.IOException;
import java.util.List;

import com.s8.io.bohr.atom.BOHR_Keywords;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.branches.LiVertex;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;
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
public class UpdateLiObjectDelta extends LiObjectDelta {

	
	public final LiType type;

	public final List<LiFieldDelta> deltas;
	
	
	/**
	 * 
	 * @param index
	 * @param type
	 * @param deltas
	 */
	public UpdateLiObjectDelta(String index, LiType type, List<LiFieldDelta> deltas) {
		super(index);
		

		// type
		this.type = type;
		
		// deltas
		this.deltas = deltas;
		
	}


	@Override
	public void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException {

		
		LiTypeComposer composer = outbound.getComposer(type.getRuntimeName());
		
		/*  advertise diff type: publish a create node */

		/* pass flag */
		outflow.putUInt8(BOHR_Keywords.UPDATE_NODE);

		/* pass index */
		outflow.putStringUTF8(id);

		// produce all diffs
		for(LiFieldDelta delta : deltas) {
			int ordinal = delta.getField().ordinal;
			composer.fieldComposers[ordinal].compose(delta, outflow);
		}

		/* Close node */
		outflow.putUInt8(BOHR_Keywords.CLOSE_NODE);
	}



	@Override
	public void operate(LiBranch branch, BuildScope scope) {

		try {
			// retrieve vertex
			LiVertex vertex = branch.getVertex(id);

			if(vertex==null) {
				throw new S8Exception("failed to retrieve vertex for index: "+id);
			}

			// retrieve object
			LiObject object = vertex.object;

			/* consume diff */
			for(LiFieldDelta delta : deltas) { delta.operate(object, scope); }

		}
		catch (Exception e) {
			e.printStackTrace();
		}	
	}


	/*
	public static UpdateLiObjectDelta deserialize(LithCodebaseIO codebaseIO, ByteInflow inflow) {
		UpdateLiObjectDelta objectDelta = new UpdateLiObjectDelta();
		objectDelta.deserializeBody(codebaseIO, inflow);
		return objectDelta;
	}
	 */


}
