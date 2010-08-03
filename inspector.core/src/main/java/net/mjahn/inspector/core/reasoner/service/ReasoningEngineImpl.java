package net.mjahn.inspector.core.reasoner.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.mjahn.inspector.core.InvalidInvocationException;

import net.mjahn.inspector.core.reasoner.JobDescription;
import net.mjahn.inspector.core.reasoner.ModifyableJobDescription;
import net.mjahn.inspector.core.reasoner.Reasoner;
import net.mjahn.inspector.core.reasoner.ReasonerResult;
import net.mjahn.inspector.core.reasoner.ReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.ReasoningServiceProvider;
import net.mjahn.inspector.core.reasoner.base.DefaultReasonerResultCompiler;
import net.mjahn.inspector.core.reasoner.base.JobDescriptionBase;
import net.mjahn.inspector.core.reasoner.base.NoClueReasonerResult;

import org.osgi.util.tracker.ServiceTracker;

public class ReasoningEngineImpl implements ReasoningServiceProvider {

    private ServiceTracker reasonerTracker;

    public ReasoningEngineImpl(ServiceTracker st) {
        reasonerTracker = st;
    }

    public ReasonerResult reason(JobDescription desc, ReasonerResultCompiler resComp) throws InvalidInvocationException{
        if(desc == null){
            throw new InvalidInvocationException("A valid JobDescription has to be provided. Null is NOT sufficient.");
        }
        Iterator<Reasoner> reasoners = getReasoners().iterator();
        if (resComp == null) {
            resComp = new DefaultReasonerResultCompiler();
        }
        if(!reasoners.hasNext()){
            // NO reasoners found! Return no clue!
            return new NoClueReasonerResult();
        }
        while (reasoners.hasNext()) {
            Reasoner reasoner = reasoners.next();
            try{
                ReasonerResult result = reasoner.analyze(desc, this, resComp);
                if (result.isConfident()) {
                    resComp.add(result);
                }
            } catch (Throwable t) {
                // something went wrong log this?
            }
        }
        return resComp;
    }

    private List<Reasoner> getReasoners() {
        Object[] services = reasonerTracker.getServices();
        ArrayList<Reasoner> list = new ArrayList<Reasoner>();
        if (services == null || services.length == 0) {
            return list;
        }
        for (int i = 0; i < services.length; i++) {
            list.add((Reasoner) services[i]);
        }
        return list;
    }

    public ModifyableJobDescription createChildJobDescription(JobDescription desc) {
        return new JobDescriptionBase(desc);
    }

    public ReasonerResult reason(JobDescription desc) throws InvalidInvocationException{
        return reason(desc, new DefaultReasonerResultCompiler());
    }
}
