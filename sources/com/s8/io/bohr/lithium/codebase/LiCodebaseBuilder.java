package com.s8.io.bohr.lithium.codebase;

import java.util.ArrayDeque;
import java.util.Queue;

import com.s8.api.exceptions.S8BuildException;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.io.bohr.lithium.fields.LiFieldFactory;
import com.s8.io.bohr.lithium.type.LiTypeBuilder;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LiCodebaseBuilder {
	
	/**
	 * factory
	 */
	private LiFieldFactory fieldFactory;
	

	/**
	 * 
	 */
	private LiCodebase codebase;


	/**
	 * 
	 */
	private Queue<LiTypeBuilder> queue;

	
	private boolean isFinished;
	private boolean isBuildingInProgress;

	private boolean isVerbose;

	/**
	 * 
	 */
	LiCodebaseBuilder(boolean isVerbose) {
		super();
		
		// init context
		codebase = new LiCodebase(isVerbose);
		
		// factory
		fieldFactory = new LiFieldFactory();
		
		// queue
		queue = new ArrayDeque<LiTypeBuilder>();


		this.isVerbose = isVerbose;
		
		isBuildingInProgress = false;
	}



	/**
	 * 
	 * @return
	 */
	public LiFieldFactory getGphFieldFactory() {
		return fieldFactory;
	}
	


	public void pushObjectTypes(Class<?>... types) throws S8BuildException {
		if(isFinished) {
			throw new S8BuildException("codebase construction process is now terminated");
		}
		
		for(Class<?> type : types) {
			pushObjectType(type);
		}
	}
	
	

	/**
	 * 
	 * @param type
	 * @throws LithTypeBuildException
	 * @throws LthSerialException 
	 */
	public void pushObjectType(Class<?> type) throws S8BuildException {
		if(isFinished) {
			throw new S8BuildException("codebase construction process is now terminated");
		}
		
		// must not have already been added
		if(!codebase.isTypeKnown(type)) {
			
			// must be annotated
			if(type.isAnnotationPresent(S8ObjectType.class)) {
				queue.add(new LiTypeBuilder(type, isVerbose));
			}
			else if(isVerbose) {
				System.out.println("Type "+type+" has not been accepted because of lacking annotation");
			}	
		}
		
		if(!isBuildingInProgress) {
			buildBuffered();
		}
	}
	
	
	/**
	 * 
	 * @return
	 * @throws LthSerialException
	 * @throws LithTypeBuildException
	 * @throws BohrTypeBuildingException
	 */
	private void buildBuffered() throws S8BuildException {

		isBuildingInProgress = true;

		int count = 0;
		LiTypeBuilder typeBuilder;
		while(!queue.isEmpty() && count<65536) {
			typeBuilder = queue.poll();

		
			// check that not already built
			if(!codebase.isTypeKnown(typeBuilder.getRawType())) {
				
				// type
				boolean isBuildable = typeBuilder.process(this);

				// register
				if(isBuildable) { codebase.put(typeBuilder.build()); }
			}

			count++;
		}
		
		isBuildingInProgress = false;
	}
	
	
	public LiCodebase build() throws S8BuildException {
		if(isFinished) {
			throw new S8BuildException("codebase construction process is already terminated");
		}
		codebase.nTypes = codebase.typesByRuntimeName.size();
		isFinished = true;
		return codebase;
	}
}
