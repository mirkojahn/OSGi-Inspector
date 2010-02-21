package net.mjahn.inspector.core.impl;

import net.mjahn.inspector.core.OSGiRuntime;

public class OSGiRuntimeImpl implements OSGiRuntime {

	public String toJSON() {
		// empty - for now!
		// FIXME: implement this meaningful
		return "{ \"osgiRuntime\":\"\"}";
	}

	public boolean hasBeenStarted() {
		// TODO check for the Framework started event... and all FrameworkStartProviders
		return false;
	}

}
