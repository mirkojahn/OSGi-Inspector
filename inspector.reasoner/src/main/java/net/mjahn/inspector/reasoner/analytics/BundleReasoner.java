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
public class BundleReasoner extends ReasonerBase {

    public BundleReasoner(){
        super("Bundle Reasoner");
    }

    public ReasonerResult analyze(JobDescription desc, ReasoningEngine engine, ReasonerResultCompiler resultCompiler) {
        		if(desc.getSourceBundle() !=null){
//			// ok, we can work on this one...
//			if(tb.isFragment()){
//				// check if it is actually attached to the host...
//				Bundle b = tb.getBundle();
//				if(b != null){
//					if(b.getState() == Bundle.INSTALLED){
//						// check why
//						// 1. FW didn't start till now
//						// 2. Missmatch on manifest entries.
//						// 3. Problems with the host
//					} else if(b.getState() == Bundle.UNINSTALLED){
//						return new DefaultReasonerResult(1.0f,"The fragment was uninstalled, so all bets are of. You shouldn't interact with it! Try to call update on the host(s) and refreshPackages.", null);
//					} else if(b.getState() == Bundle.RESOLVED){
//						return new DefaultReasonerResult(0.1f, "The fragment is resolved. Everything should work as expected for OSGi's perspective. Maybe a domain specific application requires something special.", null);
//					} else {
//						return new DefaultReasonerResult(1.0f,"Something is wrong. The fragment should never be able to enter this state. This looks like a bug in the OSGi container you're using.", null);
//					}
//				} else {
//					// FIXME: no idea what to do here
//				}
//			} else {
//				// FIXME: think about problems a bundle might have... (import missing in container, exception while starting, missing services -> delegate)
//			}

		}
		return new NoClueReasonerResult();
    }

}
