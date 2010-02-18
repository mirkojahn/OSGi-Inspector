package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.ImportedPackage;
import net.mjahn.inspector.core.ListenerInfo;
import net.mjahn.inspector.core.NotFoundServiceCall;
import net.mjahn.inspector.core.TrackedBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class TrackedBundleImpl implements TrackedBundle {
	private final long bundleId;

	private ArrayList<ListenerInfo> listeningForService = new ArrayList<ListenerInfo>();
	private ArrayList<ListenerInfo> notLongerListeningForService = new ArrayList<ListenerInfo>();
	private ArrayList<NotFoundServiceCall> notFoundServiceCalls = new ArrayList<NotFoundServiceCall>();
	private ArrayList<ExportedPackage> exportedPackages = null;
	private ArrayList<ImportedPackage> importedPackages = null;
	private ArrayList<ImportedPackage> dynImportedPackages = null;

	private Object guard = new Object();
	private Object guardImp = new Object();
	private Object guardDynImp = new Object();
	private Object guardExp = new Object();

	public TrackedBundleImpl(long id) {
		bundleId = id;
	}

	public Bundle getBundle() {
		Bundle b = null;
		try {
			b = Activator.getContext().getBundle(bundleId);
		} catch (Exception e) {
			return null;
		}
		return b;
	}

	public void addedListenerForService(
			org.osgi.framework.hooks.service.ListenerHook.ListenerInfo info) {
		if (info != null) {
			synchronized (guard) {
				notLongerListeningForService.remove(new ListenerInfoImpl(info));
				listeningForService.add(new ListenerInfoImpl(info));
			}
		}
	}

	public List<ListenerInfo> getAllAddedServiceListeners() {
		return listeningForService;
	}

	public void removeListenerForService(
			org.osgi.framework.hooks.service.ListenerHook.ListenerInfo info) {
		if (info != null) {
			synchronized (guard) {
				listeningForService.remove(new ListenerInfoImpl(info));
				notLongerListeningForService.add(new ListenerInfoImpl(info));
			}
		}
	}

	public List<ListenerInfo> getAllRemovedServiceListeners() {
		return notLongerListeningForService;
	}

	public void addNotFoundServiceCall(NotFoundServiceCall serviceCall) {
		notFoundServiceCalls.add(serviceCall);
	}

	public List<NotFoundServiceCall> getAllNotFoundServiceCalls() {
		return notFoundServiceCalls;
	}

	@SuppressWarnings("unchecked")
	public List<ImportedPackage> getImportedPackages() {
		// lazy parsing
		synchronized (guardImp) {
			if (importedPackages != null) {
				return importedPackages;
			}
			importedPackages = new ArrayList<ImportedPackage>();
			Dictionary headers = getBundle().getHeaders();
			String packagesString = (String) headers
					.get(Constants.IMPORT_PACKAGE);
			Object[][][] parsed = Util.parseStandardHeader(packagesString);
			for (int i = 0; i < parsed.length; i++) {
				importedPackages.add(new ImportedPackageImpl(parsed[i],
						bundleId));
			}
		}
		return importedPackages;
	}

	@SuppressWarnings("unchecked")
	public List<ExportedPackage> getExportedPackages() {
		synchronized (guardExp) {
			// lazy parsing
			if (exportedPackages != null) {
				return exportedPackages;
			}
			exportedPackages = new ArrayList<ExportedPackage>();
			Dictionary headers = getBundle().getHeaders();
			String packagesString = (String) headers
					.get(Constants.EXPORT_PACKAGE);
			Object[][][] parsed = Util.parseStandardHeader(packagesString);
			for (int i = 0; i < parsed.length; i++) {
				exportedPackages.add(new ExportedPackageImpl(parsed[i],
						bundleId));
			}
			return exportedPackages;
		}
	}

	public List<TrackedBundle> getHostBundles() {
		if (isFragment()) {
			ArrayList<TrackedBundle> bHosts = new ArrayList<TrackedBundle>();
			Bundle[] hosts = Activator.getPackageAdmin().getHosts(getBundle());
			if (hosts != null && hosts.length > 0) {
				for (int i = 0; i < hosts.length; i++) {
					if (hosts[i] != null) {
						bHosts.add(Activator.getFrameworkInspectorImpl()
								.getTrackedBundleImpl(hosts[i].getBundleId()));
					}
				}
			}
			return bHosts;
		}
		return new ArrayList<TrackedBundle>();
	}

	public List<TrackedBundle> getAttachedFragmentBundles() {
		ArrayList<TrackedBundle> bFragments = new ArrayList<TrackedBundle>();
		Bundle[] fragments = Activator.getPackageAdmin().getFragments(
				getBundle());
		if (fragments != null && fragments.length > 0) {
			for (int i = 0; i < fragments.length; i++) {
				if (fragments[i] != null) {
					bFragments.add(Activator.getFrameworkInspectorImpl()
							.getTrackedBundleImpl(fragments[i].getBundleId()));
				}
			}
			return bFragments;
		}
		return new ArrayList<TrackedBundle>();
	}

	public boolean isFragment() {
		getBundle().getHeaders();
		String fragHost = (String) getBundle().getHeaders().get(
				Constants.FRAGMENT_HOST);
		return (fragHost != null ? true : false);
	}

	@SuppressWarnings("unchecked")
	public List<ImportedPackage> getDynamicImportedPackage() {
		synchronized (guardDynImp) {
			// lazy parsing
			if (dynImportedPackages != null) {
				return dynImportedPackages;
			}
			dynImportedPackages = new ArrayList<ImportedPackage>();
			Dictionary headers = getBundle().getHeaders();
			String packagesString = (String) headers
					.get(Constants.DYNAMICIMPORT_PACKAGE);
			Object[][][] parsed = Util.parseStandardHeader(packagesString);
			for (int i = 0; i < parsed.length; i++) {
				dynImportedPackages.add(new ImportedPackageImpl(parsed[i],
						bundleId));
			}
			return dynImportedPackages;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean hasDynamicImport() {
		Dictionary headers = getBundle().getHeaders();
		String packagesString = (String) headers
				.get(Constants.DYNAMICIMPORT_PACKAGE);
		return (packagesString != null ? true : false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (bundleId ^ (bundleId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TrackedBundleImpl)) {
			return false;
		}
		TrackedBundleImpl other = (TrackedBundleImpl) obj;
		if (bundleId != other.bundleId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackedBundleImpl [bundleId=");
		builder.append(bundleId);
		builder.append(", ");
		List<ImportedPackage> packages = getDynamicImportedPackage();
		if (packages.size() > 0) {
			builder.append("DynamicImportedPackages: ");
			Iterator<ImportedPackage> iter = packages.iterator();
			while(iter.hasNext()){
				ImportedPackage impPack = iter.next();
				builder.append(impPack.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			packages = null;
		}
		
		List<ExportedPackage> exPackages = getExportedPackages();
		if (exPackages.size() > 0) {
			Iterator<ExportedPackage> iter = exPackages.iterator();
			while(iter.hasNext()){
				ExportedPackage expPack = iter.next();
				builder.append(expPack.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		packages = getImportedPackages();
		if (packages.size() > 0) {
			Iterator<ImportedPackage> iter = packages.iterator();
			while(iter.hasNext()){
				ImportedPackage impPack = iter.next();
				builder.append(impPack.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			packages = null;
		}
		if (listeningForService != null) {
			Iterator<ListenerInfo> iter = listeningForService.iterator();
			while(iter.hasNext()){
				ListenerInfo info = iter.next();
				builder.append(info.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		if (notFoundServiceCalls != null) {
			Iterator<NotFoundServiceCall> iter = notFoundServiceCalls.iterator();
			while(iter.hasNext()){
				NotFoundServiceCall info = iter.next();
				builder.append(info.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		if (notLongerListeningForService != null) {
			Iterator<ListenerInfo> iter = notLongerListeningForService.iterator();
			while(iter.hasNext()){
				ListenerInfo info = iter.next();
				builder.append(info.toString());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
		}
		
		// isFragment
		builder.append(", isFragment: ");
		builder.append(isFragment());
		
		// get all hosts for this fragment 
		if (isFragment()) {
			List<TrackedBundle> tb = getHostBundles();
			if(tb.size()>0){
				builder.append(", hostBundles: ");
				Iterator<TrackedBundle> iter = tb.iterator();
				while(iter.hasNext()){
					TrackedBundle bun = iter.next();
					if(bun != null){
						builder.append(" bundleId: ");
						builder.append(bun.getBundle());
						if(iter.hasNext()){
							builder.append(", ");
						}
					}
				}
			}
		}
		
		// get all fragments for this host 
		if (!isFragment()) {
			List<TrackedBundle> tb = getAttachedFragmentBundles();
			if(tb.size()>0){
				builder.append(",  attachedFragmentBundles: [");
				Iterator<TrackedBundle> iter = tb.iterator();
				while(iter.hasNext()){
					TrackedBundle bun = iter.next();
					if(bun != null){
						builder.append("{ bundleId: ");
						builder.append(bun.getBundle());
						if(iter.hasNext()){
							builder.append(", ");
						}
					}
				}
			}
		}
		return builder.toString();
	}

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"trackedBundle\": {");
		// the bundle
		builder.append("\"bundleId\":\"");
		if (getBundle() != null) {
			builder.append(getBundle());
		}
		builder.append("\", ");

		// List all dynamic imports
		List<ImportedPackage> packages = getDynamicImportedPackage();
		if (packages.size() > 0) {
			builder.append("\"dynamicImportedPackages\": [");
			Iterator<ImportedPackage> iter = packages.iterator();
			while(iter.hasNext()){
				ImportedPackage impPack = iter.next();
				builder.append(impPack.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			builder.append("], ");
			packages = null;
		}
		// has dynamic import(s)
		builder.append("\"dynamicImport\":\"");
		builder.append(hasDynamicImport());
		builder.append("\"");
		
		// list exported Packages
		List<ExportedPackage> exPackages = getExportedPackages();
		if (exPackages.size() > 0) {
			builder.append(", \"exportedPackages\": [");
			Iterator<ExportedPackage> iter = exPackages.iterator();
			while(iter.hasNext()){
				ExportedPackage expPack = iter.next();
				builder.append(expPack.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			builder.append("]");
		}
		
		// list imported packages
		packages = getImportedPackages();
		if (packages.size() > 0) {
			builder.append(", \"importedPackages\": [");
			Iterator<ImportedPackage> iter = packages.iterator();
			while(iter.hasNext()){
				ImportedPackage impPack = iter.next();
				builder.append(impPack.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			packages = null;
			builder.append("]");
		}
		
		// list the services this bundle is listening for
		if (listeningForService != null) {
			builder.append(", \"allAddedServiceListeners\": [");
			Iterator<ListenerInfo> iter = listeningForService.iterator();
			while(iter.hasNext()){
				ListenerInfo info = iter.next();
				builder.append(info.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			builder.append("]");
		}
		
		// list all service not found calls
		if (notFoundServiceCalls != null) {
			builder.append(", \"allNotFoundServiceCalls\": [");
			Iterator<NotFoundServiceCall> iter = notFoundServiceCalls.iterator();
			while(iter.hasNext()){
				NotFoundServiceCall info = iter.next();
				builder.append(info.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			builder.append("]");
		}
		
		// get all services this bundle is no longer listening for 
		if (notLongerListeningForService != null) {
			builder.append(", \"allRemovedServiceListeners\": [");
			Iterator<ListenerInfo> iter = notLongerListeningForService.iterator();
			while(iter.hasNext()){
				ListenerInfo info = iter.next();
				builder.append(info.toJSON());
				if(iter.hasNext()){
					builder.append(", ");
				}
			}
			builder.append("]");
		}
		
		// isFragment
		builder.append("\", fragment\":\"");
		builder.append(isFragment());
		builder.append("\"");
		
		// get all hosts for this fragment 
		if (isFragment()) {
			List<TrackedBundle> tb = getHostBundles();
			if(tb.size()>0){
				builder.append(", \"hostBundles\": [");
				Iterator<TrackedBundle> iter = tb.iterator();
				while(iter.hasNext()){
					TrackedBundle bun = iter.next();
					if(bun != null){
						builder.append("{\"bundleId\":\"");
						builder.append(bun.getBundle());
						builder.append("\"");
						if(iter.hasNext()){
							builder.append(", ");
						}
					}
				}
				builder.append("]");
			}
		}
		
		// get all fragments for this host 
		if (!isFragment()) {
			List<TrackedBundle> tb = getAttachedFragmentBundles();
			if(tb.size()>0){
				builder.append(", \"attachedFragmentBundles\": [");
				Iterator<TrackedBundle> iter = tb.iterator();
				while(iter.hasNext()){
					TrackedBundle bun = iter.next();
					if(bun != null){
						builder.append("{\"bundleId\":\"");
						builder.append(bun.getBundle());
						builder.append("\"");
						if(iter.hasNext()){
							builder.append(", ");
						}
					}
				}
				builder.append("]");
			}
		}
		builder.append("}}");
		return builder.toString();
	}

}
