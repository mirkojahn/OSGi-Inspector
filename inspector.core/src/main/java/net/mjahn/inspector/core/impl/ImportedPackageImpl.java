package net.mjahn.inspector.core.impl;

import java.util.Iterator;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;
import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.VersionRange;

import org.osgi.framework.Constants;

public final class ImportedPackageImpl extends AbstractPackage implements ImportedPackage {
	
	private final VersionRange version;
	private final boolean isOptional;
	
	
	public ImportedPackageImpl(Object[][] packageString, long bundle){
		super(packageString, bundle);
		// check if optional or not
		isOptional = Util.isOptional(directives);
		
		// set the default version for OSGi package export if not specified yet.
		version = new VersionRange(Util.getVersionString(attributes));
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
	
	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"importedPackage\": {");
		
		builder.append("\"packageName\":\"");
		if (packageName != null) {
			builder.append(packageName);
		}
		builder.append("\", ");
		
		builder.append(" \"versionRange\":\"");
		if (version != null) {
			builder.append(getVersion());
		}
		builder.append("\", ");
		
		builder.append("\"attributes\": [");
		if (attributes != null) {
			Iterator<Attribute> iter = attributes.iterator();
			while(iter.hasNext()){
				Attribute att = iter.next();
				if(att.getName().equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)){
					continue;
				}
				builder.append(att.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		builder.append("], ");
		
		builder.append("\"directives\": [");
		if (directives != null) {
			Iterator<Directive> iter = directives.iterator();
			while(iter.hasNext()){
				Directive dir =iter.next();
				builder.append(dir.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		builder.append("], ");
		
		builder.append("\"definingBundle\":\"");
		builder.append(bundleId);
		builder.append("\", ");
		builder.append("\"optional\":\"");
		builder.append(isOptional());
		builder.append("\"");
		
		builder.append("}}");
		return builder.toString();
	}
}
