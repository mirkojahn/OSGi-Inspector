package net.mjahn.inspector.core.impl;

import java.util.Iterator;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;
import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class ExportedPackageImpl extends AbstractPackage implements ExportedPackage {
	private final Version version;
	
	public ExportedPackageImpl(Object[][] packageString, long bundle){
		super(packageString, bundle);
		// set the default version for OSGi package export if not specified yet.
		version = new Version(Util.getVersionString(attributes));
	}
	public Version getVersion() {
		return version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExportedPackage [");
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
			builder.append("{\"exportedPackage\": {");
			builder.append("\"packageName\":\"");
			if (packageName != null) {
				builder.append(packageName);
			}
			builder.append("\", ");
			
			builder.append(" \"version\":\"");
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
			builder.append("\"");
			
			builder.append("}}");
			return builder.toString();
		}

	public boolean statisfiesImport(ImportedPackage impPackage, boolean evaluateMandatoryAttributes) {
		String impPackageName = impPackage.getPackageName();
		if(impPackageName.endsWith("*")){
			// match substring
			if(impPackageName.length() != 1){
				return packageName.startsWith(impPackageName.replace("*", ""));
			}
		} else {
			return (impPackageName.equals(packageName));
		}
		// if we're still here, match required properties
		if(evaluateMandatoryAttributes && attributes.size() > 0){
			Iterator<Attribute> atts = attributes.iterator();
			while(atts.hasNext()){
				Attribute expAtt = atts.next();
				if(expAtt.isMandatory()){
					// we found a mandatory attribute!
					Attribute impAtt = impPackage.getAttribute(expAtt.getName());
					// either the import doesn't set this attribute or they do not match in value
					if(impAtt == null || !impAtt.getValue().equals(expAtt.getValue())){
						// not satisfied!
						return false;
					}
				}
			}
		}
		return false;
	}
}
