package com.s8.core.bohr.lithium.fields.primitives;

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
public class FloatLiFieldDelta extends LiFieldDelta {


	public final FloatLiField field;

	public final float value;

	public FloatLiFieldDelta(FloatLiField field, float value) {
		super();
		this.field = field;
		this.value = value;
	}

	public @Override LiField getField() { return field; }

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.setFloat(object, value);
	}


}
