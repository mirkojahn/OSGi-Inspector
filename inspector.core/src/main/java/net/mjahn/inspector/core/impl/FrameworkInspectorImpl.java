package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.OSGiRuntime;
import net.mjahn.inspector.core.TrackedBundle;
import static net.mjahn.inspector.core.Constants.INITIAL_BUNDLE_AMOUNT_PROPERTY;
import static net.mjahn.inspector.core.Constants.INITIAL_BUNDLE_AMOUNT_DEFAULT_VALUE;
import static net.mjahn.inspector.core.Constants.FRAMEWORK_EVENT_COUNT_PROPERTY;
import static net.mjahn.inspector.core.Constants.FRAMEWORK_EVENT_COUNT_DEFAULT_VALUE;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.packageadmin.PackageAdmin;

public class FrameworkInspectorImpl implements FrameworkInspector {
	
	private ArrayList<TrackedBundleImpl> trackedBundles;
	private LinkedList<FrameworkEvent> fwEvents;
	private LinkedList<FrameworkEvent> fwErrorEvents;
	private int allowedFwEvents;
	
	public FrameworkInspectorImpl() {
		String initialBundleCount = System.getProperty(INITIAL_BUNDLE_AMOUNT_PROPERTY);
		if(initialBundleCount != null && !initialBundleCount.equals("")){
			try{
				int count = Integer.parseInt(initialBundleCount);
				if(count > 0){
					trackedBundles = new ArrayList<TrackedBundleImpl>(count);
				}else {
					trackedBundles = new ArrayList<TrackedBundleImpl>(INITIAL_BUNDLE_AMOUNT_DEFAULT_VALUE);
				}
			} catch (Exception e) {
				trackedBundles = new ArrayList<TrackedBundleImpl>(INITIAL_BUNDLE_AMOUNT_DEFAULT_VALUE);
			}
		} else {
			trackedBundles = new ArrayList<TrackedBundleImpl>(INITIAL_BUNDLE_AMOUNT_DEFAULT_VALUE);
		}
		
		String initialEventCount = System.getProperty(FRAMEWORK_EVENT_COUNT_PROPERTY);
		if(initialEventCount != null && !initialEventCount.equals("")){
			try{
				int count = Integer.parseInt(initialEventCount);
				if(count > 0){
					allowedFwEvents = count;
				}else {
					allowedFwEvents = FRAMEWORK_EVENT_COUNT_DEFAULT_VALUE;
				}
			} catch (Exception e) {
				allowedFwEvents = FRAMEWORK_EVENT_COUNT_DEFAULT_VALUE;
			}
		} else {
			allowedFwEvents = FRAMEWORK_EVENT_COUNT_DEFAULT_VALUE;
		}
		
		fwErrorEvents = new LinkedList<FrameworkEvent>();
		fwEvents = new LinkedList<FrameworkEvent>();
	}

	
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
		TrackedBundleImpl tb;
		try{
			tb = trackedBundles.get((int)id);
		} catch (IndexOutOfBoundsException e){
			// bundle not yet tracked
			tb = new TrackedBundleImpl(id);
//			System.out.println("###created new tracke bundle: "+ tb.getBundle().getSymbolicName());
			Bundle b = tb.getBundle();
			if(b == null){
				// only if the bundle doesn't exist
				return null;
			}
			trackedBundles.add((int)id,tb);
		}
//		System.out.println("###gettingTrackedBundle"+tb.getBundle().getSymbolicName());
		
