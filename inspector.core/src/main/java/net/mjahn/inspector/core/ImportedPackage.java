package net.mjahn.inspector.core;

import java.util.List;

import org.osgi.framework.Bundle;


/**
 * ImportPackage definition of a given Bundle with its properties as defined in the manifest file.
 *
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface ImportedPackage {
	
	/**
	 * Obtain the name of this package.
	 * 
	 * @since 1.0
	 * @return the string name of the package
	 */
	String getPackageName();
	
	/**
	 * The OSGi {@code VersionRange}, under which this package is exposed to the OSGi container.
	 * 
	 * @since 1.0
	 * @see VersionRange
	 * @return the OSGi VersionRange as defined in the manifest or "0.0.0" as the default assumed by OSGi.
	 */
	VersionRange getVersion();
	
	/**
	 * The attributes as defined by this import statement. If no version range was defined, this list returns
	 * no version attribute, although you can obtain an actual version range for this package - the assumed 
	 * default version defined by the OSGi specification ("0.0.0").
	 * 
	 * @since 1.0
	 * @see net.mjahn.inspector.core.Attribute
	 * @return the list of all defined attributes or an empty list if none are specified.
	 */
	public List<Attribute> getAttributes();
	
	/**
	 * The directives as defined by this import statement.
	 * 
	 * @since 1.0
	 * @see net.mjahn.inspector.core.Directive
	 * @return the list of all defined attributes or an empty list if none are specified.
	 */
	public List<Directive> getDirectives();
	
	/**
	 * Is this package marked as optional?
	 * 
	 * @since 1.0
	 * @return true if this package defines itself as optional.
	 */
	boolean isOptional();

	/**
	 * Get the bundle defining this import statement.
	 * 
	 * @since 1.0
	 * @return the bundle instance (loaded dynamically, so it can return null if uninstalled)
	 */
	public Bundle getDefiningBundle();
}
