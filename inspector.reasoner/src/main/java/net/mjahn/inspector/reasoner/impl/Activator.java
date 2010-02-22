package net.mjahn.inspector.reasoner.impl;

import java.util.Hashtable;

import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.reasoner.Reasoner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;

import net.mjahn.inspector.reasoner.ReasonerTask;

public class Activator implements BundleActivator {
	
	private static BundleContext ctx;
	private static ServiceTracker fwTracker = null;
	private static ServiceTracker paTracker = null;
	private static ServiceTracker slTracker = null;
	private static ServiceTracker rtTracker = null;
	private static Reasoner reasoner = null;
	
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
		
		slTracker = new ServiceTracker(context, StartLevel.class.getName(), null);
		slTracker.open();
		
		rtTracker = new ServiceTracker(context, ReasonerTask.class.getName(), null);
		rtTracker.open();
		
		// register the Reasoner
		reasoner = new ReasonerCore(rtTracker);
		ctx.registerService(Reasoner.class.getName(), reasoner, new Hashtable<String,String>());
		// register the ReasonerTasks as services
		ctx.registerService(ReasonerTask.class.getName(), new ClassNotFoundReasonerTask(), new Hashtable<String,String>());
//		ctx.registerService(ReasonerTask.class.getName(), new BundleReasonerTask(), new Hashtable<String,String>());
		ctx.registerService(ReasonerTask.class.getName(), new BundleExceptionReasonerTask(), new Hashtable<String,String>());
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// some minor clean-up
		if(fwTracker != null){
			fwTracker.close();
		}
		fwTracker = null;
		
		if(paTracker != null){
			paTracker.close();
		}
		paTracker = null;
		
		if(slTracker != null){
			slTracker.close();
		}
		slTracker = null;
	}
	
	public static PackageAdmin getPackageAdmin(){
		return (PackageAdmin) paTracker.getService();
	}
	
	public static FrameworkInspector getFrameworkInspector(){
		return (FrameworkInspector) fwTracker.getService();
	}
	
	public static StartLevel getStartLevelService(){
		return (StartLevel) slTracker.getService();
	}
	
	public static BundleContext getBundleContext(){
		return ctx;
	}
	
	public static Reasoner getReasoner(){
		return reasoner;
	}
	
}
