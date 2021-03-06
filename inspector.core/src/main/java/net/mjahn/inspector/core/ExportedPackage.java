package net.mjahn.inspector.core;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * ExportPackage definition of a given Bundle with its properties as defined in the manifest file.
 * 
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface ExportedPackage {
	
	/**
	 * Obtain the name of this package.
	 * 
	 * @since 1.0
	 * @return the string name of the package
	 */
	String getPackageName();
	
	/**
	 * The OSGi version, under which this package is exposed to the OSGi container.
	 * 
	 * @since 1.0
	 * @return the OSGi version as defined in the manifest or "0.0.0" as the default assumed by OSGi.
	 */
	Version getVersion();
	
	/**
	 * The attributes as defined by this export statement. If no Version was defined, this list returns
	 * no version attribute, although you can obtain an actual version for this package - the assumed default
	 * version defined by the OSGi specification.
	 * 
	 * @since 1.0
	 * @see net.mjahn.inspector.core.Attribute
	 * @return the list of all defined attributes or an empty list if none are specified.
	 */
	List<Attribute> getAttributes();
	
	/**
	 * The directives as defined by this export statement.
	 * 
	 * @since 1.0
	 * @see net.mjahn.inspector.core.Directive
	 * @return the list of all defined attributes or an empty list if none are specified.
	 */
	List<Directive> getDirectives();
	
	/**
	 * Convenient method to get a specific directive by name.
	 * 
	 * @since 1.0
	 * @param name the name of the directive
	 * @return the directive with the given name or null
	 */
	Directive getDirective(String name);
	
	/**
	 * Get the bundle defining this export statement.
	 * 
	 * @since 1.0
	 * @return the bundle instance (loaded dynamically, so it can return null if un-installed)
	 */
	Bundle getDefiningBundle();
	
	/**
	 * Check if a given import request is satisfied by this export statement. It doesn't account
	 * for the actual wiring, only the properties specified.
	 * 
	 * @since 1.0
	 * @param impPackage the package that requires an export
	 * @param evaluateMandatoryAttributes true if a match for mandatory attributes should be evaluated too.
	 * @return true if this export satisfies the import request
	 */
	boolean statisfiesImport(ImportedPackage impPackage, boolean evaluateMandatoryAttributes);

	/**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	String toJSON();
}
