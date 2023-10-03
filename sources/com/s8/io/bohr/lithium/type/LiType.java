package com.s8.io.bohr.lithium.type;


import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.fields.LiField;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LiType {


	/**
	 * 
	 */
	protected final Class<?> baseType;



	/**
	 * 
	 */
	Constructor<?> constructor;





	/**
	 * <p><b>/!\ WARNING </b></p>
	 * code for serialization / deserialization
	 */
	String name;




	/**
	 * fields
	 */
	final Map<String, LiField> fieldsByName = new HashMap<>();

	
	/**
	 * fields by ordinal number
	 */
	LiField[] fields;




	private final DebugModule debugModule;



	/**
	 * The number of references to be stored in vertex
	 */
	int nVertexReferences;


	public LiType(Class<?> baseType) {
		super();
		this.baseType = baseType;

		nVertexReferences = 0;

		// modules
		debugModule = new DebugModule(this);
	}
	
	
	
	
	public int getNumberOfFields() {
		return fields.length;
	}
	



	public Class<?> getBaseType(){
		return baseType;
	}

	public String getSerialName() {
		return name;
	}

	public String getRuntimeName() {
		return baseType.getName();
	}



	/**
	 * 
	 * @param object
	 * @param footprint
	 */
	public void computeFootprint(SpaceS8Object object, MemoryFootprint footprint) {
		footprint.reportInstance();
		fieldsByName.forEach((name, handler) -> {  
			try {
				footprint.reportEntry();

				handler.computeFootprint(object, footprint);
			} catch (S8IOException e) {
				e.printStackTrace();
			} 
		});
	}



	/**
	 * 
	 * @return
	 * @throws LthSerialException
	 */
	public SpaceS8Object createNewInstance() throws S8IOException {
		try {
			return (SpaceS8Object) constructor.newInstance(new Object[]{});
		}
		catch (InstantiationException 
				| IllegalAccessException 
				| IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new S8IOException("instance creation failed due to constructor call error", baseType, e);
		}
	}

	public final static byte[] CODEBASE_ENTRY = "<e/>".getBytes(StandardCharsets.US_ASCII);




	/**
	 * Collect all INTERNAL (belonging to this block) object whose state is matching the 
	 * state passed as argument, and switch their state to the state passed as
	 * argument.
	 * 
	 * @param object
	 * @param flagState
	 * @param queue
	 * @throws IOException 
	 * @throws S8ShellStructureException 
	 */
	public void sweep(SpaceS8Object object, GraphCrawler crawler) throws S8IOException {
		int nFields = fields.length;
		for(int i = 0; i<nFields; i++) { fields[i].sweep(object, crawler); }
	}




	/**
	 * Field accessor
	 * 
	 * @param name
	 * @return
	 */
	public LiField getFieldByName(String name) {
		return fieldsByName.get(name);
	}


	/**
	 * Collect all EXTERNAL blocks effectively (meaning ref is resolved) referenced by field of this objects
	 * @param object
	 * @param references
	 */
	public void collectReferencedBlocks(SpaceS8Object object, Queue<String> references) {
		for(LiField entryHandler : fieldsByName.values()) {
			entryHandler.collectReferencedBlocks(object, references);	
		}
	}


	/**
	 * 
	 * @param origin
	 * @param context
	 * @return
	 * @throws LthSerialException
	 */
	public SpaceS8Object deepClone(SpaceS8Object origin, ResolveScope resolveScope, BuildScope scope) throws S8IOException {
		SpaceS8Object clone = createNewInstance();
		for(LiField field : fields) {
			field.deepClone(origin, resolveScope, clone, scope);
		}
		return clone;
	}

	
	
	public LiField getField(int ordinal) {
		return fields[ordinal];
	}
	
	




	/**
	 * 
	 * @param indent
	 */
	public void DEBUG_print(String indent) {
		System.out.println(indent+"S8Object Type, name = "+getSerialName());
		System.out.println(indent+" fields:{");
		fieldsByName.forEach((name, field) -> { field.DEBUG_print(indent+"   "); });
		System.out.println(indent+" }\n");
	}

	/**
	 * 
	 * @param object
	 * @param inflow
	 * @param context
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void print(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException, S8ShellStructureException {
		debugModule.print(object, scope, writer);
	}

	
	public boolean equals(LiType right) {
		return getSerialName().equals(right.getSerialName());
	}
	
	public int getNumberOfVertexReferences() {
		return nVertexReferences;
	}




	
}
