package com.s8.io.bohr.lithium.demos;

import java.io.OutputStreamWriter;

import com.s8.io.bohr.lithium.branches.LiBranch;
import com.s8.io.bohr.lithium.codebase.LiCodebase;
import com.s8.io.bohr.lithium.demos.repo2.MyBuilding;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;

public class LiTest02 {

	public static void main(String[] args) throws Exception {
		
		
		LiCodebase codebase = LiCodebase.from(MyBuilding.class);
		codebase.DEBUG_print();
		
		MyBuilding building = MyBuilding.create();
		LiBranch branch = new LiBranch("com.toto.123.098", "master", codebase);
		branch.expose(2, building);
		
		
		LinkedByteOutflow outflow = new LinkedByteOutflow(1024);
		branch.pushSequence(outflow);
		System.out.println(outflow);
		
		LinkedByteInflow inflow = new LinkedByteInflow(outflow.getHead());
		LiBranch rBranch = new LiBranch("com.toto.123.098", "master", codebase);
		rBranch.pullSequence(inflow);
		System.out.println(rBranch.toString());
		
		//rBranch.print(new OutputStreamWriter(System.out));
		
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		rBranch.deepCompare(branch, writer);
		writer.close();
	}

}
