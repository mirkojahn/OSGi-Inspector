package net.mjahn.inspector.core.impl;

import java.util.Hashtable;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.VersionRange;
import org.osgi.framework.Constants;

public final class ImportedPackageImpl implements ImportedPackage {
	
	private final String packageName;
	private final VersionRange version;
	private final Object[] attributes;
	private final Object[] directives;
	private final boolean isOptional;
	
	public ImportedPackageImpl(Object[][] packageString){
		// first position is the package name
		packageName = (String) packageString[0][0];
		// second are directives
		directives = packageString[1];
		boolean optional = false;
		if(directives.length > 0){
			// check each attribute if it is a version. 
			
			for(int i=0;i<directives.length;i++){
				Attribute att = (Attribute)directives[i];
				if(att.getName().equalsIgnoreCase(Constants.RESOLUTION_DIRECTIVE)){
					optional = (((String)att.getValue()).equalsIgnoreCase(Constants.RESOLUTION_OPTIONAL)?true:false);
				}
			}
		}
		isOptional = optional;
		// third are attributes
		attributes = packageString[2];
		if(attributes.length > 0){
			// check each attribute if it is a version. 
			for(int i=0;i<attributes.length;i++){
				Attribute att = (Attribute)attributes[i];
				if(att.getName().equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)){
					version = new VersionRange((String)att.getValue());
					return;
				}
			}
		}
		// set the default version for OSGi package export if not specified yet.
		version = new VersionRange("0.0.0");
	}

	public String getPackageName() {
		return packageName;
	}

	public Hashtable<String, String> getProperties() {
		// FIXME: Auto-generated method stub
		return null;
	}

	public VersionRange getVersion() {
		return version;
	}

	public boolean isOptional() {
		return isOptional;
	}
	
	public boolean isCompatible(ExportedPackage exportedPackage){
		// check if the names are equal and if there is an intersection between the range and the Version
		if(exportedPackage.getName().equals(getPackageName()) && 
				version.isVersionInRange(exportedPackage.getVersion())){
			return true;
		}
		return false;
	}

}
