package net.mjahn.inspector.core.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.core.tools.impl.ComponentService;
import net.mjahn.inspector.core.tools.impl.EclipseCommands;
import net.mjahn.inspector.core.reasoner.Reasoner;
import net.mjahn.inspector.core.reasoner.ReasoningServiceProvider;
import net.mjahn.inspector.core.reasoner.service.ReasoningEngineImpl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator, SynchronousBundleListener, FrameworkListener {

    private static BundleContext ctx;
    private static FrameworkInspectorImpl fwInspector;
    private final static String equinoxCommandInterfaceName =
            "org.eclipse.osgi.framework.console.CommandProvider";
    ServiceRegistration myServRef = null;
    private static ServiceTracker rtTracker = null;
    private static ServiceTracker packageAdminTracker = null;
    private static ReasoningServiceProvider engine = null;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
//		System.out.println("Starting Bundle to inspect the OSGi Framework!");
        ctx = context;
        packageAdminTracker =
                new ServiceTracker(ctx, PackageAdmin.class.getName(), null);
        packageAdminTracker.open();

        // create the entry object used for all further investigation
        fwInspector = new FrameworkInspectorImpl();

        try {
            new R4FeatureInspector(ctx, fwInspector).addHooks();
        } catch (Throwable t) {
            // this might fail if not ran in an OSGi R4 container.
            // nothing to log or worry about!
        }
        Bundle[] bundles = ctx.getBundles();
        if (bundles != null) {
            for (int i = 0; i < bundles.length; i++) {
                fwInspector.getTrackedBundle(bundles[i].getBundleId());
            }
        }

        // create listener for newly installed bundles
        ctx.addBundleListener(this);

        // create listener for FrameworkEvents
        ctx.addFrameworkListener(this);

        // create a Tracker for Extensions with the Inspector instance
        // TODO: implement Extension mechanism

        // finally register the Inspector, so others can use it
        Hashtable<String, String> inspDict = new Hashtable<String, String>();
        inspDict.put("vendor", "net.mjahn");
//		System.out.println("registering service");

        ctx.registerService(FrameworkInspector.class.getName(), fwInspector, inspDict);

        // register the reasoning service
        rtTracker = new ServiceTracker(context, Reasoner.class.getName(), null);
        rtTracker.open();
        // register the ReasoningEngine
        engine = new ReasoningEngineImpl(rtTracker);
        ctx.registerService(ReasoningServiceProvider.class.getName(), engine, new Hashtable<String, String>());


        ComponentService.startComponentService(context);
        Dictionary<String, String> dict = new Hashtable<String, String>();
        dict.put("version", "1.0");
        // try Equinox
        try {
            getClass().getClassLoader().loadClass(equinoxCommandInterfaceName);
            context.registerService(equinoxCommandInterfaceName, new EclipseCommands(), dict);
            // well, I don't like console output... leave it away for now.
            // System.out.println("["
            // + context.getBundle().getSymbolicName()
            // + "] Equinox console found and command(s) registered.");
        } catch (ClassNotFoundException e) {
            // so, no Equinox console available... not much, we can do about it.
            // System.out.println("["
            // + context.getBundle().getSymbolicName() + "] no Equinox console extension found.");
        }
        // TODO: try felix next
    }

    /**
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        context.removeBundleListener(this);
        if (packageAdminTracker != null) {
            packageAdminTracker.close();
            packageAdminTracker = null;
        }

        if (rtTracker != null) {
            rtTracker.close();
        }
        rtTracker = null;
    }

    public static BundleContext getContext() {
        return ctx;
    }

    public void bundleChanged(BundleEvent bundleEvent) {

        if (bundleEvent.getType() == BundleEvent.UPDATED) {
            // get the bundle and initiate an update
            fwInspector.update(bundleEvent.getBundle().getBundleId());
        } else if (bundleEvent.getType() == BundleEvent.UNINSTALLED) {
            // remove the uninstalled bundle
            fwInspector.remove(bundleEvent.getBundle().getBundleId());
        } else if (bundleEvent.getType() == BundleEvent.RESOLVED) {
            // create a tracked bundle
//			System.out.println("bundle resolved... "+bundleEvent.getBundle().getSymbolicName());
            fwInspector.getTrackedBundle(bundleEvent.getBundle().getBundleId());
        } else {
//			System.out.println("### unknown bundle event: "+bundleEvent.getType()+" for bundle "+bundleEvent.getBundle().getSymbolicName());
        }

    }

    public static FrameworkInspectorImpl getFrameworkInspectorImpl() {
        return fwInspector;
    }

    public static PackageAdmin getPackageAdmin() {
        return (PackageAdmin) packageAdminTracker.getService();
    }

    public void frameworkEvent(FrameworkEvent event) {
        fwInspector.addFrameworkEvent(event);
    }

    public static ReasoningServiceProvider getReasoningServiceProvider(){
        return engine;
    }

}
