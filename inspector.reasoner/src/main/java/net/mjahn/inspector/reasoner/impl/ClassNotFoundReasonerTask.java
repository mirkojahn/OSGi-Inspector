package net.mjahn.inspector.reasoner.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.Reasoner;
import net.mjahn.inspector.reasoner.ReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerTask;
import net.mjahn.inspector.reasoner.ReasonerTaskCapability;

import org.osgi.framework.Bundle;

import net.mjahn.inspector.core.ExportedPackage;

public class ClassNotFoundReasonerTask implements ReasonerTask {

	private static String ERROR_PREFIX = Reasoner.REASONER_ERROR_CODE_CORE_PREFIX
			+ Reasoner.REASONER_ERROR_CODE_CLASSNOTFOUND_PREFIX;

	public String getDiscription(Locale l) {
		return getDiscription();
	}

	public String getDiscription() {
		return "This Reasoner Task analyzes ClassNotFoundExceptions and tries to figure out where to find the class and what to import.";
	}

	public boolean requireBundle() {
		return true;
	}

	public List<ReasonerTaskCapability> requireDictionaryEntries() {
		ArrayList<ReasonerTaskCapability> list = new ArrayList<ReasonerTaskCapability>();
		list.add(new ReasonerTaskCapability(
				Reasoner.REASONER_ANALYZE_THROWABLE,
				"A ClassNotFoundException to analyze.", true));
		return list;
	}

	@SuppressWarnings("unchecked")
	public ReasonerResult analyze(TrackedBundle tb, Dictionary dict) {
		// FIXME: account for self import 
		if (dict.get(Reasoner.REASONER_ANALYZE_THROWABLE) != null
				&& (dict.get(Reasoner.REASONER_ANALYZE_THROWABLE) instanceof ClassNotFoundException)) {
			// ok, we can work on this one...
			ClassNotFoundException cnfe = (ClassNotFoundException) dict
					.get(Reasoner.REASONER_ANALYZE_THROWABLE);
			String classToLoad = cnfe.getMessage();
			TrackedBundle b;
			// first ensure to have a bundle to work with
			if (tb != null) {
				if (tb.getBundle() != null) {
					// everything as expected
					b = tb;
				} else {
					// we can't get a hold of the bundle. maybe it got
					// uninstalled. Stop here
					return new DefaultReasonerResult(
							0.1f,
							"The CNF Exception is associated to a bundle no longer available to the runtime. " +
							"Nothing we can do here.",
							ERROR_PREFIX + "00", cnfe, null);
				}
			} else {
				// this shouldn't happen, but let's try to guess the bundle and
				// then go on with it.
				StackTraceElement ste = getFirstNonDefaultJavaStackTraceElement(cnfe);
				if (ste == null) {
					// bad luck this case is not covered yet!
					return new DefaultReasonerResult(
							0.1f,
							"Couldn't determine what bundle initiated the CNFE - maybe it was one of " +
							"the excluded ones like org.junit? Nothing we can do here - for now. " +
							"You may look at the implementation though.",
							ERROR_PREFIX + "01", cnfe, null);
				}
				Bundle[] bundles = Activator.getFrameworkInspector()
						.getBundleForClassName(ste.getClassName());
				if (bundles == null) {
					return new DefaultReasonerResult(
							0.1f,
							"Couldn't determine where the CNFE was thrown. The class suspected to be " +
							"responsible could not be found in the runtime, so no Bundle could be associated.",
							ERROR_PREFIX + "02", cnfe, null);
				}
				int bId = 0;
				if (bundles.length != 1) {
					// length == 0 is NOT supported by the API, so omit it
					if (bundles[0].getBundleId() == 0) {
						// it is very unlikely the the system bundle loaded
						// it... try the next one
						bId = 1;
					}
				}
				b = Activator.getFrameworkInspector().getTrackedBundle(
						bundles[bId].getBundleId());
			}
			// here the actual analysis happens...
			// 1.) is the class in the bundle that tries to load it?
			try {
				b.getBundle().loadClass(classToLoad);
				// most likely you're using a different class loader...
				StackTraceElement ste = getFirstNonDefaultJavaStackTraceElement(cnfe);
				return new DefaultReasonerResult(
						0.7f,
						"The class you were trying to load is in the bundle that tries to load it, " +
						"which should be fine. Maybe you're using a different ClassLoader to do this? " +
						"Check out the Stacktrace and look for the class loading. " +
						"A good starting point might be: "
								+ ste.getClassName()
								+ " line "
								+ ste.getLineNumber(), ERROR_PREFIX + "03",
						cnfe, null);
			} catch (Throwable t) {
				// do nothing, just continue
			}
			// 2.) check if the bundle defines an import on the package it tries to load
			String packageName = classToLoad.substring(0, (classToLoad
					.lastIndexOf(".")));
			ImportedPackage impPkg = getMatchingImportedPackage(packageName,b.getAllImportedPackages());
			boolean matchesDynImport = matchesDynamicImportPackageStatements(packageName,b.getAllDynamicImportedPackages());
			if(impPkg == null && !matchesDynImport){
				// simple: no import defined for package in bundle
				return new DefaultReasonerResult(
						1f,
						"The class you were trying to load is not contained in the bundle nor is the package imported! " +
						"Add the import statement of \"" + packageName + "\" to bundle: "+b.getBundle().getSymbolicName()+"-"+b.getBundle().getVersion(), 
						ERROR_PREFIX + "04",
						cnfe, null);
			} else if(impPkg != null){
				// FIXME: search all "connected" bundles and match their export statements
				// for now... try to load the class in each deployed bundle return the successful attempts
				Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(classToLoad);
				if (bundles == null || bundles.length == 0) {
					// the package has to be exported, otherwise this bundle wouldn't resolve - must be a not existing API class
					// TODO: be more specific. We can track all exported packages to find the one matching.
					if(impPkg.isOptional()){
						return new DefaultReasonerResult(
								0.8f,
								"Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a" +
								"version problem with a not existing class in the provided version of package \""+packageName+"\". " +
								"It can also mean that this optional import is NOT wired by the OSGi container!!!",
								ERROR_PREFIX + "05", cnfe, null);
					}else{
						return new DefaultReasonerResult(
							1f,
							"Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a" +
							"version problem with a not existing class in the provided version of package \""+packageName+"\"",
							ERROR_PREFIX + "06", cnfe, null);
					}
				} 
			} else if(matchesDynImport){
				// most likely we have multiple providers in different versions were not all meet the requirements
				Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(classToLoad);
				ArrayList<ExportedPackage> matchingExpPackages = new ArrayList<ExportedPackage>();
				if (bundles.length != 1) {
					for (Bundle bundle : bundles) {
						TrackedBundle tbExp = Activator.getFrameworkInspector().getTrackedBundle(bundle.getBundleId());
						// check if this bundle defines a matching export statement
						Iterator<ExportedPackage> expIter = tbExp.getAllExportedPackages().iterator();
						while(expIter.hasNext()){
							ExportedPackage ep = expIter.next();
							if(ep.statisfiesImport(impPkg, false)){
								matchingExpPackages.add(ep);
							}
						}
					}
				}
				// this should never happen - the bundle wouldn't resolve and hence not throw the exception
				if(matchingExpPackages.size() == 0){
					// we couldn't find a matching export
					return new DefaultReasonerResult(
							1f,
							"Couldn't find a matching export statement in ANY bundle in the OSGi container. " +
							"Most likely you need to provide a matching version of package \""+packageName+"\".",
							ERROR_PREFIX + "07", cnfe, null);
				} else {
					return new DefaultReasonerResult(
							1f,
							"The package \""+packageName+"\" is exported by several bundles. Most likely your dynamic" +
							" import statement got bound to the wrong version.",
							ERROR_PREFIX + "08", cnfe, null);
				}
			} 
			// TODO: incorporate boot delegation
			// no idea what else could have gone wrong...
		}
		return new DefaultReasonerResult();
	}

