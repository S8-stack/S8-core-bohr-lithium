package com.s8.io.bohr.lithium.fields.arrays;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.LiS8Object;
import com.s8.io.bohr.lithium.type.BuildScope;



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
	public void consume(LiS8Object object, BuildScope scope) throws LiIOException {
		field.handler.set(object, value);
	}	

}
