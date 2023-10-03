package com.s8.io.bohr.lithium.object;

import java.io.IOException;
import java.util.List;

import com.s8.api.bohr.BOHR_Keywords;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.branches.LiGraph;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeComposer;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class CreateLiObjectDelta extends LiObjectDelta {

	public final List<LiFieldDelta> deltas;


	public final LiType type;

	

	public CreateLiObjectDelta(String index, LiType type, List<LiFieldDelta> deltas) {
		super(index);

		this.type = type;
		
		// deltas
		this.deltas = deltas;

	}

	@Override
	public void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException {

		LiTypeComposer composer = outbound.getComposer(type.getRuntimeName());
		
		/*  advertise diff type: publish a create node */
		composer.publish_CREATE_NODE(outflow, id);

		/* serialize field deltas */
		// produce all diffs
		for(LiFieldDelta delta : deltas) {
			int ordinal = delta.getField().ordinal;
			composer.fieldComposers[ordinal].compose(delta, outflow);
		}

		/* Close node */
		outflow.putUInt8(BOHR_Keywords.CLOSE_NODE);
	}



	@Override
	public void operate(LiGraph graph, BuildScope scope) throws S8IOException {

		// create object
		SpaceS8Object object = type.createNewInstance();


		/* consume diff */
		for(LiFieldDelta delta : deltas) { delta.operate(object, scope); }

		/* append */
		graph.append(id, object);
		
	}


}

