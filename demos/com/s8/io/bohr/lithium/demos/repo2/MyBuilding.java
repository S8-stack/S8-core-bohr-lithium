package com.s8.io.bohr.lithium.demos.repo2;

import java.util.ArrayList;
import java.util.List;

import com.s8.io.bohr.atom.annotations.S8Field;
import com.s8.io.bohr.atom.annotations.S8ObjectType;
import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.object.LiObject;


/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 * 
 */
@S8ObjectType(name = "my-building")
public class MyBuilding extends LiObject {

	public final static long HAS_CHANGED = 0x02;
	
	public @S8Field(name = "n-floors", mask = HAS_CHANGED) int nFloors;
	
	public @S8Field(name = "lower-ground-floor", mask = HAS_CHANGED) MyFloor lowerGroundFloor;
	
	public @S8Field(name = "ground-floor", mask = HAS_CHANGED) MyFloor groundFloor;
	
	public @S8Field(name = "upper-floors", mask = HAS_CHANGED) List<MyFloor> upperGroundFloors;
	
	
	public MyBuilding() {
		super();
	}
	

	private void init() throws LiIOException {
		nFloors = (int) (Math.random()*128) + 3;
		lowerGroundFloor = MyFloor.create();
		groundFloor = MyFloor.create();
		
		
		upperGroundFloors = new ArrayList<>(nFloors+2);
		for(int i=0; i<nFloors; i++) {
			upperGroundFloors.add(MyFloor.create());	
		}
	}

	
	public static MyBuilding create() throws LiIOException {
		MyBuilding building = new MyBuilding();
		building.init();
		return building;
	}
	
	public void variate() throws LiIOException {
		
		
		lowerGroundFloor.init();
		for(MyFloor floor : upperGroundFloors) {  if(Math.random()<0.5) { floor.init(); } }
		reportFieldUpdates("lower-ground-floor", "upper-floors");
	}

}
