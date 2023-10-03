package com.s8.io.bohr.lithium.fields.objects;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
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
	public void operate(SpaceS8Object object, BuildScope scope) throws S8IOException {

		if(index != null) {
			scope.appendBinding(new Binding() {

				@Override
				public void resolve(BuildScope scope) throws S8IOException {
					SpaceS8Object struct = scope.retrieveObject(index);
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
