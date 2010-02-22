package net.mjahn.inspector.reasoner;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.TrackedBundle;

public class ReasonerUtil {
	
	@SuppressWarnings("unchecked")
	public static final List<ReasonerResult> reasonAboutThrowable(Reasoner reasonerService, Throwable exception, TrackedBundle trackedBundle, ReasonerTask reasonerToIgnore){
		Dictionary dict = getBasicDict(reasonerToIgnore);
		if(exception != null){
			dict.put(Reasoner.REASONER_ANALYZE_THROWABLE, exception);
		}
		return reasonerService.analyze(trackedBundle, dict);
	}

	@SuppressWarnings("unchecked")
	public static final List<ReasonerResult> reasonAboutBundle(Reasoner reasonerService, TrackedBundle trackedBundle, ReasonerTask reasonerToIgnore){
		Dictionary dict = getBasicDict(reasonerToIgnore);
		if(trackedBundle != null){
			dict.put(Reasoner.REASONER_ANALYZE_BUNDLE, trackedBundle);
		}
		return reasonerService.analyze(trackedBundle, dict);
	}
	
	@SuppressWarnings("unchecked")
	public static final List<ReasonerResult> reasonAboutService(Reasoner reasonerService, String serviceFilter, TrackedBundle trackedBundle, ReasonerTask reasonerToIgnore){
		Dictionary dict = getBasicDict(reasonerToIgnore);
		if(serviceFilter != null){
			dict.put(Reasoner.REASONER_ANALYZE_SERVICE, serviceFilter);
		}
		return reasonerService.analyze(trackedBundle, dict);
	}
	
	@SuppressWarnings("unchecked")
	public static final List<ReasonerResult> reasonAboutCustom(Reasoner reasonerService, Hashtable dictionary, TrackedBundle trackedBundle, ReasonerTask reasonerToIgnore){
		Hashtable dict = getBasicDict(reasonerToIgnore);
		if(dictionary != null){
			dictionary.putAll(dict);
		}else{
			dictionary = dict;
		}
		return reasonerService.analyze(trackedBundle, dictionary);
	}

	@SuppressWarnings("unchecked")
	public static final Hashtable getBasicDict(ReasonerTask reasonerToIgnore){
		Hashtable dict = new Hashtable();
		dict.put(Reasoner.REASONER_ANALYZE_TASK_ID, "ID_"+System.currentTimeMillis());
		HashSet<ReasonerTask> ignore = new HashSet<ReasonerTask>();
		if(reasonerToIgnore != null){
			ignore.add(reasonerToIgnore);
		}
		dict.put(Reasoner.REASONER_IGNORE, ignore);
		return dict;
	}
	
	public static ReasonerResult getHighestPriorityResult(List<ReasonerResult> list){
		ReasonerResult result = null;
		Iterator<ReasonerResult> iter = list.iterator();
		while(iter.hasNext()){
			ReasonerResult temp = iter.next();
			if(temp.getNestedReasonerResults()!=null){
				ReasonerResult temp2 = getHighestPriorityResult(temp.getNestedReasonerResults());
				if(temp2 != null && temp2.getErrorConfidence()>temp.getErrorConfidence()){
					temp = temp2;
				}
			}
			if( result == null || temp.getErrorConfidence()>result.getErrorConfidence()){
				result = temp;
			}
		}
		return result;
	}
}
