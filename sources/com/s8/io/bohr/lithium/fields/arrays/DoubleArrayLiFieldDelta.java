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
public class DoubleArrayLiFieldDelta extends LiFieldDelta {

	public final DoubleArrayLiField field;
	
	public final double[] value;

	public DoubleArrayLiFieldDelta(DoubleArrayLiField field, double[] array) {
		super();
		this.field = field;
		this.value = array;
	}

	public @Override LiField getField() { return field; }

	@Override
	public void consume(LiS8Object object, BuildScope scope) throws LiIOException {
		field.handler.set(object, value); 
	}

}
