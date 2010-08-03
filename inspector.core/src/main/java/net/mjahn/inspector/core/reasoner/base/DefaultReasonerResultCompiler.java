package net.mjahn.inspector.core.reasoner.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;

public class DefaultReasonerResultCompiler implements ReasonerResultCompiler{
	
	ArrayList<ReasonerResult> results = new ArrayList<ReasonerResult>();
	ReasonerResult finalResult;

	public void add(ReasonerResult result) {
		results.add(result);		
	}

	public List<ReasonerResult> getResults() {
		return results;
	}
 
	public String getErrorCode() {
		synchronized(results){
			if(finalResult == null){
				finalResult = getHighestPriorityResult(results);
			}
			return finalResult.getErrorCode();
		}
	}

	public String getResultMessage() {
		synchronized(results){
			if(finalResult == null){
				finalResult = getHighestPriorityResult(results);
			}
			return finalResult.getResultMessage();
		}
	}

	public float getConfidenceLevel() {
		synchronized(results){
			if(finalResult == null){
				finalResult = getHighestPriorityResult(results);
			}
			return finalResult.getConfidenceLevel();
		}
	}

	public boolean isConfident() {
		synchronized(results){
			if(finalResult == null){
				finalResult = getHighestPriorityResult(results);
			}
			return finalResult.isConfident();
		}
	}
	
	public void reset(){
		synchronized (results) {
			finalResult=null;
		}
	}
	
	protected static ReasonerResult getHighestPriorityResult(List<ReasonerResult> list){
		ReasonerResult result = null;
		Iterator<ReasonerResult> iter = list.iterator();
		while(iter.hasNext()){
			ReasonerResult temp = iter.next();
			if( result == null || temp.getConfidenceLevel()>result.getConfidenceLevel()){
				result = temp;
			}
		}
		return result;
	}

}
