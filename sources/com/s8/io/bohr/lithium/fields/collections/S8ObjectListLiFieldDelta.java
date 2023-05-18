package com.s8.io.bohr.lithium.fields.collections;

import java.util.ArrayList;
import java.util.List;

import com.s8.io.bohr.lithium.exceptions.LiIOException;
import com.s8.io.bohr.lithium.fields.LiField;
import com.s8.io.bohr.lithium.fields.LiFieldDelta;
import com.s8.io.bohr.lithium.object.LiObject;
import com.s8.io.bohr.lithium.type.BuildScope;
import com.s8.io.bohr.lithium.type.BuildScope.Binding;




/**
 * <p> Internal object ONLY</p>
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class S8ObjectListLiFieldDelta<T extends LiObject> extends LiFieldDelta {

	
	public final S8ObjectListLiField<T> field;
	
	public final String[] indices;

	/**
	 * 
	 * @param field
	 * @param indices
	 */
	public S8ObjectListLiFieldDelta(S8ObjectListLiField<T> field, String[] indices) {
		super();
		this.field = field;
		this.indices = indices;
	}


	public @Override LiField getField() { return field; }


	@Override
	public void operate(LiObject object, BuildScope scope) throws LiIOException {

		if(indices!=null) {
			int n = indices.length;
			List<T> array = new ArrayList<>(n);


			scope.appendBinding(new Binding() {
				@SuppressWarnings("unchecked")
				@Override
				public void resolve(BuildScope scope) throws LiIOException {
					for(int i=0; i<n; i++) {
						String itemGphIndex = indices[i];
						if(itemGphIndex != null) {
							array.add((T) scope.retrieveObject(itemGphIndex));
						}
						else {
							array.add(null);
						}
					}
				}
			});	
			field.handler.set(object, array);
		}
		else {
			field.handler.set(object, null);
		}
	}


}
