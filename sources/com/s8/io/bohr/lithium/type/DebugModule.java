package com.s8.io.bohr.lithium.type;

import java.io.IOException;
import java.io.Writer;

import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;


/**
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public class DebugModule {


	/**
	 * 
	 */
	private final LiType type;


	/**
	 * 
	 * @param type
	 */
	public DebugModule(LiType type) {
		super();
		this.type = type;
	}




	/**
	 * 
	 * @param object
	 * @param writer
	 * @throws IOException
	 * @throws S8ShellStructureException 
	 */
	public void print(SpaceS8Object object, ResolveScope scope, Writer writer) throws IOException, S8ShellStructureException {
		
		// advertise class
		writer.write('(');
		writer.write(object.getClass().getCanonicalName());
		writer.write(')');

		// adevrtise index
		writer.write(" index="+scope.resolveId(object));
		writer.write(" {\n");

		// loop through fields
		int nFields = type.fields.length;
		for(int i=0; i<nFields; i++) {
			writer.append('\t');
			type.fields[i].print(object, scope, writer);
			writer.append('\n');
		}
		writer.write("}\n");
	}



}
