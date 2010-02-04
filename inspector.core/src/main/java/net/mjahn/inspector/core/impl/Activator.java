package net.mjahn.inspector.core.impl;

import java.util.Hashtable;

import net.mjahn.inspector.core.FrameworkInspector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;

public class Activator implements BundleActivator {
	
	private static BundleContext ctx;
	private CoreHooks hooks;
	private FrameworkInspectorImpl fwInspector;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting Bundle to inspect the OSGi Framework!");
		ctx = context;
		
		// create the entry object used for all further investigation
		fwInspector = new FrameworkInspectorImpl();
		hooks = new CoreHooks(fwInspector);
		ctx.registerService(new String[]{FindHook.class.getName(), ListenerHook.class.getName()}, hooks, new Hashtable<String,String>());
		
		// create listener for newly installed bundles
		
		// create a Tracker for Extensions with the Inspector instance
		// TODO: implement Extension mechanism
		
		// finally register the Inspector, so others can use it
		Hashtable<String,String> inspDict = new Hashtable<String,String>();
		inspDict.put("vendor", "net.mjahn");
		ctx.registerService(FrameworkInspector.class.getName(), fwInspector, inspDict);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// no need to clean-up yet
	}

	public static BundleContext getContext(){
		return ctx;
	}	
	
}
