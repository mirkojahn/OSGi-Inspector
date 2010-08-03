package net.mjahn.inspector.core.reasoner;


public interface ReasonerResult {
	
	String REASONER_RESULT_NOT_APPLICABLE = "No clue what to do in this case! Try a different ReasonerTask.";
	
	
	// TODO: Move to default implementation, they are Reasoner specific!
	String REASONER_ERROR_CODE_CORE_PREFIX = "MJA-CORE-";
	String REASONER_ERROR_CODE_CLASSNOTFOUND_PREFIX = "CNF-";
	String REASONER_ERROR_CODE_BUNDLEEXCEPTION_PREFIX = "BEX-";
	
	// TODO: Think about having a property list or some sort of history here
	boolean isConfident();
	
	float getConfidenceLevel();
	
	String getResultMessage();
	
	String getErrorCode();
	
}
