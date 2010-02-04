package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.List;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.TrackedBundle;

import org.osgi.framework.Bundle;

public class FrameworkInspectorImpl implements FrameworkInspector {

	private ArrayList<TrackedBundleImpl> trackedBundles = new ArrayList<TrackedBundleImpl>();
	
	public List<? extends TrackedBundle> getAllTrackedBundles() {
		return getAllTrackedBundleImpls();
	}

	public List<TrackedBundleImpl> getAllTrackedBundleImpls(){
		return trackedBundles;
	}
	
	/**
	 * Obtain the tracked bundle, if it exists (or existed).
	 * 
	 * @param id the bundle ID of the tracked bundle
	 * @return the tracked bundle, if the bundle doesn't exist null will be returned
	 */
	public TrackedBundle getTrackedBundle(long id){
		return getTrackedBundleImpl(id);
	}
	
	public TrackedBundleImpl getTrackedBundleImpl(long id){
		TrackedBundleImpl tb = trackedBundles.get((int)id);
		if(tb == null){
			// bundle not yet tracked
			tb = new TrackedBundleImpl(id);
			Bundle b = tb.getBundle();
			if(b == null){
				// only if the bundle doesn't exist
				return null;
			}
			trackedBundles.add(tb);
		}
		return tb;
	}

	public List<? extends ExportedPackage> getAllExportedPackages() {
		ArrayList<ExportedPackage> packages;
		// be sure no duplicates are in this list!!!
		getAllTrackedBundleImpls();
		return null;
	}

	public List<? extends ExportedPackage> getAllExportedPackages(long id) {
		return getTrackedBundleImpl(id).getExportedPackages();
	}

	public List<? extends ImportedPackage> getAllImportedPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends ImportedPackage> getAllImportedPackages(long id) {
		return getTrackedBundleImpl(id).getImportedPackages();
	}

	public List<? extends ImportedPackage> getPotentialConflictingPackages() {
		// FIXME: Auto-generated method stub
		return null;
	}
}
