package com.s8.core.bohr.lithium.demos.repo2;

import com.s8.api.annotations.S8ObjectType;
import com.s8.api.exceptions.S8IOException;



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
	protected void init() throws S8IOException {
		baseInit();
	}

	@Override
	protected void variate() {
		
	}

}
