/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mjahn.inspector.reasoner.analytics;

import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.mjahn.inspector.core.InvalidInvocationException;
import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.BaseErrorCodes;
import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ModifyableJobDescription;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.ReasoningEngine;
import net.mjahn.inspector.core.reasoner.base.NoClueReasonerResult;
import net.mjahn.inspector.core.reasoner.base.ReasonerBase;
import net.mjahn.inspector.core.reasoner.base.SimpleReasonerResult;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 *
 * @author mjahn
 */
public class BundleExceptionReasoner extends ReasonerBase {

    private static String ERROR_PREFIX = BaseErrorCodes.REASONER_ERROR_CODE_CORE_PREFIX
            + BaseErrorCodes.REASONER_ERROR_CODE_BUNDLEEXCEPTION_PREFIX;

    public BundleExceptionReasoner() {
        super("BundleException Reasoner");
    }

    public ReasonerResult analyze(JobDescription desc, ReasoningEngine engine, ReasonerResultCompiler resultCompiler) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (desc.getThrowable() != null
                && (desc.getThrowable() instanceof BundleException)) {
            // ok, we can work on this one...
            // get the exception
            BundleException be = (BundleException) desc.getThrowable();
            // the bundle causing the problem or where the problem originated
            TrackedBundle tb = desc.getSourceBundle();
            Throwable cause = be.getCause();
            // the type of the Exception
            int type = be.getType();
            // if it is a unspecified exception... it is not related to OSGi in general
            if (type == BundleException.ACTIVATOR_ERROR) {

                // TODO: it might be a good idea to check ALL nested Exceptions as well...
                if (cause != null) {
                    // this is an exception we don't know how to handle... delegate
                    // creating a delegate...
                    ModifyableJobDescription mDesc;
                    try {
                        mDesc = engine.createChildJobDescription(desc);
                        mDesc.setSourceBundle(tb);
                        mDesc.setObjectToAnalyze(tb);
                        mDesc.setThrowableToAnalyze(cause);
                        // obtaining Reasoner results
                        ReasonerResult res = engine.reason(mDesc, null);
                        if (res == null || !res.isConfident()) {
                            // nothing we can do. Return some low confidence result about what we know.
                            return new SimpleReasonerResult(false, 0, "The BundleException doesn't provide a known/ handable cause and other reasoners don't have a clue either. ", ERROR_PREFIX + "00", null);
                        } else {
                            return res;
                        }
                    } catch (InvalidInvocationException ex) {
                        // can't happen at this point
                    }

                } else {
                    // absolutely NO clue!
                    return new SimpleReasonerResult(false, 0, "We have an ActivatorError BundleException without an attached Exception. No clue what that means.", ERROR_PREFIX + "02", null);
                }
            } else if (type == BundleException.RESOLVE_ERROR) {
                // FIXME: implement the logic
                // first, check if it is a fragment
                // yes -->
                //		is there a host to attach to? (first the bundle then, then the attachment rules, then conflicting manifest headers) --> error
                // no -->
                // 		is there a missing import?
                //		is there a problem with the attributes/directives of the import?
                return new SimpleReasonerResult(false, 0.1f, "The given error is not yet handled by the reasoner: BundleExceptionType RESOLVE_ERROR (" + type + ")", ERROR_PREFIX + "99", null);
            } else if (type == BundleException.DUPLICATE_BUNDLE_ERROR) {
                // there is just one reason why this could happen
                if (tb != null) {
                    return new SimpleReasonerResult(true, 1f, "The install or update operation failed because another already installed bundle has the same symbolic name and version. " + tb.getBundle().getSymbolicName() + " " + tb.getBundle().getVersion(), ERROR_PREFIX + "10", null);
                } else {
                    // TODO: extract the bundle name from the exception
                    return new SimpleReasonerResult(true, 1f, "The install or update operation failed because another already installed bundle has the same symbolic name and version. ", ERROR_PREFIX + "10", null);
                }
            } else if (type == BundleException.INVALID_OPERATION) {
                // FIXME: implement the logic
                return new SimpleReasonerResult(false, 0.1f, "The given error is not yet handled by the reasoner: BundleExceptionType INVALID_OPERATION (" + type + ") " + be.getMessage(), ERROR_PREFIX + "99", null);
            } else if (type == BundleException.MANIFEST_ERROR) {
                // FIXME: implement the logic
                return new SimpleReasonerResult(false, 0.1f, "The given error is not yet handled by the reasoner: BundleExceptionType MANIFEST_ERROR (" + type + ")", ERROR_PREFIX + "99", null);
            } else if (type == BundleException.SECURITY_ERROR) {
                return new SimpleReasonerResult(true, 1f, "An operation failed due to insufficient permissions. " + be.getMessage(), ERROR_PREFIX + "13", null);
            } else if (type == BundleException.START_TRANSIENT_ERROR) {
                return new SimpleReasonerResult(true, 1f, "The start transient operation failed because the start level of the bundle is greater than the current framework start level. " + be.getMessage(), ERROR_PREFIX + "14", null);
            } else if (type == BundleException.STATECHANGE_ERROR) {
                return new SimpleReasonerResult(true, 1f, "The operation failed to complete the requested lifecycle state change. " + be.getMessage(), ERROR_PREFIX + "15", null);
            } else if (type == BundleException.UNSUPPORTED_OPERATION) {
                // FIXME: implement the logic
                return new SimpleReasonerResult(false, 0.1f, "The given error is not yet handled by the reasoner: BundleExceptionType UNSUPPORTED_OPERATION (" + type + ")", ERROR_PREFIX + "99", null);
            } else if (type == BundleException.NATIVECODE_ERROR) {
                String hd = "";
                try {
                    Dictionary headerDict = tb.getBundle().getHeaders();
                    hd = (String) headerDict.get(Constants.BUNDLE_NATIVECODE);
                } catch (Exception e) {
                    hd = "Couldn't resolve the native header.";
                }
                return new SimpleReasonerResult(true, 1f, "The bundle could not be resolved due to an error with the Bundle-Native-Code header: " + hd, ERROR_PREFIX + "17", null);
            } else {
                return new SimpleReasonerResult(false, 0f, "The given error is not known by the reasoner: BundleExceptionType " + type, ERROR_PREFIX + "99", null);
            }
        }
        return new NoClueReasonerResult();
    }
}
