package com.s8.io.bohr.lithium.demos.repo2;

import java.util.ArrayList;
import java.util.List;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.annotations.S8Field;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.api.objects.space.SpaceS8Object;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 * 
 */
@S8ObjectType(name = "my-building")
public class MyBuilding extends SpaceS8Object {

	
	public @S8Field(name = "n-floors") int nFloors;
	
	public @S8Field(name = "lower-ground-floor") MyFloor lowerGroundFloor;
	
	public @S8Field(name = "ground-floor") MyFloor groundFloor;
	
	public @S8Field(name = "upper-floors") List<MyFloor> upperGroundFloors;
	
	
	public MyBuilding() {
		super();
	}
	

	private void init() throws S8IOException {
		nFloors = (int) (Math.random()*128) + 3;
		lowerGroundFloor = MyFloor.create();
		groundFloor = MyFloor.create();
		
		
		upperGroundFloors = new ArrayList<>(nFloors+2);
		for(int i=0; i<nFloors; i++) {
			upperGroundFloors.add(MyFloor.create());	
		}
	}

	
	public static MyBuilding create() throws S8IOException {
		MyBuilding building = new MyBuilding();
		building.init();
		return building;
	}
	
	public void variate() throws S8IOException {
		
		
		lowerGroundFloor.init();
		for(MyFloor floor : upperGroundFloors) {  if(Math.random()<0.5) { floor.init(); } }
		reportFieldUpdates("lower-ground-floor", "upper-floors");
	}

}
