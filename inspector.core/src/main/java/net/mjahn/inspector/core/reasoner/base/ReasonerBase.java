package net.mjahn.inspector.core.reasoner.base;

import net.mjahn.inspector.core.reasoner.Reasoner;

/**
 * Base implementation of a Reasoner with some convenient methods.
 * 
 * This bas implementation of the Reasoner should help you with 90% of the stuff you're doing.
 * Browse the methods and see what suits you best. Further versions will add more and more 
 * convenient methods on a backwards compatible level, so don't be afraid of sub classing this 
 * one.
 * 
 * @since 1.0
 * @version 1.0
 */
public abstract class ReasonerBase implements Reasoner {
	
	private final String name;
	public ReasonerBase(String reasonerName){
		name = reasonerName;
	}

	
	/**
	 * {@inheritDoc}
	 * @see net.mjahn.inspector.reasoner.Reasoner#getReasonerName()
	 * @since 1.0
	 */
	public String getReasonerName() {
		return name;
	}
	
}
