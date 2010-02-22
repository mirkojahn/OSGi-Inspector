package net.mjahn.inspector.reasoner;

import java.util.Locale;

public interface ErrorCodeResolver {
	
	String resolve(String errorCode, Locale locale);
	
	String resolve(String errorCode);

}
