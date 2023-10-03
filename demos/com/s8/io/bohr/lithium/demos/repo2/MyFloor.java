    package com.s8.io.bohr.lithium.demos.repo2;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.api.objects.space.SpaceS8Object;



/**
 * 
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
@S8ObjectType(name = "my-floor", sub= {
		MyCommercialFloor.class,
		MyEmptyFloor.class
})
public abstract class MyFloor extends SpaceS8Object {


	public @S8Field(name = "x0") double x0;

	public @S8Field(name = "x1") double x1;

	public @S8Field(name = "y0") double y0;

	public @S8Field(name = "y1") double y1;

	public @S8Field(name = "ceiling-height") double ceilingHeight;

	
	public enum Face {
		
		POS_X, NEG_X, POS_Y, NEG_Y;
		
	}
	

	
	
	

	public MyFloor() {
		super();
	}


	public void baseInit() throws S8IOException {
		x0 = Math.random()*100;
		x1 = x0 + Math.random()*100;
		y0 = Math.random()*100;
		y1 = y0 + Math.random()*100;
		ceilingHeight = Math.random();
		
		reportFieldUpdates("x0", "x1", "y0", "y1", "ceiling-height");
	}
	

	public static MyFloor create() throws S8IOException {
		MyFloor element = null;
		if(Math.random()<0.3){
			element = MyEmptyFloor.create();
		}
		else {
			element = MyCommercialFloor.create();
		}
		return element;
	}



	
	protected abstract void init() throws S8IOException;
	
	protected abstract void variate() throws S8IOException;



}
