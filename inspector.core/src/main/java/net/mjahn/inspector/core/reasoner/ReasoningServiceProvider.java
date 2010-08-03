package net.mjahn.inspector.core.reasoner;

import net.mjahn.inspector.core.InvalidInvocationException;


/**
 * Service interface to obtain when starting a reasoning Job.
 * 
 * TODO: explain how it works, mention helpers and the lib you need 
 * for basic reasoning services.
 * 
 * @version 1.0
 * @since 1.0
 */
public interface ReasoningServiceProvider extends ReasoningEngine {
	
    /**
     * Initiate a reasoning run/ job. 
     *
     * @param desc a JobDescription of a reasoning task (mandatory)
     * @return The result object for this reasoning run (most likely of type {@code ReasoningResultCompiler}.
     * @throws InvalidInvocationException in case the mandatory fields are not set properly.
     * @since 1.0
     */
    ReasonerResult reason(JobDescription desc) throws InvalidInvocationException;
}
