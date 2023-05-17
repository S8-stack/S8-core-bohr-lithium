package com.s8.io.bohr.lithium.type;

import java.io.IOException;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.object.LiS8Object;


/**
 * 
 * @author pierreconvert
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public interface ResolveScope {
	
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws IOException 
	 */
	public String resolveId(LiS8Object object) throws LiIOException;

}
