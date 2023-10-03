package com.s8.io.bohr.lithium.fields.objects;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;

import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8Getter;
import com.s8.api.objects.annotations.S8Setter;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldBuilder;
import com.s8.io.bohr.lithium.fields.LiFieldComposer;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.fields.LiFieldParser;
import com.s8.io.bohr.lithium.fields.LiFieldPrototype;
import com.s8.io.bohr.lithium.handlers.LiHandler;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;
import com.s8.io.bohr.lithium.properties.LiFieldProperties0T;
import com.s8.io.bohr.lithium.properties.LiFieldProperties1T;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.GraphCrawler;
import com.s8.io.bohr.lithium.type.ResolveScope;


/**
 * 
 * @author pc
 *
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class EnumLiField extends LiField {



	public final static LiFieldPrototype PROTOTYPE = new LiFieldPrototype() {


		@Override
		public LiFieldProperties captureField(Field field) throws S8BuildException {
			Class<?> baseType = field.getType();
			if(baseType.isEnum()) {
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {
					LiFieldProperties properties = new LiFieldProperties0T(this, LiFieldProperties.FIELD, baseType);
					properties.setFieldAnnotation(annotation);
					return properties;	
				}
				else { return null; }
			}
			else { return null; }
		}


		@Override
		public LiFieldProperties captureSetter(Method method) throws S8BuildException {
			Class<?> baseType = method.getParameterTypes()[0];
			S8Setter annotation = method.getAnnotation(S8Setter.class);
			if(annotation != null) {
				if(baseType.isEnum()) {
					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.METHODS, baseType);
					properties.setSetterAnnotation(annotation);
					return properties;
				}
				else {
					throw new S8BuildException("S8Annotated field of type List must have its "
							+"parameterized type inheriting from S8Object", method);
				}
			}
			else { return null; }
		}

		@Override
		public LiFieldProperties captureGetter(Method method) throws S8BuildException {
			Class<?> baseType = method.getReturnType();

			S8Getter annotation = method.getAnnotation(S8Getter.class);
			if(annotation != null) {
				if(baseType.isEnum()) {
					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.METHODS, baseType);
					properties.setGetterAnnotation(annotation);
					return properties;
				}
				else {
					throw new S8BuildException("S8Annotated field of type List must have its "
							+"parameterized type inheriting from S8Object", method);

				}
			}
			else { return null; }
		}


		@Override
		public LiFieldBuilder createFieldBuilder(LiFieldProperties properties, LiHandler handler) {
			return new Builder(properties, handler);
		}
	};



	private static class Builder extends LiFieldBuilder {

		public Builder(LiFieldProperties properties, LiHandler handler) {
			super(properties, handler);
		}

		@Override
		public LiFieldPrototype getPrototype() {
			return PROTOTYPE;
		}

		@Override
		public LiField build(int ordinal) throws S8BuildException {
			return new EnumLiField(ordinal, properties, handler);
		}
	}



	private Class<?> enumType;

	private Object[] values;


	/**
	 * 
	 * @param ordinal
	 * @param properties
	 * @param handler
	 * @throws S8IOException
	 */
	public EnumLiField(int ordinal, LiFieldProperties properties, LiHandler handler) {
		super(ordinal, properties, handler);
		this.enumType = properties.getBaseType();
		this.values = enumType.getEnumConstants();
	}


	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) {
		weight.reportInstance();
		weight.reportBytes(4); // int ordinal
	}


	@Override
	public void collectReferencedBlocks(SpaceS8Object object, Queue<String> references) {
		//no blocks to collect
	}


	@Override
	public void sweep(SpaceS8Object object, GraphCrawler crawler) {
		// nothing to collect
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": ("+enumType.getName()+")");
	}



	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope rScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		Object value = handler.get(origin);
		handler.set(clone, value);
	}



	@Override
	public EnumLiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new EnumLiFieldDelta(this, handler.get(object));
	}

	

	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		Object value = handler.get(object);
		if(value!=null) {
			Enum<?> enumValue = (Enum<?>) value;
			writer.write(enumValue.name());	
		}
		else {
			writer.write("null");
		}
	}


	@Override
	public String printType() {
		return enumType.getCanonicalName();
	}


	@Override
	public boolean isValueResolved(SpaceS8Object object) {
		return true; // always resolved
	}


	/* <IO-inflow-section> */

	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code) {
		case BOHR_Types.UINT8 : return new UInt8_Inflow();
		case BOHR_Types.UINT16 : return new UInt16_Inflow();
		case BOHR_Types.UINT32 : return new UInt32_Inflow();
		default : throw new S8IOException("Failed to find field-inflow for code: "+Integer.toHexString(code));
		}
	}


	private abstract class Inflow extends LiFieldParser {

		@Override
		public EnumLiField getField() {
			return EnumLiField.this;
		}

		@Override
		public LiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new EnumLiFieldDelta(getField(), deserialize(inflow));
		}

		public abstract Object deserialize(ByteInflow inflow) throws IOException;

	}

	private class UInt8_Inflow extends Inflow {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[(int) inflow.getUInt8()];
		}
	}

	private class UInt16_Inflow extends Inflow {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[inflow.getUInt16()];
		}
	}

	private class UInt32_Inflow extends Inflow {
		public @Override Object deserialize(ByteInflow inflow) throws IOException {
			return values[inflow.getUInt32()];
		}
	}

	/* </IO-inflow-section> */


	/* <IO-outflow-section> */
	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {
		case "uint8" : return new UInt8_Outflow(code);
		case "uint16" : return new UInt16_Outflow(code);
		case "uint32" : return new UInt32_Outflow(code);
		case DEFAULT_FLOW_TAG: 
			if(values.length<=0xff) {
				return new UInt8_Outflow(code);
			}
			else if(values.length<=0xffff) {
				return new UInt16_Outflow(code);
			}
			else {
				return new UInt32_Outflow(code);
			}
		default : throw new S8IOException("Failed to find field-outflow for encoding: "+flow);
		}
	}


	private abstract class Composer extends LiFieldComposer {

		public Composer(int code) {
			super(code);
		}


		@Override
		public EnumLiField getField() {
			return EnumLiField.this;
		}


		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			serialize(outflow, ((EnumLiFieldDelta) delta).value);
		}
		

		public abstract void serialize(ByteOutflow outflow, Object value) throws IOException;
	}


	private class UInt8_Outflow extends Composer {
		public UInt8_Outflow(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT8);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt8(code);
		}
	}

	private class UInt16_Outflow extends Composer {
		public UInt16_Outflow(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT16);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt16(code);
		}
	}

	private class UInt32_Outflow extends Composer {
		public UInt32_Outflow(int code) {
			super(code);
		}
		public @Override void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.UINT32);
		}
		public @Override void serialize(ByteOutflow outflow, Object value) throws IOException {
			int code = ((Enum<?>) value).ordinal();
			outflow.putUInt32(code);
		}
	}

	/* <IO-outflow-section> */
}
