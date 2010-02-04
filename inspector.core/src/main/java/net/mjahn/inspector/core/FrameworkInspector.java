package net.mjahn.inspector.core;

import java.util.List;

/**
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 *
 * @version 1.0
 */
public interface FrameworkInspector {

	TrackedBundle getTrackedBundle(long id);
	
	List<? extends TrackedBundle> getAllTrackedBundles();
	
//	List<? extends TrackedBundle> getAllConflictingBundles();
	
	List<? extends ExportedPackage> getAllExportedPackages();
	
	List<? extends ExportedPackage> getAllExportedPackages(long id);
	
	List<? extends ImportedPackage> getAllImportedPackages();
	
	List<? extends ImportedPackage> getAllImportedPackages(long id);
	
	List<? extends ImportedPackage> getPotentialConflictingPackages();
}
