package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.s8.io.bohr.atom.S8BuildException;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bytes.base64.Base64Generator;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LiBranch implements LiGraphDeltaConsumer {



	public final String id;

	public final LiCodebase codebase;

	long highestIndex;






	String comment;


	long timestamp;


	private final Base64Generator idxGen;

	private final DebugModule debugModule;

	private final LiGraph graph;

	private final ArrayList<LiGraphDelta> deltas;



	/**
	 * 
	 * @param branchId
	 * @param graph
	 * @param deltas
	 */
	public LiBranch(String id, LiCodebase codebase) {
		super();
		this.id = id;

		this.codebase = codebase;


		graph = new LiGraph(this);
		debugModule = new DebugModule(graph);
		idxGen = new Base64Generator(id);

		deltas = new ArrayList<>();
	}



	/**
	 * 
	 * @return
	 */
	public LiGraph getGraph() {
		return graph;
	}

	@Override
	public void pushDelta(LiGraphDelta delta) throws LiIOException {

		/* save delta */
		this.deltas.add(delta);

		/* operate delta immediatley */
		delta.operate(graph);
	}


	/**
	 * 
	 * @param copy
	 */
	public List<LiGraphDelta> pullDeltas() {
		List<LiGraphDelta> copy = new ArrayList<>();
		deltas.forEach(delta -> copy.add(delta));
		return copy;
	}


	/**
	 * 
	 * @return
	 */
	public String createNewIndex() {
		return idxGen.generate(++highestIndex);
	}






	/**
	 * 
	 * @return
	 */
	public LiObject[] getCurrentExposure() {
		LiVertex[] vertices = graph.exposure;
		int n = vertices.length;
		LiObject[] objects = new LiObject[n];
		for(int i = 0; i<n; i++) {
			LiVertex vertex = vertices[i];
			objects[i] = (vertex != null) ? vertex.object : null; 
		}
		return objects;
	}
	
	
	/**
	 * 
	 * @param slot
	 * @return
	 */
	public LiObject getExposed(int slot) {
		LiVertex vertex = graph.exposure[slot];
		return vertex != null ? vertex.object : null;
	}
	

	
	/**
	 * 
	 * @param slot
	 * @param object
	 * @throws LiIOException
	 */
	public void expose(int slot, LiObject object) throws LiIOException {
		graph.expose(slot, object);
	}
	



	/**
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void print(Writer writer) throws IOException {
		debugModule.print(graph.resolveScope, writer);
	}

	
	/**
	 * 
	 * @throws S8BuildException
	 * @throws S8Exception
	 * @throws IOException
	 */
	public void commit() throws S8BuildException, S8Exception, IOException {
		deltas.add(graph.produceDiff());
	}



}
