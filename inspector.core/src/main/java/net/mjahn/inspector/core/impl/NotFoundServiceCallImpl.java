package net.mjahn.inspector.core.impl;

import net.mjahn.inspector.core.NotFoundServiceCall;

import org.osgi.framework.Bundle;

public class NotFoundServiceCallImpl implements NotFoundServiceCall {
	private final long bundleId;
	private final String serviceName;
	private final String serviceFilter;
	private final boolean obtainAll;

	public NotFoundServiceCallImpl(Bundle bundle, String name, String filter, boolean all) {
		bundleId = bundle.getBundleId();
		serviceName = name;
		serviceFilter = filter;
		obtainAll = all;
	}
	
	public Bundle getBundle(){
		Bundle b = null;
		try{
			b = Activator.getContext().getBundle(bundleId);
		} catch (Exception e){
			return null;
		}
		return b;
	}
	
	public String getServiceFilter(){
		return serviceFilter;
	}
	
	public String getServiceName(){
		return serviceName;
	}
	
	public boolean isObtainAllServices(){
		return obtainAll;
	}
}
