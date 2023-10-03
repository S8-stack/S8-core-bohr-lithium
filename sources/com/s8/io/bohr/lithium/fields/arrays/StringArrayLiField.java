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
public class StringArrayLiField extends PrimitiveArrayLiField {

	/**
	 * 
	 */
	public final static Prototype PROTOTYPE = new Prototype(String[].class){
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
			return new StringArrayLiField(ordinal, properties, handler);
		}		
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}
	
	
	public StringArrayLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException {
		super(ordinal, properties, handler);
	}
	


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		String[] array = (String[]) handler.get(object);
		if(array!=null) {
			weight.reportInstance(); // the array object itself	
			int length = array.length;
			for(int i = 0; i<length; i++) {
				weight.reportInstance(); // the array object itself	
				weight.reportBytes(array[i].length()); // the array object itself	
			}
		}
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		String[] array = (String[]) handler.get(origin);
		handler.set(clone, clone(array));
	}
	
	@Override
	public StringArrayLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new StringArrayLiFieldDelta(this, (String[]) handler.get(object));
	}

	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (String[])");
	}


	/**
	 * 
	 * @param base
	 * @return
	 */
	private String[] clone(String[] base) {
		if(base!=null) {
			int n = base.length;
			String[] copy = new String[n];
			for(int i=0; i<n; i++) {
				copy[i] = base[i]; // String are immutables !
			}
			return copy;
		}
		else {
			return null;
		}
	}


	
	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		String[] array = (String[]) handler.get(object);
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
				writer.write(array[i]);
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

		case BOHR_Types.STRING_UTF8 : return new UTF8_Inflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}
	


	private class UTF8_Inflow extends LiFieldParser {

		@Override
		public StringArrayLiField getField() {
			return StringArrayLiField.this;
		}

		@Override
		public StringArrayLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new StringArrayLiFieldDelta(getField(), deserialize(inflow));
		}
		

		public String[] deserialize(ByteInflow inflow) throws IOException {
			int length = (int) inflow.getUInt7x();
			if(length >= 0) {
				String[] values = new String[length];
				for(int i = 0; i<length; i++) { values[i] = inflow.getStringUTF8(); }
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

		case DEFAULT_FLOW_TAG: case "string_UTF8[]" : return new UTF8_Outflow(code);
		
		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private class UTF8_Outflow extends LiFieldComposer {

		public UTF8_Outflow(int code) {
			super(code);
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.ARRAY);
			outflow.putUInt8(BOHR_Types.STRING_UTF8);
		}
		
		@Override
		public StringArrayLiField getField() {
			return StringArrayLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((StringArrayLiFieldDelta) delta).value);
		}
		
		
		public void serialize(ByteOutflow outflow, String[] value) throws IOException {
			if(value != null) {
				int length = value.length;
				outflow.putUInt7x(length);
				for(int i = 0; i<length; i++) { outflow.putStringUTF8(value[i]); }
			}
			else { outflow.putUInt7x(-1); }
		}

	}

	/* <IO-outflow-section> */
	
}
