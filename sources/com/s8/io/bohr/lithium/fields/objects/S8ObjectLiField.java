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
public class S8ObjectLiField extends LiField {



	public final static LiFieldPrototype PROTOTYPE = new LiFieldPrototype() {


		@Override
		public LiFieldProperties captureField(Field field) throws S8BuildException {
			Class<?> fieldType = field.getType();
			if(SpaceS8Object.class.isAssignableFrom(fieldType)){
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {
					LiFieldProperties properties = new LiFieldProperties1T(this, LiFieldProperties.FIELD, fieldType);
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
				if(SpaceS8Object.class.isAssignableFrom(baseType)) {
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
				if(SpaceS8Object.class.isAssignableFrom(baseType)){
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
		public LiField build(int ordinal) {
			return new S8ObjectLiField(ordinal, properties, handler);
		}
	}


	public S8ObjectLiField(int ordinal, LiFieldProperties properties, LiHandler handler) {
		super(ordinal, properties, handler);
	}





	@Override
	public void sweep(SpaceS8Object object, GraphCrawler crawler) {
		try {
			SpaceS8Object fieldObject = (SpaceS8Object) handler.get(object);
			if(fieldObject!=null) {
				crawler.accept(fieldObject);
			}
		} 
		catch (S8IOException cause) {
			cause.printStackTrace();
		}
	}


	@Override
	public void collectReferencedBlocks(SpaceS8Object object, Queue<String> references) {
		// No ext references
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (S8Object)");
	}

	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		weight.reportReference();
	}


	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		SpaceS8Object value = (SpaceS8Object) handler.get(origin);
		if(value!=null) {
			String index = resolveScope.resolveId(value);

			scope.appendBinding(new BuildScope.Binding() {

				@Override
				public void resolve(BuildScope scope) throws S8IOException {

					// no need to upcast to S8Object
					SpaceS8Object indexedObject = scope.retrieveObject(index);
					if(indexedObject==null) {
						throw new S8IOException("Fialed to retriev vertex");
					}
					handler.set(clone, indexedObject);
				}
			});
		}
		else {
			handler.set(clone, null);
		}
	}

	
	@Override
	public LiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException {
		SpaceS8Object value = (SpaceS8Object) handler.get(object);
		return new S8ObjectLiFieldDelta(this, scope.resolveId(value));	
	}


	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		SpaceS8Object value = (SpaceS8Object) handler.get(object);
		if(value!=null) {
			writer.write("(");
			writer.write(value.getClass().getCanonicalName());
			writer.write("): ");
			writer.write(scope.resolveId(value));	
		}
		else {
			writer.write("null");
		}
	}

	@Override
	public String printType() {
		return "S8Object";
	}

	public void setValue(Object object, SpaceS8Object struct) throws S8IOException {
		handler.set(object, struct);
	}





	@Override
	public boolean isValueResolved(SpaceS8Object object) {
		return true; // always resolved at resolve step in shell
	}


	/* <delta> */



	/* <IO-inflow-section> */

	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		switch(code){
		case BOHR_Types.S8OBJECT : return new Inflow();
		default: throw new S8IOException("Unsupported code: "+Integer.toHexString(code));
		}
	}


	private class Inflow extends LiFieldParser {

		@Override
		public LiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			String id = inflow.getStringUTF8();
			return new S8ObjectLiFieldDelta(getField(), id);
		}


		@Override
		public S8ObjectLiField getField() {
			return S8ObjectLiField.this;
		}
	}


	/* </IO-inflow-section> */



	/* <IO-outflow-section> */

	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {
		case DEFAULT_FLOW_TAG: case "obj[]" : return new Outflow(code);
		default : throw new S8IOException("Impossible to match IO type for flow: "+flow);
		}
	}


	private class Outflow extends LiFieldComposer {

		public Outflow(int code) {
			super(code);
		}

		@Override
		public LiField getField() {
			return S8ObjectLiField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.S8OBJECT);
		}

		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			String id = ((S8ObjectLiFieldDelta) delta).index;
			outflow.putStringUTF8(id);
		}
	}
	/* </IO-outflow-section> */
}
