package net.mjahn.inspector.core;

/**
 * The OSGiRuntime is a quick access to asses the features this particular runtime provides.
 * It not only analysis, what version it is, but also which services it provides and the properties
 * it was created with. This is a great place to look deeper, if the container actually fits your needs.
 * 
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface OSGiRuntime {
	
	// TODO: list provides OSGi services, Implementation specific services, the versions of the various services, the osgi version,...

	/**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	String toJSON();
}
