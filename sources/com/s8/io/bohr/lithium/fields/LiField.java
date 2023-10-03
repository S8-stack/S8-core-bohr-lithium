package com.s8.io.bohr.lithium.fields;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;

import com.s8.api.bytes.ByteInflow;
import com.s8.api.bytes.MemoryFootprint;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.handlers.LiHandler;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;
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
public abstract class LiField {

	public final static String DEFAULT_FLOW_TAG = "(default)";

	
	public final int ordinal;
	
	/**
	 * 
	 */
	public final LiHandler handler;


	/**
	 * 
	 */
	public final String name;

	/**
	 * 
	 */
	public final String flow;

	/**
	 * 
	 */
	public final long mask;

	/**
	 * 
	 */
	public final long flags;
	
	/**
	 * 
	 * @param properties
	 * @param handler
	 */
	public LiField(int ordinal, LiFieldProperties properties, LiHandler handler) {
		super();
		
		this.ordinal = ordinal;
		
		/* <field-properties> */
		this.name = properties.getName();
		this.flow = properties.getFlow();
		this.mask = properties.getMask();
		this.flags = properties.getFlags();
		/* </field-properties> */
		
		/* <handler> */
		this.handler = handler;
		/* </handler> */
	}


	
	
	protected LiHandler getHandler() {
		return handler;
	}

	
	
	
	/**
	 * 
	 * @param flow (null = use flow define in field)
	 * @return
	 * @throws  
	 */
	public abstract LiFieldComposer createComposer(int code) throws S8IOException;
	
	
	public abstract LiFieldParser createParser(ByteInflow inflow) throws IOException;
	
	


	public abstract void computeFootprint(SpaceS8Object object, MemoryFootprint weight) throws S8IOException;

	/**
	 * Collect all INTERNAL (belonging to this block) object not already in the
	 * state passed as argument, and switch their state to the state passed as
	 * argument
	 * 
	 * @param object
	 * @param state
	 * @param referenced
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public abstract void sweep(SpaceS8Object object, GraphCrawler crawler) throws S8IOException;

	/**
	 * Collect all external blocks with flag not already set to true
	 * 
	 * @param object
	 */
	public abstract void collectReferencedBlocks(SpaceS8Object object, Queue<String> references);

	

	/**
	 * Print field for debugging purposes
	 * 
	 * @param indent
	 */
	public abstract void DEBUG_print(String indent);

	/**
	 * 
	 * @param clone
	 * @param bindings
	 * @throws LthSerialException
	 */
	public abstract void deepClone(SpaceS8Object origin, ResolveScope resolveScope, SpaceS8Object clone, BuildScope scope) throws S8IOException;

	
	/**
	 * 
	 * @param object
	 * @param scope
	 * @return
	 * @throws IOException
	 */
	public abstract LiFieldDelta produceDiff(SpaceS8Object object, ResolveScope scope) throws IOException;

	

	public void print(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException, S8ShellStructureException {
		writer.append("(");
		writer.append(printType());
		writer.append(") ");
		writer.append(name);
		writer.append(": ");
		printValue(object, scope, writer);
	}
	

	/**
	 * print standard name of field type
	 */
	public abstract String printType();


	protected abstract void printValue(SpaceS8Object object, ResolveScope scope, Writer writer) throws S8IOException, 
	IOException, S8ShellStructureException;

	

	/**
	 * 
	 * @param object
	 * @return true is the object has been resolved, false otherwise
	 * @throws LthSerialException 
	 */
	public abstract boolean isValueResolved(SpaceS8Object object) throws S8IOException;

	
}
