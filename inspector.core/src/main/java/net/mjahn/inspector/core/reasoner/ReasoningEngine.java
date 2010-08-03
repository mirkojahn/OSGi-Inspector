package net.mjahn.inspector.core.reasoner;

import net.mjahn.inspector.core.InvalidInvocationException;
import net.mjahn.inspector.core.TrackedBundle;

/**
 * Engine provided for creating sub-reasoning.
 * 
 * When starting a reasoning job, it might be necessary to start further
 * reasoning tasks with tweaked or enhanced input, so other Reasoners can
 * do their piece of work. Just provide alternative values to the properties 
 * you want to manipulate when you execute the 
 * {@link ReasoningEngine#reason(Hashtable)} method. The underlying engine 
 * implementation will take care of creating the appropriate JobDescription 
 * and call the respective reasoners. From a reasoner perspective this call 
 * will be no different than any other call, but will contain additional 
 * history information of the reasoners run before.
 * 
 * @version 1.0
 * @since 1.0
 */
public interface ReasoningEngine {
	
	/**
	 * Initiate a (potentially subsequent) reasoning run/ job.
	 * 
	 * @param desc a JobDescription of a reasoning task (mandatory)
         * @param resultCompiler the holder for previous results, that might be empty (mandatory)
         * @return The result object for this reasoning run (most likely of type {@code ReasoningResultCompiler}.
	 * @throws InvalidInvocationException in case the mandatory fields are not set properly.
         * @since 1.0
	 */
	ReasonerResult reason(JobDescription desc, ReasonerResultCompiler resultCompiler) throws InvalidInvocationException;
        

        ModifyableJobDescription createChildJobDescription(JobDescription desc) throws InvalidInvocationException;
	
}
