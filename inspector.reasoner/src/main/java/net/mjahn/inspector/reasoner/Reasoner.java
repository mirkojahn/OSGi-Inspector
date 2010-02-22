package net.mjahn.inspector.reasoner;

import java.util.Dictionary;
import java.util.List;

import net.mjahn.inspector.core.TrackedBundle;

public interface Reasoner {
	
	String REASONER_ANALYZE_THROWABLE = "net.mjahn.inspector.reasoner.analyze.throwable";
	String REASONER_ANALYZE_TASK_ID = "net.mjahn.inspector.reasoner.analyze.task.id";
	String REASONER_ANALYZE_BUNDLE = "net.mjahn.inspector.reasoner.analyze.bundle";
	String REASONER_ANALYZE_SERVICE = "net.mjahn.inspector.reasoner.analyze.service";
	String REASONER_ANALYZE_TYPE = "net.mjahn.inspector.reasoner.analyze.type";
	String REASONER_ANALYZE_RESULT_CONFIDENCE = "net.mjahn.inspector.reasoner.analyze.result.confidence";
	String REASONER_IGNORE = "ignore_this_reasoner";
	String REASONER_RESULT_NOT_APPLICABLE = "No clue what to do in this case! Try a different ReasonerTask.";
	String REASONER_ERROR_CODE_CORE_PREFIX = "MJA-CORE-";
	String REASONER_ERROR_CODE_CLASSNOTFOUND_PREFIX = "CNF-";
	String REASONER_ERROR_CODE_BUNDLEEXCEPTION_PREFIX = "BEX-";
	String REASONER_ERROR_CODE_NO_CLUE = REASONER_ERROR_CODE_CORE_PREFIX+"noclue";
	
	@SuppressWarnings("unchecked")
	List<ReasonerResult> analyze(TrackedBundle tb, Dictionary dict);
}
