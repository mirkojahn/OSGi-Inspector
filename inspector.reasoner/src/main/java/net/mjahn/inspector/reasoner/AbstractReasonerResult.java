package net.mjahn.inspector.reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public abstract class AbstractReasonerResult implements ReasonerResult {
	private final float conf;
	private final String message;
	private final List<ReasonerResult> results;
	private final Throwable cause;
	private final String error;
	
	public AbstractReasonerResult(float confidence, String reasonerMessage, String errorCode, Throwable t, List<ReasonerResult> reasonerResults){
		conf = confidence;
		message = reasonerMessage;
		if(reasonerResults == null){
			results = new ArrayList<ReasonerResult>();
		} else {
			results = reasonerResults;
		}
		cause = t;
		error = errorCode;
	}
	
	public AbstractReasonerResult(float confidence, String reasonerMessage, String errorCode, List<ReasonerResult> reasonerResults){
		this(confidence, reasonerMessage, errorCode, null, reasonerResults);
	}
	
	public AbstractReasonerResult(){
		this(0f,Reasoner.REASONER_RESULT_NOT_APPLICABLE, Reasoner.REASONER_ERROR_CODE_NO_CLUE, null,null);
	}

	public float getErrorConfidence() {
		return conf;
	}

	public String getResultMessage() {
		return message;
	}

	public abstract String getResultMessage(Locale l);

	public List<ReasonerResult> getNestedReasonerResults(){
		return results;
	}
	
	public Throwable getCause(){
		return cause;
	}
	
	public String getErrorCode(){
		return error;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("AbstractReasonerResult [cause=" + cause + ", conf=" + conf
				+ ", error=" + error + ", message=" + message + ", results={");
		Iterator<ReasonerResult> resIter = results.iterator();
		while(resIter.hasNext()){
			s.append("(");
			s.append(resIter.next().toString());
			s.append(")");
			if(resIter.hasNext()){
				s.append(", ");
			}
		}
		s.append("}]");
		return s.toString();
	}
	
	
}
