package com.s8.io.bohr.lithium.fields.objects;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.serial.BohrSerializable;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
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
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {
		field.handler.set(object, value);
	}
	
	
	@Override
	public LiField getField() { 
		return field;
	}
}
