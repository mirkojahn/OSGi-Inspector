package net.mjahn.inspector.core.tools.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.mjahn.inspector.core.InvalidInvocationException;
import net.mjahn.inspector.core.ListenerInfo;
import net.mjahn.inspector.core.NotFoundServiceCall;
import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.core.impl.Activator;
import net.mjahn.inspector.core.impl.FrameworkInspectorImpl;
import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasoningServiceProvider;
import net.mjahn.inspector.core.reasoner.base.JobDescriptionBase;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

/**
 * Eclipse console extension implementing the bbn command.
 * <p>
 * When started in Eclipse, register an instance of this object as a service with the Interface
 * {@code org.eclipse.osgi.framework.console.CommandProvider}. It will then be registered in the
 * Eclipse console. Hit help in the console to see further information on how to use the command
 * properly.
 * </p>
 * 
 * @author Mirko Jahn
 * @version 1.0
 * @since 1.0
 */
public class EclipseCommands extends AbstractUtilCommand implements CommandProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
     * @version 1.0
     * @since 1.0
     */
    public String getHelp() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("---Handy tools for the OSGi runtime---\n");
        sBuilder.append("\tbbn - (short for get >>Bundle By Name<< of one of its classes) "
                + "returns the bundle(s) for a given full qualified class name.\n");
        sBuilder.append("\tcheckFW - check thrown BundleExceptions within the Eclipse Runtime and try to reason about the cause. (options are -a for all and -t for the confidence threshold like -t0.8f for 0.8f confidence.\n");
        sBuilder.append("\tpendingSR [<bundle id>] - check pending OSGi Service requests. If a list of bundle ids is given, only the bundles listed will be shown (space separated).\n");
        sBuilder.append("\tfailedSR [<bundle id>] - check for failed OSGi Service Requests tracked so far. If a list of bundle ids is given, only the bundles listed will be shown (space separated).\n");
        return sBuilder.toString();
    }

    /**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _bbn(final CommandInterpreter ci) throws Exception {
        // FIXME: allow more than one parameter
        Set<String> fqcns = new HashSet<String>();
        String fqcn = null;
        do {
            fqcn = ci.nextArgument();
            if (fqcn != null) {
                fqcns.add(fqcn);
            }
        } while (fqcn != null);
        if (fqcns.isEmpty()) {
            ci.println("ERROR: The command requires a full qualified class name like: net.mjahn.tools.inspector.IBundleUtils");
        } else {
            bundleByName(fqcns.toArray(new String[fqcns.size()]), new PrintStream(System.out));
        }
    }

    /**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _checkFW(final CommandInterpreter ci) throws Exception {
        List<FrameworkEvent> fwEvents = Activator.getFrameworkInspectorImpl().getErrorFrameworkEvents();
        ReasoningServiceProvider rsprov = Activator.getReasoningServiceProvider();
        
        boolean returnAll = false;
        float threshold = -0.1f;
        do {
        	String arg = ci.nextArgument();
        	if(arg == null){
        		break;
        	}
        	if(arg.equalsIgnoreCase("-a")){
	        	returnAll = true;
	        } else if(arg.startsWith("-t")){
	        	threshold = Float.parseFloat(arg.substring(2));
	        }
		} while (true);
        
	    	
        if (fwEvents.isEmpty()) {
            ci.println("No Error detected");
        } else {
            ci.println("Captured Runtime Errors/ Problems:");
            Iterator<FrameworkEvent> iter = fwEvents.iterator();
            while (iter.hasNext()) {
                try {
                    FrameworkEvent e = iter.next();
                    JobDescription jd = new JobDescriptionBase(null, Activator.getFrameworkInspectorImpl().getTrackedBundle(e.getBundle().getBundleId()), Activator.getFrameworkInspectorImpl().getTrackedBundle(e.getBundle().getBundleId()), null, e.getThrowable());
                    ReasonerResult result = rsprov.reason(jd);
                    if(returnAll == false && result.isConfident() == false){
                    	continue;
                    } else if (returnAll == false && result.getConfidenceLevel() > threshold) {
                    	
                    }
                    if (result != null) {
                        ci.println("Bundle: " + e.getBundle().getBundleId() + " " + e.getBundle().getSymbolicName());
                        ci.println(" Confidence: " + result.getConfidenceLevel());
                        ci.println(" Error Type: " + getErrorTypeString(((BundleException) e.getThrowable()).getType()));
                        ci.println(" Proposed Fix:" + result.getResultMessage());
                        ci.println(" Exception: " + e.getThrowable().toString());
                    } else {// this should never happen
                        ci.println("Bundle: " + e.getBundle().getBundleId() + " " + e.getBundle().getSymbolicName());
                        ci.println(" Error Type: " + getErrorTypeString(((BundleException) e.getThrowable()).getType()));
                        ci.println(" Error Message: no reasoner for this type available yet.");
                    }
                } catch (InvalidInvocationException ex) {
                    Logger.getLogger(EclipseCommands.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _pendingSR(final CommandInterpreter ci) throws Exception {
    	ci.println("The following services are watched (either by listeners or trackers): ");
        checkSR(ci, false);
    }
    
    /**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _failedSR(final CommandInterpreter ci) throws Exception {
    	ci.println("The following services faild at least once resolving: ");
        checkSR(ci, true);
    }

    private void checkSR(final CommandInterpreter ci, boolean checkErrors) throws Exception {
        FrameworkInspectorImpl fi = Activator.getFrameworkInspectorImpl();
        if (fi == null) {
            ci.println("No FrameworkInspector Service found! Can't perform command.");
        } else {
            List<? extends TrackedBundle> tbs = fi.getAllTrackedBundles();
            String arg = ci.nextArgument();
            if (arg != null) {
                while (arg != null) {
                    TrackedBundle tb = fi.getTrackedBundle(Integer.valueOf(arg));
                    if (tb != null) {
                        if(checkErrors){
                            if(!printServiceRequestErrorForTrackedBundle(tb, ci)) {
                                ci.println("no errors detected");
                            }
                        }else{
                            if(!printPendingServiceRequestsForTrackedBundle(tb, ci)) {
                                ci.println("no pending ServiceRequests detected");
                            }
                        }
                    }
                    arg = ci.nextArgument();
                }
            } else {
                Iterator<? extends TrackedBundle> tbsIter = tbs.iterator();
                boolean nothingDetected = true;
                while (tbsIter.hasNext()) {
                    TrackedBundle tb = tbsIter.next();
                    if (tb != null) {
                        if(checkErrors){
                            if(printServiceRequestErrorForTrackedBundle(tb, ci)) {
                                nothingDetected = false;
                            }
                        } else {
                            if(printPendingServiceRequestsForTrackedBundle(tb, ci)) {
                                nothingDetected = false;
                            }
                        }
                    }
                }
                if (nothingDetected == true) {
                    ci.println("nothing detected");
                }
            }
        }
    }

    private boolean printServiceRequestErrorForTrackedBundle(TrackedBundle tb, CommandInterpreter ci) {
    	List<NotFoundServiceCall> nfscs = tb.getAllNotFoundServiceCalls();
        if(nfscs.isEmpty()){
        	return false;
        }
        Iterator<NotFoundServiceCall> iter = nfscs.iterator();
        ci.println("\nBundle: "
                + tb.getBundle().getSymbolicName());
    	while(iter.hasNext()){
        	NotFoundServiceCall nfsc = iter.next();
        	ci.println(nfsc.toString());
        }
        return true;
//    	
//    	ArrayList list = new ArrayList();
//        if (!tb.getAllNotFoundServiceCalls().isEmpty()) {
//            Iterator<NotFoundServiceCall> it1 = tb.getAllNotFoundServiceCalls().iterator();
//            while (it1.hasNext()) {
//                list.add(it1.next());
//            }
//        }
//        if (list.size() > 0) {
//            Iterator iter = list.iterator();
//            if (!iter.hasNext()) {
//                // no problems found
//                return false;
//            }
//            ci.println("\nBundle: "
//                    + tb.getBundle().getSymbolicName());
//            while (iter.hasNext()) {
//                ci.println(iter.next().toString());
//            }
//            // problems found
//            return true;
//        } else {
//            // no problems found
//            return false;
//        }
    }

    private boolean printPendingServiceRequestsForTrackedBundle(TrackedBundle tb, CommandInterpreter ci) {
    	List<ListenerInfo> lis = tb.getAllAddedServiceListeners();
    	if(lis.isEmpty()){
    		return false;
    	}
    	ci.println("\nBundle: "
                + tb.getBundle().getSymbolicName());
    	Iterator<ListenerInfo> iter = lis.iterator();
    	while(iter.hasNext()){
    		ListenerInfo li = iter.next();
    		ci.println(li.toString());
    	}
    	return true;
//        ArrayList list = new ArrayList();
//        if (!tb.getAllAddedServiceListeners().isEmpty()) {
//            Iterator<ListenerInfo> it2 = tb.getAllAddedServiceListeners().iterator();
//            while (it2.hasNext()) {
//                list.add(it2.next());
//            }
//        }
//        if (list.size() > 0) {
//            Iterator iter = list.iterator();
//            if (!iter.hasNext()) {
//                // no problems found
//                return false;
//            }
//            ci.println("\nBundle: "
//                    + tb.getBundle().getSymbolicName());
//            while (iter.hasNext()) {
//                ci.println(iter.next().toString());
//            }
//            // problems found
//            return true;
//        } else {
//            // no problems found
//            return false;
//        }
    }

    private String getErrorTypeString(int type) {
        switch (type) {
            case BundleException.ACTIVATOR_ERROR:
                return "ACTIVATOR_ERROR";
            case BundleException.DUPLICATE_BUNDLE_ERROR:
                return "DUPLICATE_BUNDLE_ERROR";
            case BundleException.INVALID_OPERATION:
                return "INVALID_OPERATION";
            case BundleException.MANIFEST_ERROR:
                return "MANIFEST_ERROR";
            case BundleException.NATIVECODE_ERROR:
                return "NATIVECODE_ERROR";
            case BundleException.RESOLVE_ERROR:
                return "RESOLVE_ERROR";
            case BundleException.SECURITY_ERROR:
                return "SECURITY_ERROR";
            case BundleException.START_TRANSIENT_ERROR:
                return "START_TRANSIENT_ERROR";
            case BundleException.STATECHANGE_ERROR:
                return "STATECHANGE_ERROR";
            case BundleException.UNSPECIFIED:
                return "UNSPECIFIED";
            case BundleException.UNSUPPORTED_OPERATION:
                return "UNSUPPORTED_OPERATION";
            default:
                return "unknown bundle exception type: " + type;
        }
    }
}