		return tb;
	}

	public OSGiRuntime getRuntime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	void remove(long id){
		trackedBundles.set((int)id, null);
	}
	
	void update(long id){
		trackedBundles.remove(id);
		// add the new one (has the same id)
		getTrackedBundle(id);
	}
	
	public List<ExportedPackage>getAllExportedPackages(){
		ArrayList<ExportedPackage> expPack = new ArrayList<ExportedPackage>();
		Iterator<TrackedBundleImpl> tbsIter = getAllTrackedBundleImpls().iterator();
		while(tbsIter.hasNext()){
			TrackedBundleImpl tb = tbsIter.next();
			if(tb != null){
				expPack.addAll(tb.getExportedPackages());
			}
		}
		return expPack;
	}
	
	public List<ImportedPackage>getAllImportedPackages(){
		ArrayList<ImportedPackage> impPack = new ArrayList<ImportedPackage>();
		Iterator<TrackedBundleImpl> tbsIter = getAllTrackedBundleImpls().iterator();
		while(tbsIter.hasNext()){
			TrackedBundleImpl tb = tbsIter.next();
			if(tb != null){
				impPack.addAll(tb.getImportedPackages());
			}
		}
		return impPack;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FrameworkInspectorImpl [\n");
		if (trackedBundles != null) {
			Iterator<TrackedBundleImpl>iter = trackedBundles.iterator();
			while(iter.hasNext()){
				TrackedBundleImpl bundle = iter.next();
				builder.append(bundle.toString());
				builder.append(", \n");
			}
		}
		builder.append(" cross cutting methods are not caluculated here!]");
		return builder.toString();
	}
	
	/**
     * FIXME: add comment
     * 
     * @version 1.0
     */
    public Bundle[] getBundleForClassName(final String fqcn) {
        Class<?>[] classes = getClassesForName(fqcn);
        Bundle[] bundles = new Bundle[0];
        if (classes != null
            && classes.length > 0) {
            bundles = new Bundle[classes.length];
            for (int i = 0; i < classes.length; i++) {
                bundles[i] = getBundleForClass(classes[i]);
            }
        }
        return bundles;
    }

    /**
     * TODO: Add comment
     * 
     * @see net.mjahn.tools.inspector.IBundleUtils#getBundleForClass(java.lang.Class)
     * @version 1.0
     * @since 1.0
     */
    public Bundle getBundleForClass(final Class<?> clazz) {
        PackageAdmin admin = Activator.getPackageAdmin();
        if (admin != null) {
        	Bundle b = admin.getBundle(clazz);
        	if(b == null){
        		// must be the system bundle
        		return Activator.getContext().getBundle(0);
        	} else {
        		return b;
        	}
        }
        return null;        
    }

    /**
     * get all possible class instance available in the OSGi container for a distinct full qualified
     * class name.
     * 
     * @param clazzName full qualified class name like java.lang.Object
     * @return guarantied to be not null (but might be empty though)
     * @version 1.0
     * @since 1.0
     */
    public Class<?>[] getClassesForName(final String clazzName) {
        Bundle[] bundles = Activator.getContext().getBundles();
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        for (int i = 0; i < bundles.length; i++) {
            // check if you can successfully load the class
            try {
                classes.add(bundles[i].loadClass(clazzName));
                // add the class!!! (not the bundle) to the list of possible
                // providers (bundle can delegate class loading to other bundles
                // so only the Class - FQCN plus CL - is unique, thus use the
                // HashSet to filter multiple providers of the same Class.
            } catch (ClassNotFoundException e) {
                // indicates no class available in this bundle -> do nothing!
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    // TODO: think about a more lightweight fw Event
	
	synchronized void addFrameworkEvent(FrameworkEvent event){
		if(fwEvents.size()>allowedFwEvents){
			FrameworkEvent ev = fwEvents.getFirst();
			fwEvents.remove(0);
			fwErrorEvents.remove(ev);
		}
		fwEvents.add(event);
	}
	
	public List<FrameworkEvent> getErrorFrameworkEvents() {
		return fwErrorEvents;
	}

	public FrameworkEvent getFrameworkEvent() {
		return fwEvents.getLast();
	}

	public List<FrameworkEvent> getFrameworkEvents() {
		return fwEvents;
	}

	public List<FrameworkEvent> getFrameworkEvents(long id) {
		ArrayList<FrameworkEvent> list = new ArrayList<FrameworkEvent>(100);
		Iterator<FrameworkEvent> iter = fwEvents.iterator();
		while(iter.hasNext()){
			FrameworkEvent event = iter.next();
			try {
				if(event.getBundle().getBundleId() == id){
					list.add(event);
				}
			}catch (Exception e) {
				// do nothing... in OSGi anything can happen
			}
		}
		return list;
	}
    
}
