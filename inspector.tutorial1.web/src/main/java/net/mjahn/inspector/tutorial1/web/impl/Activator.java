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
	
	private static FrameworkInspector fwInspector = null;
	private ServiceTracker httpTracker;
	private String serviceName = "/helloworld";
	private BundleContext ctx = null;
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ctx = context;
		httpTracker =
            new ServiceTracker(context, HttpService.class.getName(), this);
        httpTracker.open();
//		fwInspector = new FrameworkInspector();
		
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if(httpTracker != null){
			HttpService serv = ((HttpService)httpTracker.getService());
			if(serv != null){
				serv.unregister(serviceName);
			}
			httpTracker.close();
		}
	}
	
	public static FrameworkInspector getFrameworkInspector(){
		return fwInspector;
	}

	public Object addingService(ServiceReference reference) {
		Hashtable<String,String> initparams = new Hashtable<String,String>(); 
		initparams.put( "name", serviceName );
		javax.servlet.Servlet myServlet = new HttpServlet() { 
			private static final long serialVersionUID = -7398914113448648745L;
			@SuppressWarnings("unused")
			String	name = "/test";
			
			public void init( ServletConfig config ) { this.name = (String)
				config.getInitParameter( "name" );
			}
			
			public void doGet( HttpServletRequest req, HttpServletResponse rsp) throws IOException { 
				rsp.setContentType( "text/plain" ); 
				rsp.getWriter().println( "Hello World! - by OSGi Inspector" );
			}
		};
		HttpService http = null;
		try {
			http = ((HttpService)ctx.getService(reference));
			http.registerServlet( serviceName, myServlet, initparams, http.createDefaultHttpContext() );
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
