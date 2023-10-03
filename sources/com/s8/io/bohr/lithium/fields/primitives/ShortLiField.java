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
public class ShortLiField extends PrimitiveLiField {


	public final static PrimitiveLiField.Prototype PROTOTYPE = new Prototype(short.class){

		@Override
		public PrimitiveLiField.Builder createFieldBuilder(LiFieldProperties properties, LiHandler handler) {
			return new ShortLiField.Builder(properties, handler);
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
			return new ShortLiField(ordinal, properties, handler);
		}		
	}

	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws S8BuildException 
	 */
	public ShortLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException{
		super(ordinal, properties, handler);
	}

	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (short)");
	}



	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		writer.write(Short.toString(handler.getShort(object)));
	}


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) {
		weight.reportBytes(2);
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		short value = handler.getShort(origin);
		handler.setShort(clone, value);
	}


	@Override
	public ShortLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new ShortLiFieldDelta(this, handler.getShort(object));
	}


	/* <IO-inflow-section> */


	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.UINT8 : return new UInt8_Inflow();
		case BOHR_Types.UINT16 : return new UInt16_Inflow();

		case BOHR_Types.INT8 : return new Int8_Inflow();
		case BOHR_Types.INT16 : return new Int16_Inflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class Inflow extends LiFieldParser {

		@Override
		public ShortLiField getField() {
			return ShortLiField.this;
		}

		@Override
		public ShortLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new ShortLiFieldDelta(getField(), deserialize(inflow));
		}

		public abstract short deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8_Inflow extends Inflow {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getUInt8();
		}
	}

	private class UInt16_Inflow extends Inflow {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getUInt16();
		}
	}


	private class Int8_Inflow extends Inflow {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return (short) inflow.getInt8();
		}
	}

	private class Int16_Inflow extends Inflow {
		public @Override short deserialize(ByteInflow inflow) throws IOException {
			return inflow.getInt16();
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case "uint8" : return new UInt8_Outflow(code);
		case "uint16" : return new UInt16_Outflow(code);

		case "int8" : return new Int8_Outflow(code);
		
		case DEFAULT_FLOW_TAG: case "int16" : return new Int16_Outflow(code);

		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class Composer extends LiFieldComposer {

		public Composer(int code) { super(code); }


		@Override
		public ShortLiField getField() {
			return ShortLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((ShortLiFieldDelta) delta).value);
		}
		

		public abstract void serialize(ByteOutflow outflow, short value) throws IOException;
	}


	private class UInt8_Outflow extends Composer {
		public UInt8_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putUInt8((int) value);
		}
	}

	private class UInt16_Outflow extends Composer {
		public UInt16_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putUInt16((int) value);
		}
	}


	private class Int8_Outflow extends Composer {
		public Int8_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT8);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putInt8((byte) value);
		}
	}

	private class Int16_Outflow extends Composer {
		public Int16_Outflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT16);
		}
		public @Override void serialize(ByteOutflow outflow, short value) throws IOException {
			outflow.putInt16(value);
		}
	}

	/* <IO-outflow-section> */



}
