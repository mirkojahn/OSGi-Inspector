package net.mjahn.inspector.tutorial1.web.impl;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mjahn.inspector.core.FrameworkInspector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {
	
	private static BundleContext ctx;
	private static FrameworkInspector fwInspector = null;
	private ServiceTracker httpTracker;
	private String serviceName = "helloworld";
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ctx = context;
		httpTracker =
            new ServiceTracker(ctx, HttpService.class.getName(), null);
        httpTracker.open();
//		fwInspector = new FrameworkInspector();
		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		((HttpService)httpTracker.getService()).unregister(serviceName);
	}

	public static BundleContext getContext(){
		return ctx;
	}
	
	public static FrameworkInspector getFrameworkInspector(){
		return fwInspector;
	}

	public Object addingService(ServiceReference reference) {
		Hashtable<String,String> initparams = new Hashtable<String,String>(); 
		initparams.put( "name", "Hello World! - by OSGi Inspector" );
		javax.servlet.Servlet myServlet = new HttpServlet() { 
			String	name = "helloworld";
			public void init( ServletConfig config ) { this.name = (String)
				config.getInitParameter( "name" );
			}
			
			public void doGet( HttpServletRequest req, HttpServletResponse rsp) throws IOException { 
				rsp.setContentType( "text/plain" ); 
				rsp.getWriter().println( this.name );
			}
		};
		try {
			((HttpService)ctx.getService(reference)).registerServlet( serviceName, myServlet, initparams, null );
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// do nothing
	}

	public void removedService(ServiceReference reference, Object service) {
		// nothing todo		
	}
	
}
