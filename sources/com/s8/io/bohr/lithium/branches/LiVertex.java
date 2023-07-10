package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.s8.io.bohr.atom.S8BuildException;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.CreateLiObjectDelta;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bohr.lithium.object.LiObjectDelta;
import com.s8.io.bohr.lithium.object.UpdateLiObjectDelta;
import com.s8.io.bohr.lithium.type.GraphCrawler;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.ResolveScope;
import com.s8.io.bytes.alpha.MemoryFootprint;


/**
 * <h1>Node for sweepable graph</h1>
 * <p>Node encompass in a unified interface two types of cases:</p>
 * <ul>
 * <li>On the fly type resolution (S8Struct)</li>
 * <li>Compiled type resolution, stored in S8Vertex extension (like LiVertex) (S8Object)</li>
 * </ul>
 * <p>This is the building block for using sweep on graph</p>
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class LiVertex {



	public final LiGraph graph;



	/**
	 * <h1>DO NOT USE THIS FIELD: SYSTEM ONLY</h1>
	 * <p>
	 * This index acts as an internal identifier and is automatically assigned at
	 * commit time.
	 * </p>
	 */
	public final String id;


	/**
	 * 
	 */
	public final LiType type;





	private boolean isUnpublished = false;
	/**
	 * 
	 */
	private boolean isCreateUnpublished = false;


	public final LiObject object;

	/**
	 * 
	 */
	public final boolean[] hasFieldUnpublishedChange;


	/**
	 * 
	 * @param type
	 * @param object
	 * @throws IOException 
	 */
	public LiVertex(LiGraph graph, String id, LiObject object) throws LiIOException {
		super();
		this.graph = graph;
		this.id = id;

		LiType type = graph.getCodebase().getType(object);
		if(type == null) {
			throw new LiIOException("Type "+object.getClass().getName()+" is unknown from this branch codebase.");
		}
		this.type = type;

		this.object = object;

		int nFields = type.getNumberOfFields();
		this.hasFieldUnpublishedChange = new boolean[nFields];
		for(int i = 0; i < nFields; i++) { hasFieldUnpublishedChange[i] = true; }


		isUnpublished = true;

		isCreateUnpublished = true;
	}



	public LiObject getObject() {
		return object;
	}



	/**
	 * 
	 * @param front
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void sweep(GraphCrawler crawler) throws IOException, S8ShellStructureException {
		type.sweep(object, crawler);
	}



	/**
	 * 
	 * @param references
	 * @throws IOException
	 */
	public void sweepReferences(Queue<String> references) {
		type.collectReferencedBlocks(object, references);	
	}


	public void getByteCount(MemoryFootprint footprint) {
		type.computeFootprint(object, footprint);
	}



	public void reportChange(String fieldName) throws LiIOException {

		LiField field = type.getFieldByName(fieldName);
		if(field == null) {
			throw new LiIOException("Field "+fieldName+" is unknown from this object type");
		}


		// update field
		hasFieldUnpublishedChange[field.ordinal] = true;


		// internal notification schema
		if(!isUnpublished) {
			graph.reportCreate(this);
			isUnpublished = true;
		}
	}
	
	
	/**
	 * 
	 * @param fieldNames
	 * @throws LiIOException
	 */
	public void reportChanges(String... fieldNames) throws LiIOException {
		int n = fieldNames.length;
		for(int i = 0; i < n; i++) { reportChange(fieldNames[i]); }
	}




	/**
	 * 
	 * @param outflow
	 * @param object
	 * @throws BkException
	 * @throws IOException 
	 * @throws S8Exception 
	 */
	public void publish( List<LiObjectDelta> objectDeltas, ResolveScope scope) throws S8BuildException, IOException, S8Exception {

		if(isUnpublished) {


			/* publish header */

			List<LiFieldDelta> fieldDeltas = new ArrayList<>();
			if(isCreateUnpublished) {
				objectDeltas.add(new CreateLiObjectDelta(id, type, fieldDeltas));
				isCreateUnpublished = false;
			}
			else {
				objectDeltas.add(new UpdateLiObjectDelta(id, type, fieldDeltas));
			}


			/* <fields> */


			int nFields = type.getNumberOfFields();

			for(int i=0; i < nFields; i++) {
				if(hasFieldUnpublishedChange[i]) {

					// output field encoding
					fieldDeltas.add(type.getField(i).produceDiff(object, scope));

					hasFieldUnpublishedChange[i] = false; // consume flag
				}		
			}




			/* clear event */
			clearUpdateEvents();

			/* </fields> */

			// all changes now published, so clear flags
			isUnpublished = false;
		}
	}



	public LiGraph getBranch() {
		return graph;
	}


	private void clearUpdateEvents() {
		int n = hasFieldUnpublishedChange.length;
		for(int i = 0; i<n; i++) { hasFieldUnpublishedChange[i] = false; }
	}

}
