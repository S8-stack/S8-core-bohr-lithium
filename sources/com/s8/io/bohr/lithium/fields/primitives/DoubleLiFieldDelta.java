package com.s8.io.bohr.lithium.fields.primitives;

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
public class DoubleLiFieldDelta extends LiFieldDelta {



	public final DoubleLiField field;

	public final double value;

	public DoubleLiFieldDelta(DoubleLiField field, double value) {
		super();
		this.field = field;
		this.value = value;
	}
	

	@Override
	public void consume(LiS8Object object, BuildScope scope) throws LiIOException {
		field.handler.setDouble(object, value);
	}


	@Override
	public LiField getField() { 
		return field;
	}


}
