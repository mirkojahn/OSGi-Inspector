package net.mjahn.inspector.reasoner.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.Reasoner;
import net.mjahn.inspector.reasoner.ReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerTask;
import net.mjahn.inspector.reasoner.ReasonerTaskCapability;

public class BundleReasonerTask implements ReasonerTask {

	public String getDiscription(Locale l) {
		return getDiscription();
	}

	public String getDiscription() {
		return "This Reasoner Task analyzes Bundles based on their capabilities. And guesses, why they didn't start/attach if it is a fragment.";
	}
	
	public boolean requireBundle(){
		return true;
	}
	
	public List<ReasonerTaskCapability> requireDictionaryEntries(){
		ArrayList<ReasonerTaskCapability> list = new ArrayList<ReasonerTaskCapability>();
		list.add(new ReasonerTaskCapability(Reasoner.REASONER_ANALYZE_BUNDLE, "A string set to 'true' (case sensitive)", true));
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public ReasonerResult analyze(TrackedBundle tb, Dictionary dict) {
		if(dict.get(Reasoner.REASONER_ANALYZE_BUNDLE)!=null 
				&& Boolean.parseBoolean((String) dict.get(Reasoner.REASONER_ANALYZE_BUNDLE))== true){
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
		return new DefaultReasonerResult();
	}

	

}
