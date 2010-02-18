package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;
import net.mjahn.inspector.core.ExportedPackage;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class ExportedPackageImpl implements ExportedPackage {
	private final String packageName;
	private final Version version;
	private final ArrayList<Directive> directives;
	private final ArrayList<Attribute> attributes;
	private final long bundleId;
	
	public ExportedPackageImpl(Object[][] packageString, long bundle){
		bundleId = bundle;
		// first position is the package name
		packageName = (String) packageString[0][0];
		
		// second are directives
		directives = Util.parseDirectives(packageString[1]);
		
		// third are attributes
		attributes = Util.parseAttributes(packageString[2]);
		// set the default version for OSGi package export if not specified yet.
		version = new Version(Util.getVersionString(attributes));
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public List<Directive> getDirectives() {
		return directives;
	}

	public String getPackageName() {
		return packageName;
	}

	public Version getVersion() {
		return version;
	}

	public Bundle getDefiningBundle() {
		try{
			return Activator.getContext().getBundle(bundleId);
		} catch(Exception e){
			return null;
		}
	}

//	public String toString(){
//		StringBuffer sb = new StringBuffer();
//		sb.append(packageName);
//		sb.append(getVersion());
//		if(!directives.isEmpty()){
//			Iterator<Directive> direcIter = directives.iterator();
//			while(direcIter.hasNext()){
//				sb.append(","+direcIter.next().toString());
//			}
//		}
//		if(!attributes.isEmpty()){
//			Iterator<Attribute> attrIter = attributes.iterator();
//			while(attrIter.hasNext()){
//				sb.append(","+attrIter.next().toString());
//			}
//		}
//		sb.append(" by bundle ["+bundleId+"]");
//		return sb.toString();
//	}
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
}
