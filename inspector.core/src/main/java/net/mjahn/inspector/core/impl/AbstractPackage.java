package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;

import org.osgi.framework.Bundle;

public abstract class AbstractPackage {
	final String packageName;
	final ArrayList<Directive> directives;
	final ArrayList<Attribute> attributes;
	final long bundleId;
	
	public AbstractPackage(Object[][] packageString, long bundle){
		bundleId = bundle;
		// first position is the package name
		packageName = (String) packageString[0][0];
		
		// second are directives
		directives = Util.parseDirectives(packageString[1]);
		
		// third are attributes
		attributes = Util.parseAttributes(packageString[2]);
	}
	public String getPackageName() {
		return packageName;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public Attribute getAttribute(String name){
		Iterator<Attribute>atts = attributes.iterator();
		while(atts.hasNext()){
			Attribute a = atts.next();
			if(a.getName().equals(name)){
				return a;
			}
		}
		return null;
	}
	
	public List<Directive> getDirectives() {
		return directives;
	}
	
	public Directive getDirective(String name){
		Iterator<Directive>dirIter = directives.iterator();
		while(dirIter.hasNext()){
			Directive d = dirIter.next();
			if(d.getName().equals(name)){
				return d;
			}
		}
		return null;
	}
	
	public Bundle getDefiningBundle() {
		try{
			return Activator.getContext().getBundle(bundleId);
		} catch(Exception e){
			return null;
		}
	}

}
