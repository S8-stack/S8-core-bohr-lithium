package com.s8.io.bohr.lithium.branches;

import static com.s8.io.bohr.atom.BOHR_Keywords.FRAME_FOOTER;
import static com.s8.io.bohr.atom.BOHR_Keywords.FRAME_HEADER;

import java.io.IOException;
import java.io.Writer;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.s8.io.bohr.atom.S8BuildException;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.object.LiS8Object;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.ResolveScope;
import com.s8.io.bytes.alpha.ByteInflow;
import com.s8.io.bytes.alpha.ByteOutflow;
import com.s8.io.bytes.base64.Base64Generator;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LiBranch {
	
	

	/**
	 * 
	 */
	public final static int EXPOSURE_RANGE = 8;


	
	public final ResolveScope resolveScope = new ResolveScope() {
		
		@Override
		public String resolveId(LiS8Object object) throws LiIOException {
			return append(null, object).id;
		}
	};
	
	
	public final String address;
	
	public final String id;

	public final LiCodebase codebase;
	
	long highestIndex;


	/**
	 * The interior mapping
	 */
	final Map<String, LiVertex> vertices;
	
	
	/**
	 * inbound
	 */
	public final LiInbound inbound;
	
	
	/**
	 * outbound
	 */
	public final LiOutbound outbound;
	
	
	
	final LiVertex[] exposure;
	

	
	/**
	 * Stateful var
	 */
	long version;
	
	
	
	String comment;
	
	
	long timestamp;
	
	
	private final Base64Generator idxGen;
	
	private final DebugModule debugModule;
	
	private boolean hasUnpublishedChanges = false;

	private final Deque<LiVertex> unpublishedVertices = new LinkedList<LiVertex>();


	private final Set<Integer> unpublishedSlotExposure = new HashSet<>();


	

	/**
	 * 
	 * @param branchId
	 * @param graph
	 * @param deltas
	 */
	public LiBranch(String address, String id, LiCodebase codebase) {
		super();
		this.address = address;
		this.id = id;
		
		this.codebase = codebase;
		
		// exposure
		exposure = new LiVertex[EXPOSURE_RANGE];
		
		vertices = new HashMap<String, LiVertex>();
		
		inbound = new LiInbound(this);
		outbound = new LiOutbound(this);
		
		debugModule = new DebugModule(this);
		
		idxGen = new Base64Generator(id);
	}
	
	
	
	public LiVertex getVertex(String id) {
		return vertices.get(id);
	}
	

	public void removeVertex(String id) {
		vertices.remove(id);
	}
	
	
	public void expose(int slot, LiS8Object object) throws IOException {
		LiVertex vertex = resolveVertex(object);
		exposure[slot] = vertex;
		outbound.reportExpose(slot);
	}
	

	public LiS8Object retrieveObject(String index) {
		return vertices.get(index).object;
	}

	
	/**
	 * 
	 * @return
	 */
	public String createNewIndex() {
		return idxGen.generate(++highestIndex);
	}
	
	
	public BuildScope createBuildScope() {
		return new BuildScope() {
			@Override
			public LiS8Object retrieveObject(String index) {
				return vertices.get(index).object;
			}
		};
	}
	
	
	
	
	
	/**
	 * 
	 * @param inflow
	 * @throws IOException
	 */
	public void pullSequence(ByteInflow inflow) throws IOException {
		// check opening
		if(!inflow.matches(FRAME_HEADER)) { throw new IOException("DO NOT MATCH HEADER"); }
		inbound.parse(inflow);
		if(!inflow.matches(FRAME_FOOTER)) { throw new IOException("DO NOT MATCH FOOTER"); }
	}
	
	
	/**
	 * 
	 * @param outflow
	 * @throws S8BuildException
	 * @throws S8Exception
	 * @throws IOException
	 */
	public void pushSequence(ByteOutflow outflow) throws S8BuildException, S8Exception, IOException {
		outflow.putByteArray(FRAME_HEADER);
		outbound.compose(outflow);
		outflow.putByteArray(FRAME_FOOTER);
	}

	

	
	
	public LiVertex resolveVertex(LiS8Object object) throws LiIOException {
		return append(null, object);
	}

	
	
	public LiVertex append(String id, LiS8Object object) throws LiIOException {
		
		/* retrieve object vertex */
		LiVertex vertex = (LiVertex) object.S8_vertex;
		
		if(vertex == null) {
			
			/* if index is null, assigned a newly generated one */
			boolean isCreating;
			if(isCreating = (id == null)){
				id = createNewIndex();
			}
			
			/* create vertex */
			vertex = new LiVertex(this, id, object);
			
			/* assign newly created vertex */
			object.S8_vertex = vertex;
			
			/* newly created vertex, so report activity */
			if(isCreating) {
				outbound.reportCreate(vertex);	
			}
			
			/* register vertex */
			vertices.put(id, vertex);
		}
		
		return vertex;
		
	}


	

	/**
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void print(Writer writer) throws IOException {
		debugModule.print(resolveScope, writer);
	}

	
	/**
	 * 
	 * @param writer
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void deepCompare(LiBranch deviated, Writer writer) throws IOException, S8ShellStructureException {
		debugModule.deepCompare(deviated, resolveScope, writer);
		
	}

	
	public void reportExpose(int slot) {
		unpublishedSlotExposure.add(slot);
		hasUnpublishedChanges = true;
	}


	public void reportCreate(LiVertex vertex) {
		unpublishedVertices.add(vertex);
		hasUnpublishedChanges = true;
	}


	public void reportUpdate(LiVertex vertex) {
		hasUnpublishedChanges = true;
		unpublishedVertices.add(vertex);
	}





}
