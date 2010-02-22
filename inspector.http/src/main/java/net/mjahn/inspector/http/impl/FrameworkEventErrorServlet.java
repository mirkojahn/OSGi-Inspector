package net.mjahn.inspector.http.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mjahn.inspector.reasoner.ReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerUtil;

import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

public class FrameworkEventErrorServlet extends HttpServlet {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 8369479460214293778L;
	private static final String HEADER = "				<div id=\"fw-error-events\" class=\"ui-widget\">" + 
"					<table id=\"error-events\" class=\"ui-widget ui-widget-content\">" + 
"						<thead>" + 
"							<tr class=\"ui-widget-header \">" +  
"								<th>Type</th>" + 
"								<th>BundleID</th>" + 
"								<th>Bundle-SymbolicName</th>" + 
"								<th>Problem </th>" + 
"							</tr>" + 
"						</thead>" + 
"						<tbody>";
	private static final String FOOTER = "						</tbody>" + 
"					</table>" + 
"				</div>";
	
	String	name = "inspector-fw-errorevents";
	
	public void init( ServletConfig config ) { this.name = (String)
		config.getInitParameter( "name" );
	}
	
	public void doGet( HttpServletRequest req, HttpServletResponse rsp) throws IOException { 
		rsp.setContentType( "text/html" ); 
		PrintWriter out = rsp.getWriter();
		List<FrameworkEvent> fwEvents = Activator.getFrameworkInspector().getErrorFrameworkEvents();
		if(fwEvents.size() == 0){
			out.print("<h2>No Error detected</h2>");
		} else {
			out.print("<h2>Captured Runtime Errors</h2><br/>");
			out.print(HEADER);
			Iterator<FrameworkEvent> iter = fwEvents.iterator();
			while(iter.hasNext()){
				FrameworkEvent e = iter.next();
				List<ReasonerResult> results = ReasonerUtil.reasonAboutThrowable(Activator.getReasoner(), e.getThrowable(), Activator.getFrameworkInspector().getTrackedBundle(e.getBundle().getBundleId()), null);
				ReasonerResult result = ReasonerUtil.getHighestPriorityResult(results);
				if(result != null){
					out.print(getRow(e.getBundle().getBundleId(), getErrorTypeString(((BundleException)e.getThrowable()).getType()), e.getBundle().getSymbolicName(), e.getBundle().getVersion().toString(), result.getErrorConfidence(), result.getResultMessage()));
				} else {
					out.print(getRow(e.getBundle().getBundleId(), "", e.getBundle().getSymbolicName(), e.getBundle().getVersion().toString(), 0, "no reasoner for this type available yet."));
				}
			}
			out.print(FOOTER);
		}
	}
	
	private String getErrorTypeString(int type){
		switch (type) {
		case BundleException.ACTIVATOR_ERROR:
			return "ACTIVATOR_ERROR";
		case BundleException.DUPLICATE_BUNDLE_ERROR:
			return "DUPLICATE_BUNDLE_ERROR";
		case BundleException.INVALID_OPERATION:
			return "INVALID_OPERATION";
		case BundleException.MANIFEST_ERROR:
			return "MANIFEST_ERROR";
		case BundleException.NATIVECODE_ERROR:
			return "NATIVECODE_ERROR";
		case BundleException.RESOLVE_ERROR:
			return "RESOLVE_ERROR";
		case BundleException.SECURITY_ERROR:
			return "SECURITY_ERROR";
		case BundleException.START_TRANSIENT_ERROR:
			return "START_TRANSIENT_ERROR";
		case BundleException.STATECHANGE_ERROR:
			return "STATECHANGE_ERROR";
		case BundleException.UNSPECIFIED:
			return "UNSPECIFIED";
		case BundleException.UNSUPPORTED_OPERATION:
			return "UNSUPPORTED_OPERATION";
		default:
			return "unknown bundle exception type: "+type;
		}
	}
	
	private String getRow(long bId, String errorType, String bundleName, String bundleVersion, float confidence, String analysis){
		StringBuffer sb = new StringBuffer();
		sb.append("<tr><td>");
		sb.append(errorType);
		sb.append("</td><td>");
		sb.append(bId);
		sb.append("</td><td>");
		sb.append(bundleName + " version: "+ bundleVersion);
		sb.append("</td><td>");
		sb.append("<b>Confidence: ");
		sb.append(confidence + "</b><br/>"+ analysis);
		sb.append("</td><tr>");
		return sb.toString();
//		
//		String row = "							<tr>" + 
//		"								<td>" +
//		bId +
//		"</td>" + 
//		"								<td>" +
//		errorType +
//		"</td>" + 
//		"								<td>" +
//		bundleName + " version: "+ bundleVersion +
//		"</td>" + 
//		"								<td><b>Confidence: " +
//		confidence + "<b><br/>"+ analysis +
//		"</td>" + 
//		"							</tr>";
//		return row;
	}
}
