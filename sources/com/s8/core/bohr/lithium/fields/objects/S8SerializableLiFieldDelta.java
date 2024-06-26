package com.s8.core.bohr.lithium.fields.objects;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.flow.space.objects.SpaceS8Object;
import com.s8.api.serial.S8Serializable;
import com.s8.core.bohr.lithium.fields.LiField;
import com.s8.core.bohr.lithium.fields.LiFieldDelta;
import com.s8.core.bohr.lithium.type.BuildScope;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8SerializableLiFieldDelta extends LiFieldDelta {
	
	
	public final S8SerializableLiField field;
	
	public final S8Serializable value;

	
	
	public S8SerializableLiFieldDelta(S8SerializableLiField field, S8Serializable value) {
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
