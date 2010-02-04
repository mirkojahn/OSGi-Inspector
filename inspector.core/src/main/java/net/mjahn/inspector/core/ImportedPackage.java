package net.mjahn.inspector.core;

import java.util.Hashtable;

public interface ImportedPackage {
	
	String getPackageName();
	
	VersionRange getVersion();
	
	Hashtable<String,String> getProperties();
	
	boolean isOptional();

}
