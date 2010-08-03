package net.mjahn.inspector.core.reasoner;

import java.util.Hashtable;
import java.util.UUID;

import net.mjahn.inspector.core.TrackedBundle;

public interface JobDescription {

    UUID getTaskId();

    JobDescription getParent();

    TrackedBundle getSourceBundle();

    Hashtable<String, String> getJobDetails();

    Throwable getThrowable();

    Object getObjectToAnalyze();

    String getServiceRequestToAnalyze();

}
