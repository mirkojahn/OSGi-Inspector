package net.mjahn.inspector.reasoner.impl;

import java.util.List;
import java.util.Locale;

import net.mjahn.inspector.reasoner.AbstractReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerResult;

public class DefaultReasonerResult extends AbstractReasonerResult {
	
	public DefaultReasonerResult(float confidence, String message, String errorCode, List<ReasonerResult> results){
		super(confidence, message, errorCode, results);
	}
	
	public DefaultReasonerResult(float confidence, String message, String errorCode, Throwable t, List<ReasonerResult> results){
		super(confidence, message, errorCode, t, results);
	}
	
	public DefaultReasonerResult(){
		super();
	}

	public String getResultMessage(Locale l) {
		return getResultMessage();
	}

}
