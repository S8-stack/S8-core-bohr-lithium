package com.s8.io.bohr.lithium.fields.objects;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;

import com.s8.api.bohr.BOHR_Properties;
import com.s8.api.bohr.BOHR_Types;
import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.ByteOutflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8Getter;
import com.s8.api.objects.annotations.S8Setter;
import com.s8.api.objects.serial.BohrSerializable;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.atom.serial.BohrSerialUtilities;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldBuilder;
import com.s8.io.bohr.lithium.fields.LiFieldComposer;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.fields.LiFieldParser;
import com.s8.io.bohr.lithium.fields.LiFieldPrototype;
import com.s8.io.bohr.lithium.handlers.LiHandler;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;
import com.s8.io.bohr.lithium.properties.LiFieldProperties0T;
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
public class S8SerializableLiField extends LiField {
	


	public final static LiFieldPrototype PROTOTYPE = new LiFieldPrototype() {


		@Override
		public LiFieldProperties captureField(Field field) throws S8BuildException {
			Class<?> fieldType = field.getType();
			if(BohrSerializable.class.isAssignableFrom(fieldType)){
				S8Field annotation = field.getAnnotation(S8Field.class);
				if(annotation != null) {
					LiFieldProperties properties = new LiFieldProperties0T(this, LiFieldProperties.FIELD, fieldType);
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
				if(BohrSerializable.class.isAssignableFrom(baseType)) {
					LiFieldProperties properties = new LiFieldProperties0T(this, LiFieldProperties.METHODS, baseType);
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
				if(BohrSerializable.class.isAssignableFrom(baseType)){
					LiFieldProperties properties = new LiFieldProperties0T(this, LiFieldProperties.METHODS, baseType);
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
			return new S8SerializableLiField(ordinal, properties, handler);
		}
	}



	private BohrSerializable.BohrSerialPrototype<?> deserializer;


	
	/**
	 * 
	 * @param properties
	 * @param handler
	 * @throws S8BuildException 
	 */
	public S8SerializableLiField(int ordinal, LiFieldProperties properties, LiHandler handler) throws S8BuildException {
		super(ordinal, properties, handler);
		Class<?> baseType = properties.getBaseType();
		try {
			deserializer = BohrSerialUtilities.getDeserializer(baseType);
		} 
		catch (S8IOException e) {
			e.printStackTrace();
			throw new S8BuildException("Failed to build the S8Serizalizable GphField due to  "+e.getMessage() , baseType);
		}
	}





	@Override
	public void sweep(SpaceS8Object object, GraphCrawler crawler) {
		// no sweep
	}


	@Override
	public void collectReferencedBlocks(SpaceS8Object object, Queue<String> references) {
		// No ext references
	}





	@Override
	public void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException {
		BohrSerializable value = (BohrSerializable) handler.get(object);
		if(value!=null) {
			weight.reportInstance();
			weight.reportBytes(value.computeFootprint());	
		}
	}

	@Override
	public void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException {
		BohrSerializable value = (BohrSerializable) handler.get(origin);
		handler.set(clone, value.deepClone());
	}


	@Override
	public void DEBUG_print(String indent) {
		System.out.println(indent+name+": (ByteSerializableFieldHandler)");
	}


	@Override
	public LiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws S8IOException {
		return new S8SerializableLiFieldDelta(this, (BohrSerializable) handler.get(object));
	}



	@Override
	protected void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException {
		Object value = handler.get(object);
		if(value!=null) {
			writer.write("(");
			writer.write(value.getClass().getCanonicalName());
			writer.write("): ");
			writer.write(value.toString());	
		}
		else {
			writer.write("null");
		}
	}

	@Override
	public String printType() {
		return "S8Object";
	}



	@Override
	public boolean isValueResolved(SpaceS8Object object) {
		return true; // always resolved at resolve step in shell
	}
	
	
	


	/* <IO-inflow-section> */

	@Override
	public LiFieldParser createParser(ByteInflow inflow) throws IOException {
		int code = inflow.getUInt8();
		if(code != BOHR_Types.SERIAL) {
			throw new IOException("only array accepted");
		}
		
		// in fine, create parser
		return new Parser();
	}
	

	private class Parser extends LiFieldParser {

		@Override
		public LiFieldDelta parseValue(ByteInflow inflow) throws IOException {
			return new S8SerializableLiFieldDelta(getField(), deserialize(inflow));
		}


		@Override
		public S8SerializableLiField getField() {
			return S8SerializableLiField.this;
		}
		
		private BohrSerializable deserialize(ByteInflow inflow) throws IOException {
			int props = inflow.getUInt8();
			if(isNonNull(props)) {
				return deserializer.deserialize(inflow);
			}
			else {
				return null;
			}
		}

	}
	
	/* </IO-inflow-section> */

	

	/* <IO-outflow-section> */
		
	@Override
	public LiFieldComposer createComposer(int code) throws S8IOException {
		switch(flow) {

		case DEFAULT_FLOW_TAG: case "serial" : return new Outflow(code);

		default : throw new S8IOException("Impossible to match IO type for flow: "+flow);
		}
	}


	private class Outflow extends LiFieldComposer {

		public Outflow(int code) { super(code); }

		@Override
		public LiField getField() {
			return S8SerializableLiField.this;
		}

		@Override
		public void publishFlowEncoding(ByteOutflow outflow) throws IOException {
			outflow.putUInt8(BOHR_Types.SERIAL);
		}

		@Override
		public void composeValue(LiFieldDelta delta, ByteOutflow outflow) throws IOException {
			BohrSerializable value = ((S8SerializableLiFieldDelta) delta).value;
			if(value != null) {
				outflow.putUInt8(BOHR_Properties.IS_NON_NULL_PROPERTIES_BIT);
				value.serialize(outflow);
			}
			else {
				outflow.putUInt8(0x00);
			}
		}
	}
	/* </IO-outflow-section> */
	

}
