package net.mjahn.inspector.core.tools.impl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.mjahn.inspector.core.InvalidInvocationException;
import net.mjahn.inspector.core.impl.Activator;
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
        StringBuffer buffer = new StringBuffer();
        buffer.append("---Handy tools for the OSGi runtime---\n");
        buffer.append("\tbbn - (short for get >>Bundle By Name<< of one of its classes) "
                + "returns the bundle(s) for a given full qualified class name.\n");
        buffer.append("\tcheckFW - check thrown BundleExceptions within the Eclipse Runtime and try to reason about the cause.\n");
        return buffer.toString();
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
                    if (result != null) {
                        ci.println(
                                "Bundle: "+e.getBundle().getBundleId() + " /r/n"
                                 + " Confidence: " + result.getConfidenceLevel() + " /r/n"
                                 + " Error Type: " + getErrorTypeString(((BundleException) e.getThrowable()).getType()) + " /r/n"
                                 + " Error Message:" + result.getResultMessage());
                    } else {
                        ci.println(
                                "Bundle: "+e.getBundle().getBundleId() + " /r/n"
                                 + " Error Type: " + getErrorTypeString(((BundleException) e.getThrowable()).getType()) + " /r/n"
                                 + " Error Message: no reasoner for this type available yet.");
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
    public void _checkSV(final CommandInterpreter ci) throws Exception {
        //FIXME: to implement
        ci.println("This command line extension is supposed to check for ServiceRequest errors, but it's not implemented yet ;-)");
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