	private StackTraceElement getFirstNonDefaultJavaStackTraceElement(
			Throwable t) {
		StackTraceElement[] ste = t.getStackTrace();
		if (ste != null && ste.length > 0) {
			for (StackTraceElement stackTraceElement : ste) {
				if (!stackTraceElement.getClassName().startsWith("java")
						&& !stackTraceElement.getClassName().startsWith("sun")
						&& !stackTraceElement.getClassName().startsWith("org.eclipse")
						&& !stackTraceElement.getClassName().startsWith("org.apache.felix")
						&& !stackTraceElement.getClassName().startsWith("org.knopflerfish")
						&& !stackTraceElement.getClassName().startsWith(
								"org.junit")) {
					return stackTraceElement;
				}
			}
		}
		return null;
	}
	
	private ImportedPackage getMatchingImportedPackage(String packageName, List<ImportedPackage> impPackages){
		Iterator<ImportedPackage> iterImp = impPackages.iterator();
		while(iterImp.hasNext()){
			ImportedPackage impPack = iterImp.next();
			if(packageName.equals(impPack.getPackageName())){
				// found it!
				return impPack;
			}
		}
		return null;
	}
	
	private boolean matchesDynamicImportPackageStatements(String packageName, List<ImportedPackage> impPackages){
		Iterator<ImportedPackage> iterImp = impPackages.iterator();
		while(iterImp.hasNext()){
			ImportedPackage impPack = iterImp.next();
			String impPackageName = impPack.getPackageName();
			if(impPackageName.endsWith("*")){
				// match substring
				if(impPackageName.length() == 1){
					// we import everything... (and do not account for property matching!!!)
					return true;
				}else{
					return packageName.startsWith(impPackageName.replace("*", ""));
				}
			} else {
				return (impPackageName.equals(packageName));
			}
		}
		return false;
	}
}
