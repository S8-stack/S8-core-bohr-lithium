package com.s8.io.bohr.lithium.branches;

import java.io.IOException;
import java.io.Writer;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.atom.S8ShellStructureException;
import com.s8.io.bohr.lithium.type.LiType;
import com.s8.io.bohr.lithium.type.ResolveScope;


/**
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class DebugModule {

	private final LiGraph graph;
	
	public DebugModule(LiGraph graph) {
		super();
		this.graph = graph;
	}
	


	
	
	/**
	 * 
	 * @throws IOException 
	 */
	public void print(ResolveScope scope, Writer writer) throws IOException {
		
		writer.write("<shell:>\n");
		
		graph.vertices.forEach((index, vertex) -> {
			try {
				LiType type = vertex.type;
				SpaceS8Object object = vertex.getObject();
				type.print(object, scope, writer);
			} 
			catch (S8IOException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (S8ShellStructureException e) {
				e.printStackTrace();
			}
		});
		writer.write("\n</shell:>");
	}
}
