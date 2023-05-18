package com.s8.io.bohr.lithium.branches;

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
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.object.ExposeLiObjectDelta;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.ResolveScope;
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
		public String resolveId(LiObject object) throws LiIOException {
			if(object != null) {
				return append(null, object).id;
			}
			else {
				return null;
			}
		}
	};


	public final String address;

	public final String id;

	public final LiCodebase codebase;

	long highestIndex;


	/**
	 * The interior mapping
	 */
	public final Map<String, LiVertex> vertices;


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

		debugModule = new DebugModule(this);

		idxGen = new Base64Generator(id);
	}



	public LiVertex getVertex(String id) {
		return vertices.get(id);
	}


	public void removeVertex(String id) {
		vertices.remove(id);
	}


	public void expose(int slot, LiObject object) throws LiIOException {
		LiVertex vertex = resolveVertex(object);
		exposure[slot] = vertex;
		reportExpose(slot);
	}


	public LiObject retrieveObject(String index) {
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
			public LiObject retrieveObject(String index) {
				return vertices.get(index).object;
			}
		};
	}





	




	public LiVertex resolveVertex(LiObject object) throws LiIOException {
		return append(null, object);
	}



	public LiVertex append(String id, LiObject object) throws LiIOException {

		if(object == null) { throw new LiIOException("Cannot append null obejct"); }

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
			if(isCreating) { reportCreate(vertex); }

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



	public boolean hasUnpublishedChanges() {
		return hasUnpublishedChanges;
	}


	/**
	 * 
	 * @param outflow
	 * @throws IOException 
	 * @throws S8Exception 
	 * @throws S8BuildException 
	 */
	public LiBranchDelta compose() throws S8BuildException, S8Exception, IOException {

		if(!hasUnpublishedChanges) {
			// TODO
		}

		LiBranchDelta branchDelta = new LiBranchDelta(version+1);
		version++;


		LiVertex vertex;
		while((vertex = unpublishedVertices.poll()) != null) {
			vertex.publish(branchDelta.objectDeltas, resolveScope);
		}


		// expose if necessary
		if(!unpublishedSlotExposure.isEmpty()) {
			unpublishedSlotExposure.forEach(slot -> {
				LiVertex exposedVertex = exposure[slot];
				if(exposedVertex != null) {
					branchDelta.appendObjectDelta(new ExposeLiObjectDelta(exposedVertex.id, slot));		
				}
			});
		}



		hasUnpublishedChanges = false;
		
		return branchDelta;

	}



}
