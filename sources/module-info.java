/**
 * 
 */
/**
 * @author pierreconvert
 *
 */
module com.s8.io.bohr.lithium {
	

	
	/* <lithium> */
	exports com.s8.io.bohr.lithium.codebase;
	exports com.s8.io.bohr.lithium.fields;
	exports com.s8.io.bohr.lithium.handlers;
	exports com.s8.io.bohr.lithium.object;
	exports com.s8.io.bohr.lithium.properties;
	exports com.s8.io.bohr.lithium.branches;
	exports com.s8.io.bohr.lithium.type;
	

	/* </lithium> */

	requires transitive com.s8.api;
	requires transitive com.s8.io.bohr.atom;
	requires transitive com.s8.io.bytes;
	
}