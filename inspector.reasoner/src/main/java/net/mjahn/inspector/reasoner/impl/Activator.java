package net.mjahn.inspector.reasoner.impl;

import java.util.Hashtable;

import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.core.reasoner.Reasoner;
import net.mjahn.inspector.reasoner.analytics.AnalyzeWrappedExceptionsReasoner;
import net.mjahn.inspector.reasoner.analytics.BundleExceptionReasoner;
import net.mjahn.inspector.reasoner.analytics.BundleReasoner;
import net.mjahn.inspector.reasoner.analytics.ClassNotFoundReasoner;
import net.mjahn.inspector.reasoner.analytics.ServiceNotFoundReasoner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private static BundleContext ctx;
    private static ServiceTracker fwTracker = null;
    private static ServiceTracker paTracker = null;

    /**
     * {@inheritDoc}
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        ctx = context;
        // some important services we might need
        fwTracker = new ServiceTracker(context, FrameworkInspector.class.getName(), null);
        fwTracker.open();

        paTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        paTracker.open();

        // register the ReasonerTasks as services
        ctx.registerService(Reasoner.class.getName(), new ClassNotFoundReasoner(), new Hashtable<String, String>());
        ctx.registerService(Reasoner.class.getName(), new BundleReasoner(), new Hashtable<String, String>());
        ctx.registerService(Reasoner.class.getName(), new ServiceNotFoundReasoner(), new Hashtable<String, String>());
        ctx.registerService(Reasoner.class.getName(), new BundleExceptionReasoner(), new Hashtable<String, String>());
        ctx.registerService(Reasoner.class.getName(), new AnalyzeWrappedExceptionsReasoner(), new Hashtable<String, String>());
    }

    /**
     * {@inheritDoc}
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        // some minor clean-up
        if (fwTracker != null) {
            fwTracker.close();
        }
        fwTracker = null;

        if (paTracker != null) {
            paTracker.close();
        }
        paTracker = null;


    }

    public static PackageAdmin getPackageAdmin() {
        return (PackageAdmin) paTracker.getService();
    }

    public static FrameworkInspector getFrameworkInspector() {
        return (FrameworkInspector) fwTracker.getService();
    }

    public static BundleContext getBundleContext() {
        return ctx;
    }
}
