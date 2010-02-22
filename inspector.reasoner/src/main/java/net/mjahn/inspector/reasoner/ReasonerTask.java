package net.mjahn.inspector.reasoner;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import net.mjahn.inspector.core.TrackedBundle;

public interface ReasonerTask {
	
	/**
	 * FIXME: write some extensive documentation once the proof of concept works!
	 * 
	 * @since 1.0
	 * @param tb the bundle causing the problem or null if unknown or not related to a specific bundle.
	 * @param dict a Dictionary with more details (Reasoner specific data) about the task it is guaranteed to never be null.
	 * @return a result having an actual solution (with a confidence rate!). NOT null!!!
	 */
	@SuppressWarnings("unchecked")
	ReasonerResult analyze(TrackedBundle tb, Dictionary dict);
	
	String getDiscription(Locale l);
	
	String getDiscription();
	
	public List<ReasonerTaskCapability> requireDictionaryEntries();
}
