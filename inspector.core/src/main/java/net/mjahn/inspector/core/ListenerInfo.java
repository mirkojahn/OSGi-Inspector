package net.mjahn.inspector.core;

import org.osgi.framework.BundleContext;

/**
 * Wrapper class for the {@code
 * org.osgi.framework.hooks.service.ListenerHook.ListenerInfo} class, to not
 * depend on it, if run in an older OSGi container.
 * 
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface ListenerInfo {

	/**
	 * @see org.osgi.framework.hooks.service.ListenerHook.ListenerInfo#getBundleContext()
	 * @since 1.0
	 * @return the bundleContext of the requesting bundle
	 */
	BundleContext getBundleContext();

	/**
	 * @see org.osgi.framework.hooks.service.ListenerHook.ListenerInfo#getFilter()
	 * @since 1.0
	 * @return the filter string
	 */
	String getFilter();

	/**
	 * @see org.osgi.framework.hooks.service.ListenerHook.ListenerInfo#isRemoved()
	 * @since 1.0
	 * @return true if removed
	 */
	boolean isRemoved();

}
