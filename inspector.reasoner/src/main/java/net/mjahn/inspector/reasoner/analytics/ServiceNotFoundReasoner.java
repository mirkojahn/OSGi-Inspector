/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mjahn.inspector.reasoner.analytics;

import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.ReasoningEngine;
import net.mjahn.inspector.core.reasoner.base.NoClueReasonerResult;
import net.mjahn.inspector.core.reasoner.base.ReasonerBase;

/**
 *
 * @author mjahn
 */
public class ServiceNotFoundReasoner extends ReasonerBase {

    public ServiceNotFoundReasoner() {
        super("ServiceNotFound Reasoner");
    }

    public ReasonerResult analyze(JobDescription desc, ReasoningEngine engine, ReasonerResultCompiler resultCompiler) {
        if (desc.getServiceRequestToAnalyze() != null) {
            // a service wasn't found so...
            // 1. check if there is actually a bundle providing the service (independent of the class space)
//            Activator.getBundleContext().getServiceReferences(clazz, filter) // 2. check if there is a bundle importing the service package, but that is not ACTIVE and thus can't provide the service
                    // 2.a check why this particular bundle didn't start!
                    // 3. last resort: check if at least a bundle is exporting the service package
                    // (identifying if these bundles are waiting for other services might be second step solution)

        }
        return new NoClueReasonerResult();
    }
}
