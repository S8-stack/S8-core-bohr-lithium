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
public class ShortArrayLiFieldDelta extends LiFieldDelta {


	public final ShortArrayLiField field;
	
	public final short[] value;

	public ShortArrayLiFieldDelta(ShortArrayLiField field, short[] array) {
		super();
		this.field = field;
		this.value = array;
	}


	public @Override LiField getField() { return field; }

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.set(object, value);
	}

}
