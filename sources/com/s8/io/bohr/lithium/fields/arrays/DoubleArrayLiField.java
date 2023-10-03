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
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class DoubleArrayLiField extends PrimitiveArrayLiField {

	/**
	 * 
	 */
	public final static Prototype PROTOTYPE = new Prototype(double[].class){
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
			return new DoubleArrayLiField(ordinal, properties, handler);
		}		
	}


	public @Override Prototype getPrototype() { return PROTOTYPE; }



	public DoubleArrayLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException {
		super(ordinal, properties, handler);
	}


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		double[] array = (double[]) handler.get(object);
		if(array!=null) {
			weight.reportInstance(); // the array object itself	
			weight.reportBytes(array.length*4);
		}
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		double[] array = (double[]) handler.get(origin);
		handler.set(clone, clone(array));
	}

	@Override
	public DoubleArrayLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new DoubleArrayLiFieldDelta(this, (double[]) handler.get(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (double[])");
	}


	/**
	 * 
	 * @param base
	 * @return
	 */
	private double[] clone(double[] base) {
		if(base!=null) {
			int n = base.length;
			double[] copy = new double[n];
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
		double[] array = (double[]) handler.get(object);
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
				writer.write(Double.toString(array[i]));
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

		case BOHR_Types.FLOAT32 : return new Float32_Inflow();
		case BOHR_Types.FLOAT64 : return new Float64_Inflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class Inflow extends LiFieldParser {

		@Override
		public DoubleArrayLiField getField() {
			return DoubleArrayLiField.this;
		}

		@Override
		public DoubleArrayLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new DoubleArrayLiFieldDelta(getField(), deserialize(inflow));
		}


		public abstract double[] deserialize(ByteInflow inflow) throws IOException;

	}

	private class Float32_Inflow extends Inflow {
		public @Override double[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				double[] values = new double[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getFloat32(); }
				return values;
			}
			else { return null; }
		}
	}

	private class Float64_Inflow extends Inflow {
		public @Override double[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				double[] values = new double[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getFloat64(); }
				return values;
			}
			else { return null; }
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */

	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case "float32[]" : return new Float32_Outflow(code);
		case DEFAULT_FLOW_TAG: case "float64[]" : return new Float64_Outflow(code);

		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class Composer extends LiFieldComposer {

		public Composer(int code) { super(code); }

		@Override
		public DoubleArrayLiField getField() {
			return DoubleArrayLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((DoubleArrayLiFieldDelta) delta).value);
		}

		public abstract void serialize(ByteOutflow outflow, double[] value) throws IOException;
	}


	private class Float32_Outflow extends Composer {
		public Float32_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.FLOAT32);
		}
		public @Override void serialize(ByteOutflow outflow, double[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putFloat32((float) value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	private class Float64_Outflow extends Composer {
		public Float64_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.FLOAT64);
		}
		public @Override void serialize(ByteOutflow outflow, double[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putFloat64(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}
	}

	/* <IO-outflow-section> */
}
