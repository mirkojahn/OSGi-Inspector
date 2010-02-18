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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NotFoundServiceCall [bundleId=");
		builder.append(bundleId);
		builder.append(", obtainAll=");
		builder.append(obtainAll);
		builder.append(", ");
		if (serviceFilter != null) {
			builder.append("serviceFilter=");
			builder.append(serviceFilter);
			builder.append(", ");
		}
		if (serviceName != null) {
			builder.append("serviceName=");
			builder.append(serviceName);
		}
		builder.append("]");
		return builder.toString();
	}
	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"notFoundServiceCall\": {");
		// the bundle
		builder.append("\"bundle\":\"");
		if (getBundle() != null) {
			builder.append(getBundle());
		}
		builder.append("\", ");
		
		// the service filter
		builder.append("\"serviceFilter\":\"");
		if (getServiceFilter() != null) {
			builder.append(getServiceFilter());
		}
		builder.append("\", ");
		
		// the service name
		builder.append("\"serviceName\":\"");
		if (getServiceName() != null) {
			builder.append(getServiceName());
		}
		builder.append("\", ");
		
		// is obtain all
		builder.append("\"obtainAllServices\":\"");
		builder.append(isObtainAllServices());
		builder.append("\" ");
		
		builder.append("}}");
		return builder.toString();
	}
	
}
