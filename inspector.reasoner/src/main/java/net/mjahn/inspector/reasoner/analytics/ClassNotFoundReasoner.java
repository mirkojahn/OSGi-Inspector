package net.mjahn.inspector.reasoner.analytics;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.BaseErrorCodes;
import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.ReasoningEngine;
import net.mjahn.inspector.core.reasoner.base.NoClueReasonerResult;
import net.mjahn.inspector.core.reasoner.base.ReasonerBase;
import net.mjahn.inspector.core.reasoner.base.SimpleReasonerResult;
import net.mjahn.inspector.core.reasoner.base.Utils;
import net.mjahn.inspector.reasoner.impl.Activator;
import org.osgi.framework.Bundle;

/**
 *
 * @author mjahn
 */
public class ClassNotFoundReasoner extends ReasonerBase {

    private static String ERROR_PREFIX = BaseErrorCodes.REASONER_ERROR_CODE_CORE_PREFIX
			+ BaseErrorCodes.REASONER_ERROR_CODE_CLASSNOTFOUND_PREFIX;
    public ClassNotFoundReasoner() {
        super("ClassNotFound Reasoner");
    }

    public ReasonerResult analyze(JobDescription desc, ReasoningEngine engine, ReasonerResultCompiler resultCompiler) {
        // FIXME: account for self import
        if (desc.getThrowable() != null
//                && desc.getThrowable() instanceof ClassNotFoundException
                && desc.getThrowable().getClass().equals(ClassNotFoundException.class)) {
            // ok, we can work on this one...
            ClassNotFoundException cnfe = (ClassNotFoundException) desc.getThrowable();
            String classToLoad = cnfe.getMessage();
            TrackedBundle tb = desc.getSourceBundle();
            TrackedBundle b;
            // first ensure to have a bundle to work with
            if (tb != null) {
                if (tb.getBundle() != null) {
                    // everything as expected
                    b = tb;
                } else {
                    // we can't get a hold of the bundle. maybe it got
                    // uninstalled. Stop here
                    return new SimpleReasonerResult(true,
                            0.2f,
                            "The CNF Exception is associated to a bundle no longer available to the runtime. "
                            + "Nothing we can do here.",
                            ERROR_PREFIX + "00", cnfe);
                }
            } else {
                // this shouldn't happen, but let's try to guess the bundle and
                // then go on with it.
                StackTraceElement ste = getFirstNonDefaultJavaStackTraceElement(cnfe);
                if (ste == null) {
                    // bad luck this case is not covered yet!
                    return new SimpleReasonerResult(false,
                            0.1f,
                            "Couldn't determine what bundle initiated the CNFE - maybe it was one of "
                            + "the excluded ones like org.junit? Nothing we can do here - for now. "
                            + "You may look at the implementation though.",
                            ERROR_PREFIX + "01", cnfe);
                }
                Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(ste.getClassName());
                if (bundles == null) {
                    return new SimpleReasonerResult(true,
                            0.1f,
                            "Couldn't determine where the CNFE was thrown. The class suspected to be "
                            + "responsible could not be found in the runtime, so no Bundle could be associated.",
                            ERROR_PREFIX + "02", cnfe);
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
                return new SimpleReasonerResult(true,
                        0.7f,
                        "The class you were trying to load is in the bundle that tries to load it, "
                        + "which should be fine. Maybe you're using a different ClassLoader to do this? "
                        + "Check out the Stacktrace and look for the class loading. "
                        + "A good starting point might be: "
                        + ste.getClassName()
                        + " line "
                        + ste.getLineNumber(), ERROR_PREFIX + "03",
                        cnfe);
            } catch (Throwable t) {
                // do nothing, just continue
            }
            // 2.) check if the bundle defines an import on the package it tries to load
            String packageName = classToLoad.substring(0, (classToLoad.lastIndexOf(".")));
            ImportedPackage impPkg = getMatchingImportedPackage(packageName, b.getAllImportedPackages());
            boolean matchesDynImport = matchesDynamicImportPackageStatements(packageName, b.getAllDynamicImportedPackages());
            if (impPkg == null && !matchesDynImport) {
                // simple: no import defined for package in bundle
                return new SimpleReasonerResult(true,
                        1f,
                        "The class you were trying to load is not contained in the bundle nor is the package imported! "
                        + "Add the import statement of \"" + packageName + "\" to bundle: " + b.getBundle().getSymbolicName() + "-" + b.getBundle().getVersion(),
                        ERROR_PREFIX + "04",
                        cnfe);
            } else if (impPkg != null) {
                // FIXME: search all "connected" bundles and match their export statements
                // for now... try to load the class in each deployed bundle return the successful attempts
                Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(classToLoad);
                if (bundles == null || bundles.length == 0) {
                    // the package has to be exported, otherwise this bundle wouldn't resolve - must be a not existing API class
                    // TODO: be more specific. We can track all exported packages to find the one matching.
                    if (impPkg.isOptional()) {
                        return new SimpleReasonerResult(true,
                                0.8f,
                                "Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a"
                                + "version problem with a not existing class in the provided version of package \"" + packageName + "\". "
                                + "It can also mean that this optional import is NOT wired by the OSGi container!!!",
                                ERROR_PREFIX + "05", cnfe);
                    } else {
                        return new SimpleReasonerResult(true,
                                1f,
                                "Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a"
                                + "version problem with a not existing class in the provided version of package \"" + packageName + "\"",
                                ERROR_PREFIX + "06", cnfe);
                    }
                }
            } else if (matchesDynImport) {
                // most likely we have multiple providers in different versions were not all meet the requirements
                Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(classToLoad);
                ArrayList<ExportedPackage> matchingExpPackages = new ArrayList<ExportedPackage>();
                if (bundles.length != 1) {
                    for (Bundle bundle : bundles) {
                        TrackedBundle tbExp = Activator.getFrameworkInspector().getTrackedBundle(bundle.getBundleId());
                        // check if this bundle defines a matching export statement
                        Iterator<ExportedPackage> expIter = tbExp.getAllExportedPackages().iterator();
                        while (expIter.hasNext()) {
                            ExportedPackage ep = expIter.next();
                            if (ep.statisfiesImport(impPkg, false)) {
                                matchingExpPackages.add(ep);
                            }
                        }
                    }
                }
                // this should never happen - the bundle wouldn't resolve and hence not throw the exception
                if (matchingExpPackages.isEmpty()) {
                    // we couldn't find a matching export
                    return new SimpleReasonerResult(true,
                            1f,
                            "Couldn't find a matching export statement in ANY bundle in the OSGi container. "
                            + "Most likely you need to provide a matching version of package \"" + packageName + "\".",
                            ERROR_PREFIX + "07", cnfe);
                } else {
                    return new SimpleReasonerResult(true,
                            1f,
                            "The package \"" + packageName + "\" is exported by several bundles. Most likely your dynamic"
                            + " import statement got bound to the wrong version.",
                            ERROR_PREFIX + "08", cnfe);
                }
            }
            // TODO: incorporate boot delegation
            // no idea what else could have gone wrong...
            
        // check for NoClassDefFoundError
        } else if (desc.getThrowable() != null
//                && desc.getThrowable() instanceof NoClassDefFoundError
                && desc.getThrowable().getClass().equals(NoClassDefFoundError.class)) {
            // ok, we can work on this one...
			NoClassDefFoundError ncdfe = (NoClassDefFoundError) desc.getThrowable();
            String classToLoad = getClassToLoad(ncdfe.getMessage());
            if(null == classToLoad){
            	return new NoClueReasonerResult();
            }
            TrackedBundle tb = desc.getSourceBundle();
            TrackedBundle b;
            // first ensure to have a bundle to work with
            if (tb != null) {
                if (tb.getBundle() != null) {
                    // everything as expected
                    b = tb;
                } else {
                    // we can't get a hold of the bundle. maybe it got
                    // uninstalled. Stop here
                    return new SimpleReasonerResult(true,
                            0.2f,
                            "The NoClassDefFoundError is associated to a bundle no longer available to the runtime. "
                            + "Nothing we can do here.",
                            ERROR_PREFIX + "09", ncdfe);
                }
            } else {
                // this shouldn't happen, but let's try to guess the bundle and
                // then go on with it.
                StackTraceElement ste = getFirstNonDefaultJavaStackTraceElement(ncdfe);
                if (ste == null) {
                    // bad luck this case is not covered yet!
                    return new SimpleReasonerResult(false,
                            0.1f,
                            "Couldn't determine what bundle initiated the NoClassDefFoundError - maybe it was one of "
                            + "the excluded ones like org.junit? Nothing we can do here - for now. "
                            + "You may look at the implementation though.",
                            ERROR_PREFIX + "10", ncdfe);
                }
                Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(ste.getClassName());
                if (bundles == null) {
                    return new SimpleReasonerResult(true,
                            0.1f,
                            "Couldn't determine where the CNFE was thrown. The class suspected to be "
                            + "responsible could not be found in the runtime, so no Bundle could be associated.",
                            ERROR_PREFIX + "11", ncdfe);
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
            String[] packageAndClassName = Utils.splitPackageAndClassForSearch(classToLoad);
        	try {
        		URL url = b.getBundle().getEntry(packageAndClassName[2]);
            	// Workaround in the mean time
            	if(url != null){
            		// something was found...	
	            	// most likely you're using a different class loader...
	                StackTraceElement ste = getFirstNonDefaultJavaStackTraceElement(ncdfe);
	                return new SimpleReasonerResult(true,
	                        0.7f,
	                        "The class you were trying to load "
	                        + classToLoad
	                        + " is in the bundle that tries to load it, "
	                        + "which should be fine. Maybe you're using a different ClassLoader to do this? "
	                        + "Check out the Stacktrace and look for the class loading. Also make sure you do not have a split package problem! "
	                        + "A good starting point might be: "
	                        + ste.getClassName()
	                        + " line "
	                        + ste.getLineNumber(), ERROR_PREFIX + "12",
	                        ncdfe);
            	}
                
            } catch (Throwable t) {
                // do nothing, just continue
            } 
            // 2.) check if the bundle defines an import on the package it tries to load
            String packageName = packageAndClassName[0];
            ImportedPackage impPkg = getMatchingImportedPackage(packageName, b.getAllImportedPackages());
            boolean requiredBundles = hasRequiredBundles(b);
            boolean matchesDynImport = matchesDynamicImportPackageStatements(packageName, b.getAllDynamicImportedPackages());
            if (impPkg == null && !matchesDynImport && !requiredBundles) {
                // simple: no import(s) defined for package in bundle
                return new SimpleReasonerResult(true,
                        1f,
                        "The class you were trying to load "
	                        + classToLoad
	                        + " is not contained in the bundle nor is the package imported! "
	                        + "Add the import statement of \"" + packageName + "\" to bundle: " + b.getBundle().getSymbolicName() + "-" + b.getBundle().getVersion(),
                        ERROR_PREFIX + "13",
                        ncdfe);
            } else if (impPkg != null) {
                // FIXME: search all "connected" bundles and match their export statements
                // for now... try to load the class in each deployed bundle return the successful attempts
                Bundle[] bundles = Activator.getFrameworkInspector().getBundlesContainingClass(classToLoad);
                if (bundles == null || bundles.length == 0) {
                    // the package has to be exported, otherwise this bundle wouldn't resolve - must be a not existing API class
                    // TODO: be more specific. We can track all exported packages to find the one matching.
                    if (impPkg.isOptional()) {
                        return new SimpleReasonerResult(true,
                                0.8f,
                                "Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a"
                                + "version problem with a not existing class in the provided version of package \"" + packageName + "\". "
                                + "It can also mean that this optional import is NOT wired by the OSGi container!!!",
                                ERROR_PREFIX + "14", ncdfe);
                    } else {
                        return new SimpleReasonerResult(true,
                                1f,
                                "Couldn't load the requested class from ANY bundle in the OSGi container. Most likely this is a"
                                + "version problem with a not existing class in the provided version of package \"" + packageName + "\"",
                                ERROR_PREFIX + "15", ncdfe);
                    }
                }
            } else if (requiredBundles) {
            	// we know it is a class and we know the package
            	// 1. Try to find the TrackedBundle exposing the package
            	Bundle[] bundles = Activator.getFrameworkInspector().getBundlesContainingClass(classToLoad);
            	if (bundles == null || bundles.length == 0) {
            		// In case we do not have a match. Propose adding a bundle that supports such an export
            		return new SimpleReasonerResult(true,
                            0.7f,
                            "Couldn't find the requested class in ANY bundle in the OSGi container. Most likely this is a "
                            + "problem with a not existing bundle that provides the package \"" + packageName + "\". ",
                            ERROR_PREFIX + "16", ncdfe);
            		// It can also mean that this optional required bundle is NOT wired by the OSGi container!!! (or it is provided by the system)
            	} else {
            		// 2. If we have a match propose adding a dependency to one of the matches
            		if(bundles.length == 1) {
            			return new SimpleReasonerResult(true,
                                0.6f,
                                "Could find the requested class from "+bundles.length+" bundle in the OSGi container. Most likely a wiring to " +
                                "the bundle that provides the package \"" + packageName + "\" is missing. " +
                                "Try adding the import package statement " + packageName +
                                " or the required bundle statement for bundle " + bundles[0].getSymbolicName() + " with id: " + bundles[0].getBundleId() +
                                " It can also mean that this is an optional dependency that is NOT wired by the OSGi container!!!", 
                                ERROR_PREFIX + "17", ncdfe);
            		} else {
            			String myBundles = "[" +bundles[0].getBundleId()+ "] " + bundles[0].getSymbolicName();
            			for(int j=1; j<bundles.length;j++){
            				myBundles = "[" +bundles[j].getBundleId()+ "] " + bundles[j].getSymbolicName();
            			}
            			return new SimpleReasonerResult(true,
                                0.6f,
                                "Could find the requested class from "+bundles.length+" bundles in the OSGi container. Most likely a wiring to " +
                                "the bundle that provides the package \"" + packageName + "\" is missing. " +
                                "Try adding the import package statement " + packageName +
                                " or the required bundle statement for one of the following bundles: " + myBundles +
                                " It can also mean that this is an optional dependency that is NOT wired by the OSGi container!!!", 
                                ERROR_PREFIX + "17", ncdfe);            			// It can also mean that this optional required bundle is NOT wired by the OSGi container!!! (or it is provided by the system)
            		}
            	}
            	
            	
            } else if (matchesDynImport) {
                // most likely we have multiple providers in different versions were not all meet the requirements
                Bundle[] bundles = Activator.getFrameworkInspector().getBundleForClassName(classToLoad);
                ArrayList<ExportedPackage> matchingExpPackages = new ArrayList<ExportedPackage>();
                if (bundles.length != 1) {
                    for (Bundle bundle : bundles) {
                        TrackedBundle tbExp = Activator.getFrameworkInspector().getTrackedBundle(bundle.getBundleId());
                        // check if this bundle defines a matching export statement
                        Iterator<ExportedPackage> expIter = tbExp.getAllExportedPackages().iterator();
                        while (expIter.hasNext()) {
                            ExportedPackage ep = expIter.next();
                            if (ep.statisfiesImport(impPkg, false)) {
                                matchingExpPackages.add(ep);
                            }
                        }
                    }
                }
                // this should never happen - the bundle wouldn't resolve and hence not throw the exception
                if (matchingExpPackages.isEmpty()) {
                    // we couldn't find a matching export
                    return new SimpleReasonerResult(true,
                            1f,
                            "Couldn't find a matching export statement in ANY bundle in the OSGi container. "
                            + "Most likely you need to provide a matching version of package \"" + packageName + "\".",
                            ERROR_PREFIX + "18", ncdfe);
                } else {
                    return new SimpleReasonerResult(true,
                            1f,
                            "The package \"" + packageName + "\" is exported by several bundles. Most likely your dynamic"
                            + " import statement got bound to the wrong version.",
                            ERROR_PREFIX + "19", ncdfe);
                }
            } 
            // TODO: incorporate required bundles, boot delegation
            // no idea what else could have gone wrong...
        }
        return new NoClueReasonerResult();
    }

    private boolean hasRequiredBundles(TrackedBundle b) {
		if(b.getRequiredBundles().isEmpty()){
    		return false;
    	}
		return true;
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

    private ImportedPackage getMatchingImportedPackage(String packageName, List<ImportedPackage> impPackages) {
        Iterator<ImportedPackage> iterImp = impPackages.iterator();
        while (iterImp.hasNext()) {
            ImportedPackage impPack = iterImp.next();
            if (packageName.equals(impPack.getPackageName())) {
                // found it!
                return impPack;
            }
        }
        return null;
    }

    private boolean matchesDynamicImportPackageStatements(String packageName, List<ImportedPackage> impPackages) {
        Iterator<ImportedPackage> iterImp = impPackages.iterator();
        while (iterImp.hasNext()) {
            ImportedPackage impPack = iterImp.next();
            String impPackageName = impPack.getPackageName();
            if (impPackageName.endsWith("*")) {
                // match substring
                if (impPackageName.length() == 1) {
                    // we import everything... (and do not account for property matching!!!)
                    return true;
                } else {
                    return packageName.startsWith(impPackageName.replace("*", ""));
                }
            } else {
                return (impPackageName.equals(packageName));
            }
        }
        return false;
    }
    
    private String getClassToLoad(String message) {
    	// we're basically guessing here, but that's better than nothing, right?
    	String[] messageFragments = message.split(" ");
    	if(messageFragments != null){
    		for(int i=0;messageFragments.length<i;i++){
    			if(messageFragments[i].contains(".")){
    				return messageFragments[i].replace('.','/');
    			}
    		}
    	}
    	return message;
    }
}
