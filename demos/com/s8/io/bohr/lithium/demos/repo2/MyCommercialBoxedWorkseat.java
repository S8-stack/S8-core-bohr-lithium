package com.s8.io.bohr.lithium.demos.repo2;

import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.lithium.exceptions.LiIOException;


/**
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
@S8ObjectType(name = "MyCommercialBoxedWorkseat")
public class MyCommercialBoxedWorkseat extends MyCommercialFloorElement {


	public @S8Field(name = "x-deck-dimension", mask = HAS_CHANGED) double xDeckDimension;

	public @S8Field(name = "y-deck-dimension", mask = HAS_CHANGED) double yDeckDimension;


	public MyCommercialBoxedWorkseat() {
		super();
	}



	@Override
	public void init() throws LiIOException {
		baseInit();
		xDeckDimension = Math.random();
		yDeckDimension = Math.random();
		reportFieldUpdates("x-deck-dimension", "y-deck-dimension");
	}


	public static MyCommercialBoxedWorkseat create() throws LiIOException {
		MyCommercialBoxedWorkseat room = new MyCommercialBoxedWorkseat();
		room.init();
		return room;
	}

	@Override
	public void variate() throws LiIOException {
		double u = Math.random();
		if(u<0.3) {
			init();
		}
		else {
			xDeckDimension = Math.random();
			yDeckDimension = Math.random();
		}
		reportFieldUpdates("x-deck-dimension", "y-deck-dimension");
	}
}
