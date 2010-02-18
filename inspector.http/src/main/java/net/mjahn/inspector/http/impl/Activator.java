package net.mjahn.inspector.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	private static FrameworkInspector fwInspector = null;
	private ServiceTracker httpTracker;
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
		// fwInspector = new FrameworkInspector();

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
		return fwInspector;
	}
	
	public static BundleContext getBundleContext(){
		return ctx;
	}

	public Object addingService(ServiceReference reference) {
		Hashtable<String, String> initparams = new Hashtable<String, String>();
		initparams.put("name", serviceName);
		javax.servlet.Servlet myServlet = new HttpServlet() {
			private static final long serialVersionUID = -7398914113448648745L;
			@SuppressWarnings("unused")
			String name = "/inspector";
			String content = null;

			public void init(ServletConfig config) {
				this.name = (String) config.getInitParameter("name");
			}

			public void doGet(HttpServletRequest req, HttpServletResponse rsp)
					throws IOException {
				rsp.setContentType("text/html");
				rsp.setCharacterEncoding( "UTF-8" );
				if(req.getRequestURI().startsWith("/inspector/")){
					// redirect
					URL indexUrl = ctx.getBundle().getEntry("/redirect.html");
					URLConnection connection = indexUrl.openConnection();
					InputStream ins = connection.getInputStream();
					OutputStream out = rsp.getOutputStream();
					byte[] buf = new byte[2048];
					int rd;
					while ((rd = ins.read(buf)) >= 0) {
						out.write(buf, 0, rd);
					}
					return;
				}
				synchronized(this){
					if(content == null){
						URL indexUrl = ctx.getBundle().getEntry("/index.html");
						URLConnection connection = indexUrl.openConnection();
						InputStream ins = connection.getInputStream();
						OutputStream out = new ByteArrayOutputStream();//rsp.getOutputStream();
						byte[] buf = new byte[2048];
						int rd;
						while ((rd = ins.read(buf)) >= 0) {
							out.write(buf, 0, rd);
						}
						content = out.toString();
						out.close();
					}
				}
				String output = content;
//				if(req.getRequestURI().startsWith("/inspector/")){
//					// make the resources absolute
//					output = output.replaceAll("inspector\\/res\\/", "\\/inspector\\/res\\/");
//				}
				rsp.getWriter().print(output);
			}
		};
		HttpService http = null;
		try {
			http = ((HttpService) ctx.getService(reference));
			HttpContext context =http.createDefaultHttpContext();
			http.registerServlet(serviceName, myServlet, initparams, context);
			Hashtable<String, String> initparams2 = new Hashtable<String, String>();
			initparams2.put("name", "/inspector-bundles");
			http.registerServlet("/inspector-bundles", new BundleServlet(), initparams2,context);
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
