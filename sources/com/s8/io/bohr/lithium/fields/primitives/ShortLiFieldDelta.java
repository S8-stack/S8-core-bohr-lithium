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
public class ShortLiFieldDelta extends LiFieldDelta {


	public final ShortLiField field;

	public final short value;

	
	/**
	 * 
	 * @param field
	 * @param value
	 */
	public ShortLiFieldDelta(ShortLiField field, short value) {
		super();
		this.field = field;
		this.value = value;
	}

	
	@Override
	public LiField getField() { 
		return field; 
	}

	
	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.setShort(object, value);
	}
	
}
