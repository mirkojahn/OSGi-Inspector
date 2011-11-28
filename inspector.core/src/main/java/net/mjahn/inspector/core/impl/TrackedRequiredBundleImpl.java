package net.mjahn.inspector.core.impl;

import java.util.ArrayList;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;
import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.core.TrackedRequiredBundle;

public class TrackedRequiredBundleImpl implements TrackedRequiredBundle {

	final String bundleSymbolicName;
	final long requiringBundle;
	final ArrayList<Directive> directives;
	final ArrayList<Attribute> attributes;
	
	public TrackedRequiredBundleImpl(Object[][] bundleString, long requiringBundle){
		// first position is the BundleSymbolicName
		bundleSymbolicName = (String) bundleString[0][0];
		// the bundle that defines this bundle as required
		this.requiringBundle = requiringBundle;
		// second are directives
		directives = Util.parseDirectives(bundleString[1]);
		
		// third are attributes
		attributes = Util.parseAttributes(bundleString[2]);
	}
	
	@Override
	public TrackedBundle getBundle() {
		// FIXME: obtain the tracked Bundle
		return null;
	}

}
