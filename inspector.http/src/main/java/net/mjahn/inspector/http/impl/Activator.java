package net.mjahn.inspector.http.impl;

import java.util.Hashtable;

import javax.servlet.ServletException;

import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.reasoner.Reasoner;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	private ServiceTracker httpTracker;
	private static ServiceTracker fwTracker;
	private static ServiceTracker rsTracker;
	private String serviceName = "/inspector";
	private static BundleContext ctx = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ctx = context;
		httpTracker = new ServiceTracker(context, HttpService.class.getName(),
				this);
		httpTracker.open();
		fwTracker = new ServiceTracker(context, FrameworkInspector.class.getName(), null);
		fwTracker.open();
		rsTracker = new ServiceTracker(context, Reasoner.class.getName(), null);
		rsTracker.open();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (httpTracker != null) {
			HttpService serv = ((HttpService) httpTracker.getService());
			if (serv != null) {
				serv.unregister(serviceName);
			}
			httpTracker.close();
		}
	}

	public static FrameworkInspector getFrameworkInspector() {
		return (FrameworkInspector)fwTracker.getService();
	}
	
	public static Reasoner getReasoner() {
		return (Reasoner)rsTracker.getService();
	}
	
	public static BundleContext getBundleContext(){
		return ctx;
	}

	public Object addingService(ServiceReference reference) {
		Hashtable<String, String> initparams = new Hashtable<String, String>();
		initparams.put("name", serviceName);
		HttpService http = null;
		try {
			http = ((HttpService) ctx.getService(reference));
			HttpContext context =http.createDefaultHttpContext();
			http.registerServlet(serviceName, new OSGiRuntimeServlet(), initparams, context);
			Hashtable<String, String> initparams2 = new Hashtable<String, String>();
			initparams2.put("name", "/inspector-bundles");
			Hashtable<String, String> initparams3 = new Hashtable<String, String>();
			initparams3.put("name", "/inspector-fw-errorevents");
			http.registerServlet("/inspector-bundles", new BundleServlet(), initparams2,context);
			http.registerServlet("/inspector-fw-errorevents", new FrameworkEventErrorServlet(), initparams2,context);
			http.registerResources("/inspector/res", "/inspector/res", context);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}

		return http;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// do nothing
	}

	public void removedService(ServiceReference reference, Object service) {
		// nothing todo
	}

}
