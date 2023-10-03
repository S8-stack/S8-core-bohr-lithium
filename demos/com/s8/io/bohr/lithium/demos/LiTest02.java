package com.s8.io.bohr.lithium.demos;

import java.util.List;

import com.s8.api.exceptions.S8IOException;
import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.branches.LiGraphDelta;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.demos.repo2.MyBuilding;

public class LiTest02 {

	public static void main(String[] args) throws Exception {
		
		
		LiCodebase codebase = LiCodebase.from(MyBuilding.class);
		codebase.DEBUG_print();
		
		MyBuilding building = MyBuilding.create();
		LiBranch branch = new LiBranch("master", codebase);
		branch.expose(2, building);
		
		
		building.variate();
		System.out.println("val 0:"+building.upperGroundFloors.get(4).y0);
		
		branch.commit();
		List<LiGraphDelta> deltas = branch.pullDeltas();
		
		LiBranch branch2 = new LiBranch("master", codebase);
		deltas.forEach(d -> {
			try { branch2.pushDelta(d); }catch (S8IOException e) { e.printStackTrace(); }
		});
		
		MyBuilding b2 = (MyBuilding) branch2.getExposed(2);
		
		System.out.println("val 0:"+b2.upperGroundFloors.get(4).y0);
		
	}

}
