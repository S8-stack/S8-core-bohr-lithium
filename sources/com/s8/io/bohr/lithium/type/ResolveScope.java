package com.s8.io.bohr.lithium.type;

import java.io.IOException;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;


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
	public String resolveId(SpaceS8Object object) throws S8IOException;

}
