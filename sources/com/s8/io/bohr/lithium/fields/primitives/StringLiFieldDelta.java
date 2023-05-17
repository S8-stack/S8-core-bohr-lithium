package com.s8.io.bohr.lithium.fields.primitives;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
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
public class StringLiFieldDelta extends LiFieldDelta {


	public final StringLiField field;

	public final String value;

	public StringLiFieldDelta(StringLiField field, String value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override 
	public StringLiField getField() { 
		return field;
	}


	@Override
	public void consume(LiS8Object object, BuildScope scope) throws LiIOException {
		field.handler.setString(object, value);
	}


}
