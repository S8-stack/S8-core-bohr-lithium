package com.s8.io.bohr.lithium.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.s8.api.exceptions.S8BuildException;
import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.annotations.S8Getter;
import com.s8.api.objects.annotations.S8ObjectType;
import com.s8.api.objects.annotations.S8Setter;

/**
 * 
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public class MethodLiHandler implements LiHandler {


	@Override
	public Kind getKind() {
		return Kind.METHOD_BASED;
	}

	


	public static boolean isMatching(Method method) {
		S8Getter methodAnnotation = method.getAnnotation(S8Getter.class);
		return methodAnnotation!=null;
	}


	protected Method setMethod;

	protected Method getMethod;
	
	
	public static MethodLiHandler initWithGetter(Method method) throws S8BuildException {
		MethodLiHandler handler = new MethodLiHandler();
		handler.attachGetMethod(method);
		return handler;
	}
	
	
	public static MethodLiHandler initWithSetter(Method method) throws S8BuildException {
		MethodLiHandler handler = new MethodLiHandler();
		handler.attachSetMethod(method);
		return handler;
	}
	

	
	/*
	public String getDescriptor(boolean isGetting) {
		if(isGetting && getMethod!=null) {
			return "getter: "+name+"@"+getMethod.getDeclaringClass();	
		}
		else if(!isGetting && setMethod!=null){
			return "setter: "+name+"@"+setMethod.getDeclaringClass();
		}
		else {
			return "method: "+name;
		}
	}
	*/

	@Override
	public void attachGetMethod(Method method) throws S8BuildException {
		if(getMethod!=null) {
			throw new S8BuildException("Cannot override method: "+method.getName());
		}
		
		Parameter[] parameters = method.getParameters();
		if(parameters.length>0) {
			throw new S8BuildException("Getter MUST NOT have parameters: "+method.getName());
		}
		this.getMethod = method;
	}


	
	@Override
	public void attachSetMethod(Method method) throws S8BuildException {
		if(setMethod!=null) {
			throw new S8BuildException("Cannot override method: "+method.getName());
		}
		Parameter[] parameters = method.getParameters();
		if(parameters.length!=1) {
			throw new S8BuildException("Setter MUST have only ONE parameter: "+method.getName());
		}
		
		this.setMethod = method;
	}



	@Override
	public void attachField(Field field) throws S8BuildException {
		throw new S8BuildException("failed to write double[] field");
	}
	

	@Override
	public String describe(boolean isGetting) {
		if(isGetting && getMethod!=null) {
			S8Getter methodAnnotation = getMethod.getAnnotation(S8Getter.class);
			Class<?> declaringType = getMethod.getDeclaringClass();
			S8ObjectType typeAnnotation = declaringType.getAnnotation(S8ObjectType.class);
			return "method: "+methodAnnotation.name()+" of type "+typeAnnotation.name()+" ("+getMethod+")";		
		}
		else if(!isGetting && setMethod!=null) {
			S8Setter methodAnnotation = setMethod.getAnnotation(S8Setter.class);
			Class<?> declaringType = setMethod.getDeclaringClass();
			S8ObjectType typeAnnotation = declaringType.getAnnotation(S8ObjectType.class);
			return "method: "+methodAnnotation.name()+" of type "+typeAnnotation.name()+" ("+setMethod+")";		
		}
		else {
			return "/!\\ undefined handler";
		}
	}
	
	
	
	
	@Override
	public void set(Object object, Object value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public Object get(Object object) throws S8IOException {
		try {
			return getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}


	
	@Override
	public void setBoolean(Object object, boolean value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}
	
	
	@Override
	public byte getByte(Object object) throws S8IOException {
		try {
			return (byte) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	

	@Override
	public void setByte(Object object, byte value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public boolean getBoolean(Object object) throws S8IOException {
		try {
			return (boolean) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	

	@Override
	public void setDouble(Object object, double value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public double getDouble(Object object) throws S8IOException {
		try {
			return (double) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}

	

	@Override
	public void setFloat(Object object, float value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public float getFloat(Object object) throws S8IOException {
		try {
			return (float) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	
	@Override
	public void setShort(Object object, short value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public short getShort(Object object) throws S8IOException {
		try {
			return (short) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	@Override
	public void setInteger(Object object, int value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public int getInteger(Object object) throws S8IOException {
		try {
			return (int) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	
	@Override
	public void setLong(Object object, long value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public long getLong(Object object) throws S8IOException {
		try {
			return (long) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
	
	@Override
	public void setString(Object object, String value) throws S8IOException {
		try {
			setMethod.invoke(object, new Object[]{ value });
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", setMethod, cause);
		}
	}


	@Override
	public String getString(Object object) throws S8IOException {
		try {
			return (String) getMethod.invoke(object, new Object[0]);
		} 
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException cause) {
			throw new S8IOException("failed to write double[] field", getMethod, cause);
		}
	}
	
}
