package net.mjahn.inspector.reasoner.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import net.mjahn.inspector.core.TrackedBundle;
import net.mjahn.inspector.reasoner.Reasoner;
import net.mjahn.inspector.reasoner.ReasonerResult;
import net.mjahn.inspector.reasoner.ReasonerTask;
import net.mjahn.inspector.reasoner.ReasonerTaskCapability;
import net.mjahn.inspector.reasoner.ReasonerUtil;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class BundleExceptionReasonerTask implements ReasonerTask {

	private static String ERROR_PREFIX = Reasoner.REASONER_ERROR_CODE_CORE_PREFIX
	+ Reasoner.REASONER_ERROR_CODE_BUNDLEEXCEPTION_PREFIX;
	public String getDiscription(Locale l) {
		return getDiscription();
	}

	public String getDiscription() {
		return "This Reasoner Task analyzes BundleExceptions and tries to figure out the root cause and steps to solve the core problem.";
	}
	
	public boolean requireBundle(){
		return false;
	}

	public List<ReasonerTaskCapability> requireDictionaryEntries(){
		ArrayList<ReasonerTaskCapability> list = new ArrayList<ReasonerTaskCapability>();
		list.add(new ReasonerTaskCapability(Reasoner.REASONER_ANALYZE_THROWABLE, "A BundleException to analyze.", true));
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public ReasonerResult analyze(TrackedBundle tb, Dictionary dict) {
		
		if(dict.get(Reasoner.REASONER_ANALYZE_THROWABLE)!=null 
				&& ( dict.get(Reasoner.REASONER_ANALYZE_THROWABLE) instanceof BundleException)){
			// ok, we can work on this one...
			Hashtable<String,Object> props = new Hashtable<String,Object>();
			// get the exception
			BundleException be = (BundleException) dict.get(Reasoner.REASONER_ANALYZE_THROWABLE);
			Throwable cause = be.getCause();
			// the type of the Exception
			int type = be.getType();
			// if it is a unspecified exception... it is not related to OSGi in general
			if(type == BundleException.ACTIVATOR_ERROR){
				
				// TODO: it might be a good idea to check ALL nested Exceptions as well...
				if(cause != null){
					// this is an exception we don't know how to handle... delegate
					// creating a delegate...
					props.put(Reasoner.REASONER_ANALYZE_THROWABLE, cause);
					// obtaining Reasoner results
					List<ReasonerResult> res = ReasonerUtil.reasonAboutThrowable(Activator.getReasoner(),cause,tb,this);
					if(res == null || res.isEmpty()){
						// nothing we can do. Return some low confidence result about what we know.
						return new DefaultReasonerResult(0.1f,"The BundleException doesn't provide a known/ handable cause and other reasoners don't have a clue either. ", ERROR_PREFIX + "00", null);
					} else {
						return new DefaultReasonerResult(0.1f,"The Unspecified BundleException was useful to other reasoners. See their result for more details.", ERROR_PREFIX + "01", res);
					}
				} else {
					// absolutely NO clue!
					return new DefaultReasonerResult(0.1f, "We have an ActivatorError BundleException without an attached Exception. No clue what that means.", ERROR_PREFIX + "02", null);
				}
			} else if(type == BundleException.RESOLVE_ERROR) {
				// first, check if it is a fragment
				// yes --> 
				//		is there a host to attach to? (first the bundle then, then the attachment rules, then conflicting manifest headers) --> error
				// no -->
				// 		is there a missing import?
				//		is there a problem with the attributes/directives of the import?
				return new DefaultReasonerResult(0.1f,"The given error is not yet handled by the reasoner: BundleExceptionType RESOLVE_ERROR ("+type+")", ERROR_PREFIX + "99", null);
			} else if(type == BundleException.DUPLICATE_BUNDLE_ERROR) {
				// there is just one reason why this could happen
				return new DefaultReasonerResult(1f,"The install or update operation failed because another already installed bundle has the same symbolic name and version. "+tb.getBundle().getSymbolicName()+" "+tb.getBundle().getVersion(), ERROR_PREFIX + "10", null);
			} else if(type == BundleException.INVALID_OPERATION) {
				return new DefaultReasonerResult(0.1f,"The given error is not yet handled by the reasoner: BundleExceptionType INVALID_OPERATION ("+type+") "+be.getMessage(), ERROR_PREFIX + "99", null);
			} else if(type == BundleException.MANIFEST_ERROR) {
				return new DefaultReasonerResult(0.1f,"The given error is not yet handled by the reasoner: BundleExceptionType MANIFEST_ERROR ("+type+")", ERROR_PREFIX + "99", null);
			} else if(type == BundleException.SECURITY_ERROR) {
				return new DefaultReasonerResult(1f,"An operation failed due to insufficient permissions. "+be.getMessage(), ERROR_PREFIX + "13", null);
			} else if(type == BundleException.START_TRANSIENT_ERROR) {
				return new DefaultReasonerResult(1f,"The start transient operation failed because the start level of the bundle is greater than the current framework start level. "+be.getMessage(), ERROR_PREFIX + "14", null);
			} else if(type == BundleException.STATECHANGE_ERROR) {
				return new DefaultReasonerResult(0.1f,"The given error is not yet handled by the reasoner: BundleExceptionType STATECHANGE_ERROR ("+type+")", ERROR_PREFIX + "99", null);
			} else if(type == BundleException.UNSUPPORTED_OPERATION) {
				return new DefaultReasonerResult(0.1f,"The given error is not yet handled by the reasoner: BundleExceptionType UNSUPPORTED_OPERATION ("+type+")", ERROR_PREFIX + "99", null);
			} else if(type == BundleException.NATIVECODE_ERROR) {
				String hd = "";
				try {
					Dictionary headerDict = tb.getBundle().getHeaders();
					hd = (String)headerDict.get(Constants.BUNDLE_NATIVECODE);
				} catch (Exception e){
					hd = "Couldn't resolve the native header.";
				}
				return new DefaultReasonerResult(1f,"The bundle could not be resolved due to an error with the Bundle-Native-Code header: "+hd, ERROR_PREFIX + "99", null);
			} else { 
				return new DefaultReasonerResult(0.1f,"The given error is not known by the reasoner: BundleExceptionType "+type, ERROR_PREFIX + "99", null);
			}
		}
		return new DefaultReasonerResult();
	}
}
