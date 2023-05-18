package com.s8.io.bohr.lithium.fields.objects;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bohr.lithium.object.LiRef;
import com.s8.io.bohr.lithium.type.BuildScope;

/**
 * 
 *
 * @author Pierre Convert Copyright (C) 2022, Pierre Convert. All rights
 *         reserved.
 * 
 */
public class S8RefLiFieldDelta extends LiFieldDelta {

	public final S8RefLiField field;

	public final LiRef ref;

	/**
	 * 
	 * @param fieldCode
	 * @param field
	 * @param address
	 * @param slot
	 */
	public S8RefLiFieldDelta(S8RefLiField field, LiRef ref) {
		super();
		this.field = field;
		this.ref = ref;
	}

	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {
		field.handler.set(object, ref);
	}

	@Override
	public LiField getField() {
		return field;
	}


}
