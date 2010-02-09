package net.mjahn.inspector.core.impl;

import org.osgi.framework.BundleContext;

import net.mjahn.inspector.core.ListenerInfo;

public class ListenerInfoImpl implements ListenerInfo {
	
	private final org.osgi.framework.hooks.service.ListenerHook.ListenerInfo li;
	
	public ListenerInfoImpl(org.osgi.framework.hooks.service.ListenerHook.ListenerInfo info) {
		li = info;
	}

	public BundleContext getBundleContext() {
		return li.getBundleContext();
	}

	public String getFilter() {
		return li.getFilter();
	}

	public boolean isRemoved() {
		return li.isRemoved();
	}
	
	public boolean equals(Object obj) {
		return li.equals(obj);
	}
	
	public int hashCode() {
		return li.hashCode();
	}
	
	public String toString() {
		return li.toString();
	}

}
