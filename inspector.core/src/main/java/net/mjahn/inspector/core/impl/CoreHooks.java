package net.mjahn.inspector.core.impl;

import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;

public class CoreHooks implements FindHook, ListenerHook {
	private FrameworkInspectorImpl fwAnalyzer;

	public CoreHooks(FrameworkInspectorImpl analyzer) {
		fwAnalyzer = analyzer;
	}

	public void find(BundleContext ctx, String serviceName, String filter,
			boolean obtainAll, @SuppressWarnings("rawtypes") Collection serviceReferences) {
		// track services that are not found
		if (serviceReferences == null || serviceReferences.isEmpty()) {
			NotFoundServiceCallImpl nfsc = new NotFoundServiceCallImpl(
					ctx.getBundle(), serviceName, filter, obtainAll);
			// System.out.println("Not satisfied service Request: "+ctx.getBundle().getSymbolicName()
			// + " " + serviceName + " " + filter);
			fwAnalyzer.getTrackedBundleImpl(ctx.getBundle().getBundleId())
					.addNotFoundServiceCall(nfsc);

			// these services got found
		} else {
			// System.out.println("Service lookup succeeded: "+ctx.getBundle().getSymbolicName()
			// + " " + serviceName + " " + filter);
			// do nothing for now
		}
	}

	public void added(@SuppressWarnings("rawtypes") Collection coll) {
		// shouldn't be empty, but anyway
		if (coll == null || coll.isEmpty()) {
			return;
		}
		@SuppressWarnings("rawtypes")
		Iterator iter = coll.iterator();
		while (iter.hasNext()) {
			ListenerInfo li = (ListenerInfo) iter.next();
			long id = li.getBundleContext().getBundle().getBundleId();
			TrackedBundleImpl tb = fwAnalyzer.getTrackedBundleImpl(id);
			tb.addedListenerForService(li);
		}
	}

	public void removed(@SuppressWarnings("rawtypes") Collection coll) {
		// do nothing... (for now - later track what listener was removed)
		if (coll == null || coll.isEmpty()) {
			return;
		}
		@SuppressWarnings("rawtypes")
		Iterator iter = coll.iterator();
		while (iter.hasNext()) {
			ListenerInfo li = (ListenerInfo) iter.next();
			// System.out.println("Remove Listener for bundle '"+li.getBundleContext().getBundle().getSymbolicName()
			// + "' with filter: "+ li.getFilter());
			try {
				long id = li.getBundleContext().getBundle().getBundleId();
				TrackedBundleImpl tb = fwAnalyzer.getTrackedBundleImpl(id);
				tb.removeListenerForService(li);
			} catch (Exception e) {
				// the bundle context might be invalid... not nice, but can
				// happen :-/
			}
		}

	}

}
