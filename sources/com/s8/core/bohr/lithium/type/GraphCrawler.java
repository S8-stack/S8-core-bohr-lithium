package com.s8.core.bohr.lithium.type;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.space.objects.SpaceS8Object;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public interface GraphCrawler {

	
	/**
	 * 
	 * @param object
	 * @throws S8IOException
	 */
	public void accept(SpaceS8Object object);
	
	
}
