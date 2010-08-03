package net.mjahn.inspector.core.reasoner.base;

import java.util.Hashtable;
import java.util.UUID;

import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ModifyableJobDescription;

public class JobDescriptionBase implements ModifyableJobDescription {

    private JobDescription parentJobDesc = null;
    private Hashtable<String, String> jobProperties = new Hashtable<String, String>();
    private final UUID id;
    private TrackedBundle tb;
    private String serviceRequestTA;
    private Throwable throwableTA;
    private Object objTA;

    public JobDescriptionBase(JobDescription parentJobDescription) {
        id = parentJobDescription.getTaskId();
        parentJobDesc = parentJobDescription;
        tb = parentJobDescription.getSourceBundle();
        serviceRequestTA = parentJobDescription.getServiceRequestToAnalyze();
        throwableTA = parentJobDescription.getThrowable();
        objTA = parentJobDescription.getObjectToAnalyze();
        jobProperties = (Hashtable<String, String>) parentJobDescription.getJobDetails().clone();
    }

    /**
     * Do NOT use this constructor unless you exactly know what you're doing!!!
     *
     * @param jobDetails the base JobDescription to start with
     * @param taskId the task id already provided by a former job
     * @param trackedBundle the bundle where the incident to analyze happened.
     */
    public JobDescriptionBase(Hashtable<String, String> jobDetails,
            TrackedBundle trackedBundle,
            Object objectToAnalyze,
            String serviceRequestToAnalyze,
            Throwable throwableToAnalyze) {
        id = UUID.randomUUID();
        jobProperties = jobDetails;
        if(jobDetails == null){
            jobProperties = new Hashtable<String, String>();
        }
        tb = trackedBundle;
        objTA = objectToAnalyze;
        serviceRequestTA = serviceRequestToAnalyze;
        throwableTA = throwableToAnalyze;
    }

    public Hashtable<String, String> getJobDetails() {
        //return (Hashtable<String,String>)java.util.Collections.unmodifiableMap(jobProperties);
        return jobProperties;
    }

    public TrackedBundle getSourceBundle() {
        return tb;
    }

    public UUID getTaskId() {
        return id;
    }

    public void setSourceBundle(TrackedBundle trackedBundle) {
        tb = trackedBundle;
    }

    public JobDescription getParent() {
        return parentJobDesc;
    }

    public void setParent(JobDescription parentJobDescription) {
        parentJobDesc = parentJobDescription;
    }

    public void setThrowableToAnalyze(Throwable t) {
        throwableTA = t;
    }

    public void setObjectToAnalyze(Object o) {
        objTA = o;
    }

    public void setServiceRequestToAnalyze(String request) {
        serviceRequestTA = request;
    }

    public Throwable getThrowable() {
        return throwableTA;
    }

    public Object getObjectToAnalyze() {
        return objTA;
    }

    public String getServiceRequestToAnalyze() {
        return serviceRequestTA;
    }

}
