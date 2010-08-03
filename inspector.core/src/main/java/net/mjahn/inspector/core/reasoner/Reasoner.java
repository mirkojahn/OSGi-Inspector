package net.mjahn.inspector.core.reasoner;


/**
 * Reasoner is the core interface of the analytics figuring out what went wrong.
 * 
 * <p>A Reasoner is the Interface to implement if you intent to create your own reasoning
 * logic to why a certain error case happened. Every Reasoner is considered to be 
 * stateless and ThreadSafe. Your best choice in implementing a Reasoner is starting
 * with on in the net.mjahn.inspector.reasoner.base package and looking at one of the
 * core Reasoners provided by the bundle, such as the ClassNotFoundReasoner, 
 * BundleReasoner or ServiceReasoner.</p>
 *
 * <p>Reasoner are by design OSGi Services Exposed by this interface. The inspector.reasoner
 * bundle will track them and execute them as needed. There are no assumptions made on the
 * context or number of calls the reasoner might get. The only guaranteed constraint is
 * that a reasoner gets called only once upon a request. However, a reasoner can make its
 * own reasoning by calling the ReasoningEngine provided. However, if doing so, the
 * reasoner has to make sure, it is not creating a loop by getting called a second time or
 * by subsequent indirect calls derived from the initial call. For instance, your Reasoner
 * might create a new Resoning job, that triggers again another Reasoning job, executing the 
 * initial (your Reasoner) with the same arguments, resulting in a infinite loop. As a tool
 * to handle these kinds of problems, each Job has a unique task id and a job history, 
 * containing a list with all Reasoner previously executed. Of course, you could base your
 * reasoning on how to prevent endless loops on your own measurements. However, the task of
 * preventing such scenarios is totally in your responsibility!</p>
 * 
 */
public interface Reasoner {
	
		
	String REASONER_ANALYZE_RESULT_CONFIDENCE = "net.mjahn.inspector.reasoner.analyze.result.confidence";
	
	/**
	 * Provides a human readable name.
	 * 
	 * The name provide should give the user of the reasoner a hint, what it actually does. The name
	 * does not have to be unique or adhere to any semantics. The name however should be compromised
	 * from the English language to give users from various nationalities a chance of understanding
	 * its purpose. The name will never be localized!
	 * 
	 * @return a string representing the reasoner name.
	 */
	String getReasonerName();
	
	/**
	 * The method which does the number crunching and reasoning
	 * 
	 * This method contains all the logic required to complete its task. The properties given are the
	 * configuration which drives the analysis. An implementer has first to check if all required properties
	 * are set before doing any real work, because the caller doesn't know if this particular Reasoner is
	 * a fit for the given problem!
	 * 
	 * @param desc the Job description
	 * @param engine the engine to make subsequent reasoning calls
         * @param resultCompiler an aggregator gathering results for this analysis run.
	 * @return
	 */
	ReasonerResult analyze(JobDescription desc, ReasoningEngine engine, ReasonerResultCompiler resultCompiler);

}
