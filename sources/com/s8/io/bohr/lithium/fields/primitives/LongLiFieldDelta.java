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
	public void consume(LiS8Object object, BuildScope scope) throws LiIOException {
		field.handler.setLong(object, value);
	}

}

