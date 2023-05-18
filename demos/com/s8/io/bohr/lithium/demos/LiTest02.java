package com.s8.io.bohr.lithium.demos;

import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.demos.repo2.MyBuilding;

public class LiTest02 {

	public static void main(String[] args) throws Exception {
		
		
		LiCodebase codebase = LiCodebase.from(MyBuilding.class);
		codebase.DEBUG_print();
		
		MyBuilding building = MyBuilding.create();
		LiBranch branch = new LiBranch("com.toto.123.098", "master", codebase);
		branch.expose(2, building);
		
		
	}

}
