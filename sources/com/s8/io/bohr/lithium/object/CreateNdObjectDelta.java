package com.s8.io.bohr.lithium.object;

import java.io.IOException;
import java.util.List;

import com.s8.io.bohr.atom.BOHR_Keywords;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiOutbound;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeComposer;
import com.s8.io.bohr.neodymium.fields.NdFieldDelta;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class CreateNdObjectDelta extends LiObjectDelta {

	public List<LiFieldDelta> deltas;


	public final LiType type;

	

	public CreateNdObjectDelta(String index, LiType type, List<LiFieldDelta> deltas) {
		super(index);

		this.type = type;
		
		// deltas
		this.deltas = deltas;

	}

	@Override
	public void serialize(LiOutbound outbound, ByteOutflow outflow) throws IOException {

		LiTypeComposer composer = outbound.getComposer(type.getRuntimeName());
		
		/*  advertise diff type: publish a create node */
		composer.publish_CREATE_NODE(outflow, index);

		/* serialize field deltas */
		// produce all diffs
		for(LiFieldDelta delta : deltas) {
			int ordinal = delta.getField().ordinal;
			composer.fieldComposers[ordinal].publish(delta, outflow);
		}

		/* Close node */
		outflow.putUInt8(BOHR_Keywords.CLOSE_NODE);
	}



	@Override
	public void consume(LiBranch branch, BuildScope scope) throws LiIOException {

		// create object
		LiS8Object object = type.createNewInstance();


		/* consume diff */
		for(LiFieldDelta delta : deltas) { delta.consume(object, scope); }

		/* append */
		branch.append(index, object);
		
	}


	@Override
	public void computeFootprint(MemoryFootprint weight) {

		weight.reportInstance();

		// fields
		if(deltas!=null) {
			for(NdFieldDelta delta : deltas) {
				delta.computeFootprint(weight);
			}
		}
	}

}

