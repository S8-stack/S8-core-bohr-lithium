package com.s8.io.bohr.lithium.fields.objects;

import com.s8.io.bohr.atom.BohrSerializable;
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
public class S8SerializableLiFieldDelta extends LiFieldDelta {
	
	
	public final S8SerializableLiField field;
	
	public final BohrSerializable value;

	
	
	public S8SerializableLiFieldDelta(S8SerializableLiField field, BohrSerializable value) {
		super();
		this.field = field;
		this.value = value;
	}

	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.set(object, value);
	}
	
	
	@Override
	public LiField getField() { 
		return field;
	}
}
