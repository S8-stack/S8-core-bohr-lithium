package com.s8.core.bohr.lithium.demos.repo2;

import com.s8.api.annotations.S8Field;
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
@S8ObjectType(name = "my-com-meeting-room#0002")
public class MyCommercialMeetingRoom extends MyCommercialFloorElement {

	public @S8Field(name = "x-table-dimension") double xTableDimension;
	
	public @S8Field(name = "y-table-dimension") double yTableDimension;
	
	public MyCommercialMeetingRoom() {
		super();
	}
	
	
	@Override
	public void init() throws S8IOException {
		baseInit();
		xTableDimension = Math.random();
		yTableDimension = Math.random();
		reportFieldUpdates("x-table-dimension", "y-table-dimension");
	}

	
	public static MyCommercialMeetingRoom create() throws S8IOException {
		MyCommercialMeetingRoom room = new MyCommercialMeetingRoom();
		room.init();
		return room;
	}


	@Override
	public void variate() throws S8IOException {
		double u = Math.random();
		if(u<0.3) {
			init();
		}
		else {
			xTableDimension = Math.random();
			yTableDimension = Math.random();
		}
		reportFieldUpdates("x-table-dimension", "y-table-dimension");
	}
}
