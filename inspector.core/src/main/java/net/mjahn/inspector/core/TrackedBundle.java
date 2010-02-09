package net.mjahn.inspector.core;

import java.util.List;

import org.osgi.framework.Bundle;

/**
 * This interface hold analysis data for a specific Bundle.
 *
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * @version 1.0
 */
public interface TrackedBundle {

	/**
	 * Obtain the actual Bundle instance.
	 * 
	 * @since 1.0
	 * @return the original Bundle instance
	 */
	Bundle getBundle();

	/**
	 * <p>Obtain the services this bundle is listening for.</p>
	 * 
	 * <b>This feature requires an OSGi R4.2 container!</b>
	 * 
	 * @since 1.0
	 * @return a list with the services this bundles is listening for or an empty array
	 */
	List<ListenerInfo> getAllAddedServicesListens();

	/**
	 * <p>Obtain the ServiceListeners removed by this bundle.</p>
	 * 
	 * <b>This feature requires an OSGi R4.2 container!</b>
	 * 
	 * @since 1.0
	 * @return the list of all ServiceListneres removed by this bundle
	 */
	List<ListenerInfo> getAllRemovedServicesListens();

	/**
	 * <p>Obtain service calls that try to obtain a concrete service but failed to do so. This
	 * is usually a very good indicator that some functionality expected to work are in fact 
	 * not. This is a good starting point for investigating problems you experience within
	 * your application.</p>
	 * 
	 * <b>This feature requires an OSGi R4.2 container!</b>
	 * 
	 * @since 1.0
	 * @return services requested, but not obtained by this bundle or an empty list (never null).
	 */
	public List<NotFoundServiceCall> getAllNotFoundServiceCalls();
	
	/**
	 * <p>Obtain the list of all packages this bundle requests to import. This not the actual 
	 * import done by the container, but the definition as found in the manifest file. Gathering
	 * of this is done once lazy up-on the first request.</p>
	 * 
	 * @since 1.0
	 * @return a list with the packages imported by this Bundle as defined in the manifest file or an empty list (never null).
	 */
	public List<ImportedPackage> getImportedPackages();
	
	/**
	 * <p>Obtain the list of all packages this bundle provides as an export. This not the actual 
	 * export consumed by another bundle in the container, but the definition as found in the 
	 * manifest file. Gathering of this is done once lazy up-on the first request.</p>
	 * 
	 * @since 1.0
	 * @return a list with the packages exported by this Bundle as defined in the manifest file  or an empty list (never null).
	 */
	public List<ExportedPackage> getExportedPackages();
	
	/**
	 * Checks if this bundle is a fragment bundle. The behavior is different to {@code getHostBundles()}, because this method
	 * checks the bundle manifest and NOT the OSGi runtime to get its information.
	 * 
	 * @since 1.0
	 * @return true if this is a fragment bundle.
	 */
	public boolean isFragment();
	
	/**
	 * Obtain the tracked host bundles for this fragment, if in fact it is a fragment. For frameworks minor R4.2, this method
	 * can only return one bundle even if there are more bundles defined, because this data is obtained from the container and
	 * older versions of OSGi only support attaching a fragment to one host. Also if it is not attached yet, it will not return
	 * a host bundle, even if there is one (or more) defined. 
	 * @since 1.0
	 * @return the list of host bundles or an empty list if it is not a fragment bundle.
	 */
	List<TrackedBundle> getHostBundles(); 

	/**
	 * Does this bundle has a dynamic import?
	 * 
	 * @since 1.0
	 * @return true if it has a dynamic import statement.
	 */
	boolean hasDynamicImport();
	
	/**
	 * Obtain the dynamically imported packages if any.
	 * 
	 * @since 1.0
	 * @return the list of dynamically imported packages or an empty list if none.
	 */
	List<ImportedPackage> getDynamicImportedPackage();
	
	/**
	 * Obtain the fragments attached to this as host bundle.
	 * 
	 * @since 1.0
	 * @return fragments attached, if any or an empty list.
	 */
	List<TrackedBundle> getAttachedFragmentBundles();
	
}