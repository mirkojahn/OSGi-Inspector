package net.mjahn.inspector.reasoner;

import java.util.List;
import java.util.Locale;

public interface ReasonerResult {
	
	float getErrorConfidence();
	
	String getResultMessage();

	String getResultMessage(Locale l);
	
	Throwable getCause();
	
	String getErrorCode();
	
	List<ReasonerResult> getNestedReasonerResults();
}
