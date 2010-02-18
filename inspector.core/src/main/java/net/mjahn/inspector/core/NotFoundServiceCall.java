package net.mjahn.inspector.core;

import org.osgi.framework.Bundle;

/**
 * 
 * This interface denotes a not found OSGi service request. This is not a listening request for
 * an appearing service, but an actual request to obtain a serviceReference that is tracked here.
 * So every instance of this actually means something didn't work as expected or indicates a
 * poorly written implementation of OSGi usage, which is always good to avoid.
 *
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface NotFoundServiceCall {

	/**
	 * The bundle performing the failed service request.
	 * 
	 * @since 1.0
	 * @return the bundle instance of the failed service request.
	 */
	Bundle getBundle();

	/**
	 * Obtain the Filter used to define the service in question, if specified by the caller.
	 * 
	 * @since 1.0
	 * @return the filter used to obtain the service in question or null if not specified.
	 */
	String getServiceFilter();

	/**
	 * The class name of the services to find or null to find all services.
	 * 
	 * @since 1.0
	 * @return the name of the requested service, if specified by the caller.
	 */
	String getServiceName();

	/**
	 * Did the caller try to obtain all services that match these specific criteria? If it did,
	 * this indicates, that the caller didn't care about compatibility of the class space and 
	 * might anticipate that it wouldn't get any service matching at all, but this is just a 
	 * lucky guess. It's still worth investigating, f.i. if this is done without the proper
	 * knowledge to what this call really means!
	 * 
	 * @since 1.0
	 * @return true if all services were requested, if the once not compatible with this bundles
	 * 		class space.
	 */
	boolean isObtainAllServices();

	/**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	String toJSON();
}