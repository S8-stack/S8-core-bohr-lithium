package com.s8.io.bohr.lithium.fields.primitives;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;

/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class IntegerLiFieldDelta  extends LiFieldDelta {

	public final IntegerLiField field;
	public final int value;

	public IntegerLiFieldDelta(IntegerLiField field, int value) {
		super();
		this.field = field;
		this.value = value;
	}


	@Override
	public LiField getField() { 
		return field; 
	}

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.setInteger(object, value);
	}
}
