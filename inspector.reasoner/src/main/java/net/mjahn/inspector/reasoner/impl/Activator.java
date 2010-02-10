package net.mjahn.inspector.reasoner.impl;

import net.mjahn.inspector.core.FrameworkInspector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	
	private static BundleContext ctx;
	private static FrameworkInspector fwInspector = null;
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ctx = context;
		
//		fwInspector = new FrameworkInspector();
		
		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
	}

	public static BundleContext getContext(){
		return ctx;
	}
	
	public static FrameworkInspector getFrameworkInspector(){
		return fwInspector;
	}
}
