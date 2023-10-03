package com.s8.io.bohr.lithium.fields.objects;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import com.s8.io.bohr.lithium.object.LiRef;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;
import com.s8.io.bohr.lithium.properties.LiFieldProperties1T;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.GraphCrawler;
import com.s8.io.bohr.lithium.type.ResolveScope;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8RefLiField extends LiField {


	public final static LiFieldPrototype PROTOTYPE = new LiFieldPrototype() {


		@Override
		public LiFieldProperties captureField(Field field) throws S8BuildException {
			Class<?> fieldType = field.getType();
			if(LiRef.class.equals(fieldType)) {
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {

					Type parameterType = field.getGenericType();
					ParameterizedType parameterizedType = (ParameterizedType) parameterType; 
					Class<?> typeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.FIELD, typeArgument);
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
				if(LiRef.class.isAssignableFrom(baseType)) {

					Type parameterType = method.getGenericParameterTypes()[0];
					ParameterizedType parameterizedType = (ParameterizedType) parameterType; 
					Class<?> typeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.METHODS, typeArgument);
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

				if(LiRef.class.isAssignableFrom(baseType)) {

					Type parameterType = method.getGenericReturnType();
					ParameterizedType parameterizedType = (ParameterizedType) parameterType; 
					Class<?> typeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];

					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.METHODS, typeArgument);
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
			return new S8RefLiField(ordinal, properties, handler);
		}
	}




	/**
	 * 
	 * @param properties
	 * @param handler
	 * @throws S8BuildException 
	 */
	public S8RefLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException {
		super(ordinal, properties, handler);
	}




	@Override
	public void collectReferencedBlocks(SpaceS8Object object, Queue<String> references) {
		/*
		try {
			BkRef<?> ref = (BkRef<?>) field.get(object);

			// collect block ONLY is reference is not resolve at this point
			if(ref.isResolved()) {
				NgObject_i0 value = ref.get();
				if(value!=null) {
					Block block = value.getHandle().getBlock();
					if(!block.flag) {
						block.flag = true;
						references.add(block);
					}
				}	
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		 */
	}

	@Override
	public void sweep(SpaceS8Object object, GraphCrawler crawler) {
		// nothing to collect here
	}



	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		String address = ((LiRef) handler.get(object)).address;
		weight.reportBytes(1 + address.length() + 8);
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope reScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		handler.set(clone, (LiRef) handler.get(origin));
	}


	@Override
	public LiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		return new S8RefLiFieldDelta(this, (LiRef) handler.get(object));
	}

	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (S8Ref<?>)");
	}




	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		LiRef value = (LiRef) handler.get(object);
		if(value!=null) {
			writer.write(value.toString());
		}
		else {
			writer.write("null");
		}
	}

	@Override
	public String printType() {
		return "S8Ref<?>";
	}





	/* <IO-inflow-section> */

	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code;
		switch((code = inflow.getUInt8())){
		case BOHR_Types.S8REF : return new Inflow();
		default: throw new S8IOException("Unsupported code: "+Integer.toHexString(code));
		}
	}


	private class Inflow extends LiFieldParser {

		@Override
		public S8RefLiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new S8RefLiFieldDelta(getField(), deserialize(inflow));
		}


		@Override
		public S8RefLiField getField() {
			return S8RefLiField.this;
		}
		

		private LiRef deserialize(ByteInflow inflow) throws IOException {
			return LiRef.read(inflow);
		}
	}

	/* </IO-inflow-section> */



	/* <IO-outflow-section> */

	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case "obj[]" : return new Outflow(code);

		default : throw new S8IOException("Impossible to match IO type for flow: "+flow);
		}
	}


	private class Outflow extends LiFieldComposer {

		public Outflow(int code) {
			super(code);
		}

		@Override
		public LiField getField() {
			return S8RefLiField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.S8REF);
		}

		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			LiRef value = ((S8RefLiFieldDelta) delta).ref;
			LiRef.write(value, outflow);
		}
	}
	/* </IO-outflow-section> */




	@Override
	public boolean isValueResolved(SpaceS8Object object) throws S8IOException {
		// TODO Auto-generated method stub
		return false;
	}


}


