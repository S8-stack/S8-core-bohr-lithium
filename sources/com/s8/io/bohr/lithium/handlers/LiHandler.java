package com.s8.io.bohr.lithium.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public interface LiHandler {


	public enum Kind {
		FIELD_BASED, METHOD_BASED;
	}


	/**
	 * 
	 * @return
	 */
	public abstract Kind getKind();

	
	/**
	 * 
	 * @param isGetting
	 * @return
	 */
	public abstract String describe(boolean isGetting);
	
	
	
	/**
	 * 
	 * @param field
	 * @throws LithTypeBuildException
	 */
	public void attachField(Field field) throws S8BuildException;
	
	
	/**
	 * 
	 * @param method
	 * @throws LithTypeBuildException
	 */
	public void attachGetMethod(Method method) throws S8BuildException;

	
	/**
	 * 
	 * @param method
	 * @throws LithTypeBuildException
	 */
	public void attachSetMethod(Method method) throws S8BuildException;


	/**
	 * default getter
	 * 
	 * @param object
	 * @return
	 * @throws LthSerialException
	 */
	public Object get(Object object) throws S8IOException;


	/**
	 * default setter
	 * 
	 * @param object
	 * @param value
	 * @throws LthSerialException
	 */
	public void set(Object object, Object value) throws S8IOException;


	public byte getByte(Object object) throws S8IOException;
	

	public void setByte(Object object, byte value) throws S8IOException;
	

	public boolean getBoolean(Object object) throws S8IOException;


	public void setBoolean(Object object, boolean value) throws S8IOException;


	public double getDouble(Object object) throws S8IOException;
	
	
	public void setDouble(Object object, double value) throws S8IOException;
	
	
	public void setFloat(Object object, float value) throws S8IOException;


	public float getFloat(Object object) throws S8IOException;


	public void setInteger(Object object, int value) throws S8IOException;


	public int getInteger(Object object) throws S8IOException;


	public void setLong(Object object, long value) throws S8IOException;


	public long getLong(Object object) throws S8IOException;


	public void setShort(Object object, short value) throws S8IOException;


	public short getShort(Object object) throws S8IOException;


	public void setString(Object object, String value) throws S8IOException;


	public String getString(Object object) throws S8IOException;






}
