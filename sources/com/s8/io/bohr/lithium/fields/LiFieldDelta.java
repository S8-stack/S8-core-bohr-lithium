package com.s8.io.bohr.lithium.fields;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.type.BuildScope;


/**
 * <p><code>NdFieldDelta</code> are immutable!</p>
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class LiFieldDelta {
		
	
	public LiFieldDelta() {
		super();
	}
	
	/**
	 * 
	 * @param object
	 * @throws LthSerialException 
	 */
	public abstract void operate(SpaceS8Object object, BuildScope scope) throws S8IOException;
	
	public abstract LiField getField();
}
