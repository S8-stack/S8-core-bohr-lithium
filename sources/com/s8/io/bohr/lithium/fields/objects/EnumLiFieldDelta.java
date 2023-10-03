package com.s8.io.bohr.lithium.fields.objects;

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
public class EnumLiFieldDelta extends LiFieldDelta {


	public final EnumLiField field;

	public final Object value;

	public EnumLiFieldDelta(EnumLiField field, Object value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.set(object, value);
	}


	public @Override LiField getField() { return field; }

}
