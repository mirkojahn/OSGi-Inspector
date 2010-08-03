/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mjahn.inspector.core.reasoner;

import net.mjahn.inspector.core.TrackedBundle;

/**
 *
 * @author mjahn
 */
public interface ModifyableJobDescription extends JobDescription {


    void setParent(JobDescription parentJobDescription);

    void setSourceBundle(TrackedBundle tb);

    void setThrowableToAnalyze(Throwable t);

    void setObjectToAnalyze(Object o);

    void setServiceRequestToAnalyze(String request);


}
