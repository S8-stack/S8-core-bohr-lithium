package com.s8.io.bohr.lithium.fields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.s8.api.exceptions.S8BuildException;
import com.s8.io.bohr.lithium.handlers.LiHandler;
import com.s8.io.bohr.lithium.properties.LiFieldProperties;


/**
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 *
 */
public abstract class LiFieldBuilder {
	
	
	
	/**
	 * 
	 * @return
	 */
	public LiFieldProperties properties;
	
	
	public LiHandler handler;
	

	/**
	 * 
	 * @param field
	 */
	public LiFieldBuilder(LiFieldProperties properties, LiHandler handler) {
		super();
		this.properties = properties;
		this.handler = handler;
	}

	
	/**
	 * 
	 * @param field
	 * @throws S8BuildException
	 */
	public void attachField(Field field) throws S8BuildException {
		LiFieldProperties props = this.getPrototype().captureField(field);
		if (props != null) {
			this.properties.merge(props);
			this.handler.attachField(field);
		}
		else {
			throw new S8BuildException("Field type is not matching field builder", field);
		}
	}
	

	/**
	 * 
	 * @param method
	 * @param isValidationEnabled
	 * @throws LithTypeBuildException
	 */
	public void attachGetMethod(Method method) throws S8BuildException {
		LiFieldProperties props = getPrototype().captureGetter(method);
		if (props != null) {
			properties.merge(props);
			handler.attachGetMethod(method);
		}
		else {
			throw new S8BuildException("Getter type is not matching", method);
		}
	}

	/**
	 * 
	 * @param method
	 * @param isValidationEnabled
	 * @throws LithTypeBuildException
	 */
	public void attachSetMethod(Method method) throws S8BuildException {
		LiFieldProperties props = getPrototype().captureSetter(method);
		if (props != null) {
			properties.merge(props);
			handler.attachSetMethod(method);
		}
		else {
			throw new S8BuildException("Getter type is not matching", method);
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public abstract LiFieldPrototype getPrototype();
	
	

	/**
	 * 
	 * @return
	 * @throws S8BuildException 
	 * @throws com.s8.io.bohr.atom.S8BuildException 
	 */
	public abstract LiField build(int ordinal) throws S8BuildException;


}
