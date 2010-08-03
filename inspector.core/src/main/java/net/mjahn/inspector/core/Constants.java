package net.mjahn.inspector.core;

public interface Constants {
	
	String INSPECTOR_CORE_PROPERTY_PREFIX = "net.mjahn.inspector.core.";
	
	
	String INITIAL_BUNDLE_AMOUNT_PROPERTY = INSPECTOR_CORE_PROPERTY_PREFIX+"initial.bundle.amount";
	int INITIAL_BUNDLE_AMOUNT_DEFAULT_VALUE = 500;
	
	String FRAMEWORK_EVENT_COUNT_PROPERTY = INSPECTOR_CORE_PROPERTY_PREFIX+"framework.event.count";
	int FRAMEWORK_EVENT_COUNT_DEFAULT_VALUE = 5000;
}
