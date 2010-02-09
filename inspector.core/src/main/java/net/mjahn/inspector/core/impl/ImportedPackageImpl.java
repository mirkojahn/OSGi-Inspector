package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;
import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.VersionRange;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public final class ImportedPackageImpl implements ImportedPackage {
	
	private final String packageName;
	private final VersionRange version;
	private final boolean isOptional;
	private final ArrayList<Directive> directives;
	private final ArrayList<Attribute> attributes;
	private final long bundleId;
	
	public ImportedPackageImpl(Object[][] packageString, long bundle){
		bundleId = bundle;
		// first position is the package name
		packageName = (String) packageString[0][0];
		
		// second are directives
		directives = Util.parseDirectives(packageString[1]);
		// check if optional or not
		isOptional = Util.isOptional(directives);
		
		// third are attributes
		attributes = Util.parseAttributes(packageString[2]);
		// set the default version for OSGi package export if not specified yet.
		version = new VersionRange(Util.getVersionString(attributes));
	}

	public String getPackageName() {
		return packageName;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public List<Directive> getDirectives() {
		return directives;
	}

	public VersionRange getVersion() {
		return version;
	}

	public boolean isOptional() {
		return isOptional;
	}
	
	public boolean isCompatible(ExportedPackage exportedPackage){
		// check if the names are equal and if there is an intersection between the range and the Version
		if(exportedPackage.getPackageName().equals(getPackageName()) && 
				version.isVersionInRange(exportedPackage.getVersion())){
			return true;
		}
		return false;
	}

	public Bundle getDefiningBundle() {
		try{
			return Activator.getContext().getBundle(bundleId);
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportedPackage [");
		if (packageName != null) {
			builder.append("packageName=");
			builder.append(packageName);
			builder.append(", ");
		}
		if (version != null) {
			builder.append("version=");
			builder.append(getVersion());
			builder.append(", ");
		}
		if (attributes != null) {
			Iterator<Attribute> iter = attributes.iterator();
			while(iter.hasNext()){
				Attribute att = iter.next();
				if(att.getName().equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)){
					continue;
				}
				builder.append(att.toString());
				builder.append(", ");
			}
		}
		if (directives != null) {
			Iterator<Directive> iter = directives.iterator();
			while(iter.hasNext()){
				Directive dir =iter.next();
				builder.append(dir.toString());
				builder.append(", ");
			}
		}
		builder.append("bundleId=");
		builder.append(bundleId);
		
		builder.append("]");
		return builder.toString();
	}
	
}