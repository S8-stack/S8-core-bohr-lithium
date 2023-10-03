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
 *
 */
@S8ObjectType(name = "my-commercial-floor-element", sub= {
		MyCommercialBoxedWorkseat.class,
		MyCommercialMeetingRoom.class
})
public abstract class MyCommercialFloorElement extends SpaceS8Object {
	


	public @S8Field(name = "tag") String tag;
	
	public @S8Field(name = "x0") double x0;

	public @S8Field(name = "x1") double x1;

	public @S8Field(name = "y0") double y0;

	public @S8Field(name = "y1") double y1;

	public @S8Field(name = "door-location-face") MyFloor.Face doorLocationFace;
	
	
	/**
	 * Must be between 0 and 1
	 */
	public @S8Field(name = "door-location-coordinate") double doorLocationCoordinate;
	
	
	public MyCommercialFloorElement() {
		super();
	}
	

	public void baseInit() throws S8IOException {
		x0 = Math.random()*100;
		x1 = x0 + Math.random()*100;
		y0 = Math.random()*100;
		y1 = y0 + Math.random()*100;
		
		
		doorLocationFace = MyFloor.Face.values()[(int) (Math.random()*4)];
		doorLocationCoordinate = Math.random();
		reportFieldUpdates("x0", "x1", "y0", "y1", "door-location-face", "door-location-coordinate");
	}
	

	public abstract void init() throws S8IOException;
	
	
	public static MyCommercialFloorElement create() throws S8IOException {
		MyCommercialFloorElement element = null;
		if(Math.random()<0.3){
			element = MyCommercialMeetingRoom.create();
		}
		else {
			element = MyCommercialBoxedWorkseat.create();
		}
		return element;
	}


	public abstract void variate() throws S8IOException;
	
	
}
