package com.s8.io.bohr.lithium.fields.primitives;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.type.BuildScope;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class LongLiFieldDelta extends LiFieldDelta {


	public final LongLiField field;

	public final long value;

	public LongLiFieldDelta(LongLiField field, long value) {
		super();
		this.field = field;
		this.value = value;
	}

	public @Override LongLiField getField() { return field; }

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.setLong(object, value);
	}

}

