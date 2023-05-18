package com.s8.io.bohr.lithium.fields.objects;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.LiObject;
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
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.set(object, value);
	}


	public @Override LiField getField() { return field; }

}
