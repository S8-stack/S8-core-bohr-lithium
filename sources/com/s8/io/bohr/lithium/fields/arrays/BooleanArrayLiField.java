package com.s8.io.bohr.lithium.fields.arrays;

import java.io.IOException;
import java.io.Writer;

import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldComposer;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.fields.LiFieldParser;
import com.s8.io.bohr.lithium.fields.LiFieldPrototype;
import com.s8.io.bohr.lithium.fields.primitives.PrimitiveLiField;
import com.s8.io.bohr.lithium.handlers.LiHandler;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.ResolveScope;


/**
 * later aggregate
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BooleanArrayLiField extends PrimitiveArrayLiField {

	/**
	 * 
	 */
	public final static Prototype PROTOTYPE = new Prototype(boolean[].class){
		@Override
		public PrimitiveLiField.Builder createFieldBuilder(LiFieldProperties properties, LiHandler handler) {
			return new Builder(properties, handler);
		}
	};



	/**
	 * 
	 * @author pierreconvert
	 *
	 */
	private static class Builder extends PrimitiveLiField.Builder {

		public Builder(LiFieldProperties properties, LiHandler handler) {
			super(properties, handler);
		}

		@Override
		public LiFieldPrototype getPrototype() {
			return PROTOTYPE;
		}

		@Override
		public LiField build(int ordinal) throws S8BuildException {
			return new BooleanArrayLiField(ordinal, properties, handler);
		}		
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	public BooleanArrayLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException {
		super(ordinal, properties, handler);
	}


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		boolean[] array = (boolean[]) handler.get(object);
		if(array!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(array.length*4);
		}
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		boolean[] array = (boolean[]) handler.get(origin);
		handler.set(clone, clone(array));
	}

	@Override
	public BooleanArrayLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new BooleanArrayLiFieldDelta(this, (boolean[]) handler.get(object));
	}
	

	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (boolean[])");
	}


	/**
	 * 
	 * @param base
	 * @return
	 */
	private boolean[] clone(boolean[] base) {
		if(base!=null) {
			int n = base.length;
			boolean[] copy = new boolean[n];
			for(int i=0; i<n; i++) {
				copy[i] = base[i];
			}
			return copy;
		}
		else {
			return null;
		}
	}



	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		boolean[] array = (boolean[]) handler.get(object);
		if(array!=null) {
			boolean isInitialized = false;
			writer.write('[');
			int n = array.length;
			for(int i=0; i<n; i++) {
				if(isInitialized) {
					writer.write(" ,");	
				}
				else {
					isInitialized = true;
				}
				writer.write(Boolean.toString(array[i]));
			}
			writer.write(']');
		}
		else {
			writer.write("null");
		}
	}


	
	/* <IO-inflow-section> */
	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {


		int code = inflow.getUInt8();
		if(code != BOHR_Types.ARRAY) {
			throw new IOException("Only array accepted");
		}

		switch(code = inflow.getUInt8()) {

		case BOHR_Types.BOOL8 : return new BOOL8_Inflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private class BOOL8_Inflow extends LiFieldParser {

		@Override
		public BooleanArrayLiField getField() {
			return BooleanArrayLiField.this;
		}

		@Override
		public BooleanArrayLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new BooleanArrayLiFieldDelta(getField(), deserialize(inflow));
		}

		public boolean[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				boolean[] values = new boolean[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getBool8(); }
				return values;
			}
			else { return null; }
		}
	}



	/* </IO-inflow-section> */


	/* <IO-outflow-section> */

	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case DEFAULT_FLOW_TAG: case "bool8[]" : return new DefaultComposer(code);

		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private class DefaultComposer extends LiFieldComposer {

		public DefaultComposer(int code) { super(code); }

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.BOOL8);
		}

		@Override
		public BooleanArrayLiField getField() {
			return BooleanArrayLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((BooleanArrayLiFieldDelta) delta).value);
		}

		public void serialize(ByteOutflow outflow, boolean[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putBool8(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}

	}

	/* <IO-outflow-section> */
}
