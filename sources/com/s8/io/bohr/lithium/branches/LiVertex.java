package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.util.Queue;

import com.s8.io.bohr.atom.S8BuildException;
import com.s8.io.bohr.atom.S8Exception;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldComposer;
import com.s8.io.bohr.lithium.object.LiS8Object;
import com.s8.io.bohr.lithium.type.GraphCrawler;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.LiTypeComposer;
import com.s8.io.bohr.lithium.type.LiTypeParser;
import com.s8.io.bohr.lithium.type.ResolveScope;
import com.s8.io.bytes.alpha.ByteOutflow;
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

	
	
	public final LiBranch branch;
	

	
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
	
	

	/**
	 * The port used to expose this object
	 */
	public int port = -1;


	
	
	/**
	 * 
	 */
	public LiTypeParser typeParser;
	
	
	/**
	 * 
	 */
	public LiTypeComposer typeComposer;
	
	
	
	private boolean isUnpublished = false;
	/**
	 * 
	 */
	private boolean isCreateUnpublished = false;
	
	
	public final LiS8Object object;
	
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
	public LiVertex(LiBranch branch, String id, LiS8Object object) throws LiIOException {
		super();
		this.branch = branch;
		this.id = id;
		
		LiType type = branch.codebase.getType(object);
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
	
	
	
	public LiS8Object getObject() {
		return object;
	}
	
	
	
	public LiType getType() throws LiIOException {
		if(typeComposer == null) {
			typeComposer = branch.outbound.getComposer(object.getClass());
		}
		return typeComposer.type;
	}

	/**
	 * 
	 * @param front
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void sweep(GraphCrawler crawler) throws IOException, S8ShellStructureException {
		getType().sweep(object, crawler);
	}
	
	
	
	/**
	 * 
	 * @param references
	 * @throws IOException
	 */
	public void sweepReferences(Queue<String> references) {
		try {
			getType().collectReferencedBlocks(object, references);
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void getByteCount(MemoryFootprint footprint) {
		try {
			getType().computeFootprint(object, footprint);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
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
			branch.outbound.reportCreate(this);
			isUnpublished = true;
		}
	}



	
	/**
	 * 
	 * @param outflow
	 * @param object
	 * @throws BkException
	 * @throws IOException 
	 * @throws S8Exception 
	 */
	public void publish(ByteOutflow outflow) throws S8BuildException, IOException, S8Exception {

		if(isUnpublished) {

			LiOutbound outbound = branch.outbound;
			
			// type composer
			if(typeComposer == null) {
				typeComposer = outbound.getComposer(object.getClass());
			}
			
			/* publish header */
			if(isCreateUnpublished) {
				typeComposer.publish_CREATE_NODE(outflow, id);
				isCreateUnpublished = false;
			}
			else {
				typeComposer.publish_UPDATE_NODE(outflow, id);
			}
			
			
			/* <fields> */

			LiFieldComposer[] fieldComposers = typeComposer.fieldComposers;
			int nFields = fieldComposers.length;

			ResolveScope resolveScope = branch.resolveScope;
			
			for(int ordinal=0; ordinal < nFields; ordinal++) {

				LiFieldComposer fieldComposer = fieldComposers[ordinal];
				if(hasFieldUnpublishedChange[ordinal]) {

					// output field encoding
					fieldComposer.compose(object, outflow, resolveScope);
					
					hasFieldUnpublishedChange[ordinal] = false; // consume flag
				}		
			}
			
			
			
			/* clear event */
			clearUpdateEvents();
			 
			/* </fields> */

			typeComposer.publishCloseNode(outflow);
		
			// all changes now published, so clear flags
			isUnpublished = false;
		}
	}

	
	
	public LiBranch getBranch() {
		return branch;
	}


	private void clearUpdateEvents() {
		int n = hasFieldUnpublishedChange.length;
		for(int i = 0; i<n; i++) { hasFieldUnpublishedChange[i] = false; }
	}
	
}
