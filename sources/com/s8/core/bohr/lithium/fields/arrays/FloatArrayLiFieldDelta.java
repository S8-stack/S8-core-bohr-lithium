package com.s8.core.bohr.lithium.fields.arrays;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.space.objects.SpaceS8Object;
import com.s8.core.bohr.lithium.fields.LiField;
import com.s8.core.bohr.lithium.fields.LiFieldDelta;
import com.s8.core.bohr.lithium.type.BuildScope;



/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class FloatArrayLiFieldDelta extends LiFieldDelta {

	
	public final FloatArrayLiField field;
	
	public final float[] value;

	public FloatArrayLiFieldDelta(FloatArrayLiField field, float[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	@Override
	public LiField getField() {
		return field;
	}

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.set(object, value);
	}	

}
