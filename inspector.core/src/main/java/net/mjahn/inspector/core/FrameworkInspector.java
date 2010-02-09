package net.mjahn.inspector.core;

import java.util.List;

import org.osgi.framework.Bundle;

/**
 * This is the entry class to get access to all the analysis data gathered by this bundle.
 * 
 * <p>The class gets exposed as an OSGi service and can be obtained by its class name. Browse 
 * the various methods in order to get an understanding what you can do with this API.
 * This is your one-stop shop to analyze an OSGi runtime without the need to fidle around with 
 * the pain of other OSGi services.</p>
 * 
 * 
 * <b>This is a read-only API, do not attempt to change ANY data!</b>
 * 
 * 
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface FrameworkInspector {

	/**
	 * Get the TrackedBundle analysis data for a specific Bundle id. If you have the bundle
	 * instance at hand, you can obtain the id by calling <pre>someBundle.getId()</pre>.
	 * 
	 * @since 1.0
	 * @param id the bundle id.
	 * @return the corresponding TrackedBundle instance reference with analysis data or null if bundle didn't exist or was uninstalled!.
	 */
	TrackedBundle getTrackedBundle(long id);
	
	/**
	 * Obtain all bundles tracked an analyzed. What you actually get is a reference to the list,
	 * not a copy, so this list might change while you work on it. No manipulations on the list
	 * are allowed - you've been warned! Also, if a bundle was uninstalled null is returned!
	 * 
	 * @since 1.0
	 * @return the list of all bundles or an empty list. If a bundle was uninstalled null is returned!
	 */
	List<? extends TrackedBundle> getAllTrackedBundles();
	
	/**
	 * Obtain an OSGiRuntime object, containing information of the OSGi container, like the version,
	 * supported features and a like.
	 * 
	 * @since 1.0
	 * @return analysis object of the OSGi Runtime.
	 */
	OSGiRuntime getRuntime();
	
	/**
	 * Obtain in one call all packages available in the runtime. This will cause to parse once all
	 * tracked bundle manifest headers. The second call will only iterate over the already created
	 * list (and parse the bundles newly added).
	 * 
	 * @since 1.0
	 * @return a list of all Packages exported in the runtime
	 */
	List<ExportedPackage>getAllExportedPackages();
	
	/**
	 * Obtain in one call all packages requested in the runtime. This will cause to parse once all
	 * tracked bundle manifest headers. The second call will only iterate over the already created
	 * list (and parse the bundles newly added). This does NOT analyze what packages are actually 
	 * used through wiring!
	 * 
	 * @since 1.0
	 * @return a list of all Packages defined as imported by all tracked bundles in the runtime.
	 */
	List<ImportedPackage>getAllImportedPackages();
	
	/**
     * For a given full qualified class name, all possible bundles, providing this class will be
     * returned.
     * 
     * @param fqcn like java.lang.Object
     * @return an array of Bundles, defining this class or null if non of the bundle class loaders
     *         or the system/ boot class loader from the system bundle are able to load the class.
     * @version 1.0
     */
    public Bundle[] getBundleForClassName(final String fqcn);

    /**
     * If you need to know, which Bundle actually defined a certain class, this is the method to
     * use.
     * 
     * @param clazz the class to inspect
     * @return the bundle, which had the class loader defining this class
     * @version 1.0
     */
    public Bundle getBundleForClass(final Class<?> clazz);
//	List<? extends TrackedBundle> getAllConflictingBundles();
	
// TODO: move this method to the Reasoner
//	/**
//	 * 
//	 * @since 1.0
//	 * @return a list of PotentialImportConflicts or an empty list if none.
//	 */
//	List<? extends ImportedPackage> getPotentialConflictingPackages();
}
