package com.s8.core.bohr.lithium.demos.repo2;

import java.util.ArrayList;
import java.util.List;

import com.s8.api.annotations.S8Field;
import com.s8.api.annotations.S8ObjectType;
import com.s8.api.exceptions.S8IOException;


/**
 * 
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
@S8ObjectType(name = "my-commercial-floor")
public class MyCommercialFloor extends MyFloor {

	
	
	
	public  @S8Field(name = "elements") List<MyCommercialFloorElement> elements;
	
	
	public MyCommercialFloor() {
		super();
	}
	
	
	@Override
	public void init() throws S8IOException {
		baseInit();
		
		int n = (int) (Math.random()*12) + 4;
		elements = new ArrayList<>();
		for(int i=0; i<n; i++) {
			elements.add(MyCommercialFloorElement.create());
		}
		reportFieldUpdate("elements");
	}
	


	@Override
	protected void variate() throws S8IOException {
		double u = Math.random();
		if(u<0.2) {
			init();
		}
		else {
			int n = (int) (Math.random()*0.999999*elements.size());
			for(int i=0; i<n; i++) {
				int index = (int) (Math.random()*0.999999*elements.size());
				elements.get(index).variate();
			}
		}
		reportFieldUpdate("elements");
	}
	
	public static MyCommercialFloor create() throws S8IOException {
		MyCommercialFloor floor = new MyCommercialFloor();
		floor.init();
		return floor;
	}


}
