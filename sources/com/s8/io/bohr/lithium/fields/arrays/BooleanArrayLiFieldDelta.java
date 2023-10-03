package com.s8.io.bohr.lithium.fields.arrays;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;


/**
 * later aggregate
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class BooleanArrayLiFieldDelta extends LiFieldDelta {


	public final BooleanArrayLiField field;

	public final boolean[] value;

	public BooleanArrayLiFieldDelta(BooleanArrayLiField field, boolean[] array) {
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
