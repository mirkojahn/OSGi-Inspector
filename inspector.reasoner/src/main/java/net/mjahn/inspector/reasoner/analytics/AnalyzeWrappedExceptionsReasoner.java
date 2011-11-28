package net.mjahn.inspector.reasoner.analytics;

import net.mjahn.inspector.core.InvalidInvocationException;
import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ModifyableJobDescription;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.ReasoningEngine;
import net.mjahn.inspector.core.reasoner.base.NoClueReasonerResult;
import net.mjahn.inspector.core.reasoner.base.ReasonerBase;

public class AnalyzeWrappedExceptionsReasoner extends ReasonerBase {

	public AnalyzeWrappedExceptionsReasoner() {
        super("AnalyzeWrappedExceptions Reasoner");
    }
	@Override
	public ReasonerResult analyze(JobDescription desc, ReasoningEngine engine,
			ReasonerResultCompiler resultCompiler) {
		if(desc != null && desc.getThrowable() != null){
			// we're having an exception! Great!
			Throwable t = desc.getThrowable().getCause();
			if(t != null){
				// re-check this exception
				try {
					// create a new job description with the new exception
					// FIXME: make sure with a uuid or so that a jobdescripton can't be reused (maybe add different parameters to the create new child... like new throwable)
					ModifyableJobDescription jobDesc = engine.createChildJobDescription(desc);
					jobDesc.setThrowableToAnalyze(t);
					
					ReasonerResult result = engine.reason(jobDesc, resultCompiler);
					// check if we found a reasonable result
					if (result != null && result.getConfidenceLevel()>0){
						// return the found result
						return result;
					}
				} catch (InvalidInvocationException e) {
					e.printStackTrace();
				}
			}
		}
		return new NoClueReasonerResult();
	}

}
