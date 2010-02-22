package net.mjahn.inspector.reasoner.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.Reasoner;
import net.mjahn.inspector.reasoner.ReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerTask;

import org.osgi.util.tracker.ServiceTracker;

public class ReasonerCore implements Reasoner {
	
	private ServiceTracker reasonerTaskTracker;
	
	public ReasonerCore(ServiceTracker st) {
		reasonerTaskTracker = st;
	}

	@SuppressWarnings("unchecked")
	public List<ReasonerResult> analyze(TrackedBundle tb, Dictionary dict) {
		Iterator<ReasonerTask> tasks=getReasonerTasks().iterator();
		if(dict.get(REASONER_ANALYZE_TASK_ID) == null){
			dict.put(REASONER_ANALYZE_TASK_ID, "ID_"+System.currentTimeMillis());
		}
		if(dict.get(REASONER_IGNORE) == null){
			dict.put(REASONER_IGNORE, new HashSet<String>());
		}
		ArrayList<ReasonerResult> results = new ArrayList<ReasonerResult>();
		while(tasks.hasNext()){
			// FIXME: check capabilities first
			ReasonerTask task = tasks.next();
			if(!((HashSet)dict.get(REASONER_IGNORE)).contains(task.getClass().getName())){
				ReasonerResult result = task.analyze(tb, dict);
				if(result.getErrorConfidence()>0){
					results.add(result);
				}
			}
		}
		return results;
	}

	private List<ReasonerTask>getReasonerTasks(){
		Object[] services = reasonerTaskTracker.getServices();
		ArrayList<ReasonerTask> list = new ArrayList<ReasonerTask>();
		if(services == null || services.length == 0){
			return list;
		}
		for(int i=0;i<services.length;i++){
			list.add((ReasonerTask)services[i]);
		}
		return list;
	}
}
