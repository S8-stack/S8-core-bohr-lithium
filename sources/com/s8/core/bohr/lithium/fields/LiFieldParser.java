package com.s8.core.bohr.lithium.fields;

import java.io.IOException;

import com.s8.api.bytes.ByteInflow;
import com.s8.core.bohr.atom.protocol.BOHR_Properties;


/**
 * 
 *
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class LiFieldParser {
	
	
	public abstract LiField getField();
	
	
	/**
	 * 
	 * @param map
	 * @param object
	 * @param inflow
	 * @param bindings
	 * @throws BkException
	 */
	public abstract LiFieldDelta parseValue(ByteInflow inflow) throws IOException;
	

	
	/**
	 * 
	 * @param props
	 * @return
	 */
	public static boolean isNonNull(int props) {
		return (props & BOHR_Properties.IS_NON_NULL_PROPERTIES_BIT) == BOHR_Properties.IS_NON_NULL_PROPERTIES_BIT;
	}
	
	
}
