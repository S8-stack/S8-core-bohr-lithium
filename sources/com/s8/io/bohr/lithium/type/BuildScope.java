package com.s8.io.bohr.lithium.type;

import java.util.ArrayList;
import java.util.List;

import com.s8.api.exceptions.S8IOException;
import com.s8.api.objects.space.SpaceS8Object;


/**
 * 
 * @author Pierre Convert
 * Copyright (C) 2022, Pierre Convert. All rights reserved.
 * 
 */
public abstract class BuildScope {
	

	
	/**
	 * 
	 */
	List<Binding> bindings = new ArrayList<>();


	/**
	 * 
	 * @param binding
	 */
	public void appendBinding(Binding binding) {
		bindings.add(binding);
	}
	
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract SpaceS8Object retrieveObject(String index);
	
	/**
	 * <p><b>ALWAYS RESOLVE AFTER DESERIALIZATION</b></p>
	 * 
	 * @throws LthSerialException
	 */
	public void process() throws S8IOException {

		// screen all bindings
		for(Binding binding : bindings) {

			// resolve
			binding.resolve(this);
		}

		// bindings have now all been consumed, so clear
		bindings.clear();
	}


	
	/**
	 * 
	 * @author pc
	 *
	 */
	public interface Binding {


		/**
		 * Attempt to resolve a binding.
		 * @return false if the binding has been successfully resolved (nothing else to do)
		 * @throws LthSerialException
		 */
		public abstract void resolve(BuildScope scope) throws S8IOException;

	}
	
}
