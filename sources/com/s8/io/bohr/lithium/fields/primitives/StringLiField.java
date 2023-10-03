package com.s8.io.bohr.lithium.fields.primitives;

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
public class StringLiField extends PrimitiveLiField {


	public final static StringLiField.Prototype PROTOTYPE = new Prototype(String.class){

		@Override
		public PrimitiveLiField.Builder createFieldBuilder(LiFieldProperties properties, LiHandler handler) {
			return new StringLiField.Builder(properties, handler);
		}
	};


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
			return new StringLiField(ordinal, properties, handler);
		}		
	}


	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws S8BuildException 
	 */
	public StringLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException{
		super(ordinal, properties, handler);
	}
	

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		String value = handler.getString(object);
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.length());
		}
	}

	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		String value = handler.getString(origin);
		handler.setString(clone, value);
	}
	

	

	@Override
	public StringLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new StringLiFieldDelta(this, (String) handler.get(object));
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (String)");
	}



	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		String val = handler.getString(object);
		writer.write(val!=null ? val : "<null>");
	}


	/* <IO-inflow-section> */


	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.STRING_UTF8 : return new UTF8_Inflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private class UTF8_Inflow extends LiFieldParser {

		@Override
		public StringLiField getField() {
			return StringLiField.this;
		}

		@Override
		public StringLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new StringLiFieldDelta(getField(), inflow.getStringUTF8());
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */

	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {
		case DEFAULT_FLOW_TAG:
		case "StringUTF8" : return new UTF8_Outflow(code);
		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private class UTF8_Outflow extends LiFieldComposer {

		public UTF8_Outflow(int code) {
			super(code);
		}

		@Override
		public StringLiField getField() {
			return StringLiField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.STRING_UTF8);
		}

		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			outflow.putStringUTF8(((StringLiFieldDelta) delta).value);
		}
	}

	/* <IO-outflow-section> */	


}
