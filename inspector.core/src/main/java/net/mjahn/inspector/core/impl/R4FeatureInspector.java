package net.mjahn.inspector.core.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;

public class R4FeatureInspector {
	BundleContext bContext;
	ServiceRegistration sreg;
	FrameworkInspectorImpl fwInspector;

	public R4FeatureInspector(BundleContext ctx, FrameworkInspectorImpl inspector) {
		bContext = ctx;
		fwInspector = inspector;
	}
	
	public void addHooks(){
		CoreHooks hooks = new CoreHooks(fwInspector);
		sreg = bContext.registerService(new String[]{FindHook.class.getName(), ListenerHook.class.getName()}, hooks, new Hashtable<String,String>());
	}
	
	public void removeHooks(){
		bContext.ungetService(sreg.getReference());
	}
}
