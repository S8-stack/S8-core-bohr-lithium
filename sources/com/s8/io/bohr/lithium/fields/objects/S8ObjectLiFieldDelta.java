package com.s8.io.bohr.lithium.fields.objects;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.BuildScope.Binding;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8ObjectLiFieldDelta extends LiFieldDelta {



	public final S8ObjectLiField field;

	/**
	 * 
	 */
	public final String index;


	/**
	 * 
	 * @param fieldCode
	 * @param field
	 * @param index
	 */
	public S8ObjectLiFieldDelta(S8ObjectLiField field, String index) {
		super();
		this.field = field;
		this.index = index;
	}


	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {

		if(index != null) {
			scope.appendBinding(new Binding() {

				@Override
				public void resolve(BuildScope scope) throws LiIOException {
					LiObject struct = scope.retrieveObject(index);
					field.handler.set(object, struct);
				}
			});
		}
		else {
			// nothing to do
			field.handler.set(object, null);
		}
	}


	@Override
	public S8ObjectLiField getField() { 
		return field;
	}


}
