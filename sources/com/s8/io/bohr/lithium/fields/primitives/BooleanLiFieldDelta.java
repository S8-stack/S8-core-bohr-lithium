package com.s8.io.bohr.lithium.fields.primitives;

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
public class BooleanLiFieldDelta extends LiFieldDelta {


	public final BooleanLiField field;

	public final boolean value;

	public BooleanLiFieldDelta(BooleanLiField field, boolean value) {
		super();
		this.field = field;
		this.value = value;
	}


	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.setBoolean(object, value);
	}
	
	
	@Override
	public LiField getField() { 
		return field;
	}

}
