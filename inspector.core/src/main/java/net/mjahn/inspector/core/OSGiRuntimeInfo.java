package net.mjahn.inspector.core;

import java.util.ArrayList;

import org.osgi.framework.Version;

/**
 * The OSGiRuntime is a quick access to asses the features this particular runtime provides.
 * It not only analysis, what version it is, but also which services it provides and the properties
 * it was created with. This is a great place to look deeper, if the container actually fits your needs.
 * 
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface OSGiRuntimeInfo {
	
	// TODO: list provides OSGi services, Implementation specific services, the versions of the various services, the osgi version,...

	/**
	 * Check all RuntimeStartProviders if the framework has been started.
	 * 
	 * @since 1.0
	 * @return true if it looks like the framework has been started.
	 */
	boolean hasBeenStarted(); 
	
	Version getOSGiRuntimeVersion();
	
	String getOSGiSpecificationVersion();
	
	boolean supportsFragmets();
	
	String getFrameworkVendor();
	
	String[] getSupportedExecutionEnvironments();
	
	String getFrameworkOsName();
	
	String getFrameworkOsVersion();
	
	boolean supportsFrameworkExtensions();
	
	boolean supportsBootClassPathExtension();
	
	boolean supportsRequireBundle();
	
	ArrayList<ExportedPackage> getBootDelegationPackages();
	
	ArrayList<ExportedPackage> getSystemPackages();
	
	/**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	String toJSON();
}
