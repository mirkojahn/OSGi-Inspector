/**
 * 
 */
package net.mjahn.inspector.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mjahn.inspector.core.OSGiRuntimeInfo;
import net.mjahn.inspector.core.ExportedPackage;

final class OSGiRuntimeServlet extends HttpServlet {
		private static final long serialVersionUID = -7398914113448648745L;
		String content = null;

		public void doGet(HttpServletRequest req, HttpServletResponse rsp)
				throws IOException {
			rsp.setContentType("text/html");
			rsp.setCharacterEncoding( "UTF-8" );
			if(req.getRequestURI().startsWith("/inspector/")){
				// redirect
				URL indexUrl = Activator.getBundleContext().getBundle().getEntry("/redirect.html");
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
					URL indexUrl = Activator.getBundleContext().getBundle().getEntry("/index.html");
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
			String output = content.replace("##content##", analyzeRuntime(Activator.getFrameworkInspector().getRuntimeInfo()));
			rsp.getWriter().print(output);
		}
		
		private String analyzeRuntime(OSGiRuntimeInfo info){
			StringBuffer sb = new StringBuffer();
			sb.append("<p>");
			
			sb.append("Runtime Vendor: ");
			sb.append(info.getFrameworkVendor());
			sb.append("<br />");
			
			sb.append("Implemented OSGi Specification Version: ");
			sb.append(info.getOSGiSpecificationVersion());
			sb.append("<br />");
			
			sb.append("Framework Operating System: ");
			sb.append(info.getFrameworkOsName());
			sb.append(" ");
			sb.append(info.getFrameworkOsVersion());
			sb.append("<br />");
			
			
			sb.append("<br /><b>Supports:</b><br /><br />");
			
			sb.append("Execution Environments: ");
			Iterator<String> seeIter = Arrays.asList(info.getSupportedExecutionEnvironments()).iterator();
			while(seeIter.hasNext()){
				sb.append(seeIter.next());
				if(seeIter.hasNext()){
					sb.append(", ");
				}
			}
			sb.append("<br />");
			
			sb.append("Fragments: ");
			sb.append(info.supportsFragmets());
			sb.append("<br />");
			
			sb.append("Required Bundles: ");
			sb.append(info.supportsRequireBundle());
			sb.append("<br />");
			
			sb.append("Boot Classpath Extensions: ");
			sb.append(info.supportsBootClassPathExtension());
			sb.append("<br />");
			
			sb.append("Framework Extensions: ");
			sb.append(info.supportsFrameworkExtensions());
			sb.append("<br />");
			
			sb.append("<br /><b>Provides:</b><br /><br />");
			sb.append("Bootdelegation Definitions: ");
			Iterator<ExportedPackage> bdIter = info.getBootDelegationPackages().iterator();
			if(bdIter.hasNext()){
				sb.append("<ul>");
				while(bdIter.hasNext()){
					sb.append("<li>");
					ExportedPackage exp = bdIter.next();
					sb.append(exp.getPackageName());
					sb.append(" version=");
					sb.append(exp.getVersion());
					sb.append("</li>");
				}
				sb.append("</ul>");
			} else {
				sb.append("none<br />");
			}
			
			sb.append("System Packages: ");
			Iterator<ExportedPackage> spIter = info.getSystemPackages().iterator();
			if(spIter.hasNext()){
				sb.append("<ul>");
				while(spIter.hasNext()){
					sb.append("<li>");
					ExportedPackage exp = spIter.next();
					sb.append(exp.getPackageName());
					sb.append(" version=");
					sb.append(exp.getVersion());
					sb.append("</li>");
				}
				sb.append("</ul>");
			} else {
				sb.append("none<br />");
			}
			
			sb.append("</p>");
			return sb.toString();
		}
	}