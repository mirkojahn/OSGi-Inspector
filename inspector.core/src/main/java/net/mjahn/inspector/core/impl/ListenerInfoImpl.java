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

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"listenerInfo\": {");
		// the filter
		builder.append("\"filter\":\"");
		if (li.getFilter() != null) {
			builder.append(li.getFilter());
		}
		builder.append("\", ");
		// is removed
		builder.append("\"removed\":\"");
		builder.append(li.isRemoved());
		builder.append("\" ");
		builder.append("}}");
		return builder.toString();
	}

}
