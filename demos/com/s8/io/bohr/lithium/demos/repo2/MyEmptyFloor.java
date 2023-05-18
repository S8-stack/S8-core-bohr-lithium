package com.s8.io.bohr.lithium.demos.repo2;

import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.lithium.exceptions.LiIOException;



/**
 * 
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
@S8ObjectType(name = "empty-floor")
public class MyEmptyFloor extends MyFloor {

	public MyEmptyFloor() {
		super();
	}

	public static MyFloor create() {
		MyEmptyFloor floor = new MyEmptyFloor();
		return floor;
	}

	@Override
	protected void init() throws LiIOException {
		baseInit();
	}

	@Override
	protected void variate() {
		
	}

}
