package com.s8.core.bohr.lithium.object;

import java.io.IOException;
import java.util.List;

import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.space.objects.SpaceS8Object;
import com.s8.core.bohr.atom.protocol.BOHR_Keywords;
import com.s8.core.bohr.lithium.branches.LiGraph;
import com.s8.core.bohr.lithium.branches.LiOutbound;
import com.s8.core.bohr.lithium.branches.LiVertex;
import com.s8.core.bohr.lithium.fields.LiFieldDelta;
import com.s8.core.bohr.lithium.type.BuildScope;
import com.s8.core.bohr.lithium.type.LiType;
import com.s8.core.bohr.lithium.type.LiTypeComposer;


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
	public void operate(LiGraph graph, BuildScope scope) {

		try {
			// retrieve vertex
			LiVertex vertex = graph.getVertex(id);

			if(vertex==null) {
				throw new S8IOException("failed to retrieve vertex for index: "+id);
			}

			// retrieve object
			SpaceS8Object object = vertex.object;

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
