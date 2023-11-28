/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.core.bohr.lithium {
	

	
	/* <lithium> */
	exports com.s8.core.bohr.lithium.codebase;
	exports com.s8.core.bohr.lithium.fields;
	exports com.s8.core.bohr.lithium.handlers;
	exports com.s8.core.bohr.lithium.object;
	exports com.s8.core.bohr.lithium.properties;
	exports com.s8.core.bohr.lithium.branches;
	exports com.s8.core.bohr.lithium.type;
	

	/* </lithium> */

	requires transitive com.s8.api;
	requires transitive com.s8.core.bohr.atom;
	requires transitive com.s8.core.io.bytes;
	
}