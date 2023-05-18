package com.s8.io.bohr.lithium.fields.arrays;

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
public class LongArrayLiFieldDelta extends LiFieldDelta {

	public final LongArrayLiField field;
	
	public final long[] value;

	public LongArrayLiFieldDelta(LongArrayLiField field, long[] array) {
		super();
		this.field = field;
		this.value = array;
	}


	@Override
	public LiField getField() { 
		return field;
	}


	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.set(object, value);
	}

}
