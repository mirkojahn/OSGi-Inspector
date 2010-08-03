package net.mjahn.inspector.core.reasoner;

import java.util.List;

public interface ReasonerResultCompiler extends ReasonerResult {
	
	void add(ReasonerResult result);
	
	List<ReasonerResult> getResults();

}
