package net.mjahn.inspector.core;

import org.osgi.framework.Bundle;

public interface NotFoundServiceCall {

	Bundle getBundle();

	String getServiceFilter();

	String getServiceName();

	boolean isObtainAllServices();

}