package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.NotFoundServiceCall;
import net.mjahn.inspector.core.TrackedBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;

public class TrackedBundleImpl implements TrackedBundle {
	private final long bundleId;
	
	private ArrayList<ListenerInfo> listeningForService = new ArrayList<ListenerInfo>();
	private ArrayList<ListenerInfo> notListeningForService = new ArrayList<ListenerInfo>();
	private ArrayList<NotFoundServiceCall> notFoundServiceCalls = new ArrayList<NotFoundServiceCall>();
	private ArrayList<ExportedPackage> exportedPackages = null;
	private ArrayList<ImportedPackage> importedPackages = null;
	
	private Object guard = new Object();
	private Object guardImp = new Object();
	private Object guardExp = new Object();
	
	public TrackedBundleImpl(long id) {
		bundleId = id;
	}
	
	public Bundle getBundle(){
		Bundle b = null;
		try{
			b = Activator.getContext().getBundle(bundleId);
		} catch (Exception e){
			return null;
		}
		return b;
	}
	
	public void addedListenerForService(ListenerInfo info){
		if(info != null){
			synchronized(guard){
				notListeningForService.remove(info);
				listeningForService.add(info);
			}
		}
	}
	
	public List<ListenerInfo> getAllAddedServicesListens(){
		return listeningForService;
	}

	public void removedListenerForService(ListenerInfo info){
		if(info != null){
			synchronized(guard) {
				listeningForService.remove(info);
				notListeningForService.add(info);
			}
		}
	}
	
	public List<ListenerInfo> getAllRemovedServicesListens(){
		return notListeningForService;
	}
	
	public void addNotFoundServiceCall(NotFoundServiceCall serviceCall){
		notFoundServiceCalls.add(serviceCall);
	}
	
	public List<NotFoundServiceCall> getAllNotFoundServiceCalls(){
		return notFoundServiceCalls;
	}
	
	@SuppressWarnings("unchecked")
	public List<ImportedPackage> getImportedPackages(){
		// lazy parsing
		synchronized(guardImp){
			if(importedPackages != null){
				return importedPackages;
			}
			importedPackages = new ArrayList<ImportedPackage>();
			Dictionary headers = getBundle().getHeaders();
			String packagesString = (String)headers.get(Constants.IMPORT_PACKAGE);
			Object [][][] parsed = Util.parseStandardHeader(packagesString);
			for(int i = 0;i<parsed.length;i++){
				importedPackages.add(new ImportedPackageImpl(parsed[i]));
			}
		}
		return importedPackages;
	}
	
	public List<ExportedPackage> getExportedPackages(){
		synchronized(guardExp){
			// lazy parsing
			if(exportedPackages != null){
				return exportedPackages;
			}
		exportedPackages = new ArrayList<ExportedPackage>();
//		 Export-Package: net.mjahn.inspector.core;uses:="org.osgi.framework,org.osgi.framework.hooks.service";version="1.0.0",org.osgi.service.framework; version="1.0"; x-internal:=true
		//FIXME: implement this
		return null;
		}
	}
	
	private void parseExportedBundles(){
		
	}
}
