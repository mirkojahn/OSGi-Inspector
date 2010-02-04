package net.mjahn.inspector.core;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;

public interface TrackedBundle {

	Bundle getBundle();

	/**
	 * Obtain the services this bundle is listening for.
	 * 
	 * @return a list with the services this bundles is listening for or an empty array
	 */
	List<ListenerInfo> getAllAddedServicesListens();

	List<ListenerInfo> getAllRemovedServicesListens();

	public List<NotFoundServiceCall> getAllNotFoundServiceCalls();
	
	public List<ImportedPackage> getImportedPackages();
	
	public List<ExportedPackage> getExportedPackages();

}