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
public class LongLiField extends PrimitiveLiField {


	public final static PrimitiveLiField.Prototype PROTOTYPE = new Prototype(long.class){

		@Override
		public PrimitiveLiField.Builder createFieldBuilder(LiFieldProperties properties, LiHandler handler) {
			return new LongLiField.Builder(properties, handler);
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
			return new LongLiField(ordinal, properties, handler);
		}		
	}

	
	/**
	 * 
	 * @param outboundTypeName
	 * @param handler
	 * @throws S8BuildException 
	 */
	public LongLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException{
		super(ordinal, properties, handler);
	}


	@Override
	public Prototype getPrototype() {
		return PROTOTYPE;
	}

	
	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) {
		weight.reportBytes(8);
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		long value = handler.getLong(origin);
		handler.setLong(clone, value);
	}

	@Override
	public LongLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new LongLiFieldDelta(this, handler.getLong(object));
	}



	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (long)");
	}



	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		writer.write(Long.toString(handler.getLong(object)));
	}
	

	/* <IO-inflow-section> */


	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {

		case BOHR_Types.UINT8 : return new UInt8_NdFieldInflow();
		case BOHR_Types.UINT16 : return new UInt16_NdFieldInflow();
		case BOHR_Types.UINT32 : return new UInt32_NdFieldInflow();
		case BOHR_Types.UINT64 : return new UInt64_NdFieldInflow();

		case BOHR_Types.INT8 : return new Int8_NdFieldInflow();
		case BOHR_Types.INT16 : return new Int16_NdFieldInflow();
		case BOHR_Types.INT32 : return new Int32_NdFieldInflow();
		case BOHR_Types.INT64 : return new Int64_NdFieldInflow();

		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}



	private abstract class LongNdFieldInflow extends LiFieldParser {

		@Override
		public LongLiField getField() {
			return LongLiField.this;
		}

		@Override
		public LongLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new LongLiFieldDelta(getField(), deserialize(inflow));
		}
		

		public abstract long deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getUInt8();
		}
	}

	private class UInt16_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getUInt16();
		}
	}
	private class UInt32_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getUInt32();
		}
	}

	private class UInt64_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getUInt64();
		}
	}

	private class Int8_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getInt8();
		}
	}

	private class Int16_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getInt16();
		}
	}
	private class Int32_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return (long) inflow.getInt32();
		}
	}

	private class Int64_NdFieldInflow extends LongNdFieldInflow {
		public @Override long deserialize(ByteInflow inflow) throws IOException {
			return inflow.getInt64();
		}
	}


	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case "uint8" : return new UInt8_NdFieldOutflow(code);
		case "uint16" : return new UInt16_NdFieldOutflow(code);
		case "uint32" : return new UInt32_NdFieldOutflow(code);
		case "uint64" : return new UInt64_NdFieldOutflow(code);

		case "int8" : return new Int8_NdFieldOutflow(code);
		case "int16" : return new Int16_NdFieldOutflow(code);
		case "int32" : return new Int32_NdFieldOutflow(code);
		case DEFAULT_FLOW_TAG: case "int64" : return new Int64_NdFieldOutflow(code);

		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class Composer extends LiFieldComposer {
		public Composer(int code) { super(code); }

		@Override
		public LongLiField getField() {
			return LongLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((LongLiFieldDelta) delta).value);
		}
		

		public abstract void serialize(ByteOutflow outflow, long value) throws IOException;
	}


	private class UInt8_NdFieldOutflow extends Composer {
		public UInt8_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putUInt8((int) value);
		}
	}

	private class UInt16_NdFieldOutflow extends Composer {
		public UInt16_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putUInt16((int) value);
		}
	}

	private class UInt32_NdFieldOutflow extends Composer {
		public UInt32_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT32);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putUInt32((int) value);
		}
	}

	private class UInt64_NdFieldOutflow extends Composer {
		public UInt64_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT64);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putUInt64((int) value);
		}
	}

	private class Int8_NdFieldOutflow extends Composer {
		public Int8_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT8);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putInt8((byte) value);
		}
	}

	private class Int16_NdFieldOutflow extends Composer {
		public Int16_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT16);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putInt16((short) value);
		}
	}

	private class Int32_NdFieldOutflow extends Composer {
		public Int32_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT32);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putInt32((int) value);
		}
	}

	private class Int64_NdFieldOutflow extends Composer {
		public Int64_NdFieldOutflow(int code) { super(code); }
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.INT64);
		}
		public @Override void serialize(ByteOutflow outflow, long value) throws IOException {
			outflow.putInt64(value);
		}
	}

	/* <IO-outflow-section> */

}
