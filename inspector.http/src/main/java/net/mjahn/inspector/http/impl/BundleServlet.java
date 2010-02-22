package net.mjahn.inspector.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BundleServlet extends HttpServlet {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7653896054054421890L;
	
	public void doGet( HttpServletRequest req, HttpServletResponse rsp) throws IOException { 
		rsp.setContentType( "text/html" ); 
//		try {
//			Thread.currentThread().wait(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		URL indexUrl = Activator.getBundleContext().getBundle().getEntry("/inspector-bundles");
		URLConnection connection = indexUrl.openConnection();
		InputStream ins = connection.getInputStream();
		OutputStream out = rsp.getOutputStream();
		byte[] buf = new byte[2048];
		int rd;
		while ((rd = ins.read(buf)) >= 0) {
			out.write(buf, 0, rd);
		}
	}
}
