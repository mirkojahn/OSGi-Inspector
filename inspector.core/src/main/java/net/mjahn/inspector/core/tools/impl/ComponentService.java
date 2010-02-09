package net.mjahn.inspector.core.tools.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.util.tracker.ServiceTracker;

public class ComponentService {

    protected static BundleContext _context = null;
    ServiceReference _reference = null;
    PackageAdmin _admin = null;
    private volatile IBundleNode _rootNode = null;
    private BundleChangeListener _bListener = null;
    static ComponentService componentService = null;

    // listener for components lifecycle
    static class BundleChangeListener implements BundleListener {

        public void bundleChanged(final BundleEvent event) {
            // probably add this one
            if (event.getType() == BundleEvent.RESOLVED) {
                // add the bundle to the list of all available bundles, which
                // may be required...
                // we really need to get all bundles, because this method would
                // otherwise only return the bundle itself (bundles which
                // require this very bundle and not the bundles this bundle
                // requires!!!)
                RequiredBundle[] rbs =
                    ((PackageAdmin)_context.getService(componentService._reference))
                        .getRequiredBundles(null);
                if (rbs != null) {
                    // ok, more than null bundles are required some how
                    for (int i = 0; i < rbs.length; i++) {
                        // let's find out, who requires this bundles...
                        Bundle[] bundles = rbs[i].getRequiringBundles();
                        // now we know enough to build an according structure
                        componentService.addDependencyToBundle(rbs[i].getBundle(), bundles);
                    }
                }
            } else if (event.getType() == BundleEvent.UNRESOLVED) {
                // if a bundle is unresolved it must be removed from the list
                componentService.removeDependencyOfBundle(event.getBundle());
            }
        }
    }

    /*
     * this method is first called by the Activator of this bundle (after startup). In this case you
     * can be sure that there is a context available, as soon as the bundle is active.
     */
    public synchronized static ComponentService getService() throws ServiceException {
        if (componentService == null) {
            throw new ServiceException("Bundle not started! The bundle must be started first."); //$NON-NLS-1$
        }
        return componentService;
    }

    private ComponentService(final BundleContext context) {
        // needed by the data structure to resolve Bundles
        _context = context;
    }

    /**
     * add the dependency this bundle causes to the dependency tree. in detail this means adding
     * this bundle as a dependency to all bundles, which depend on this one.
     * 
     * @param bundle
     */
    protected void addDependencyToBundle(final Bundle bundle) {
        // it is somehow weird, but this method only returns this very bundle
        // (and maybe different versions of it)
        RequiredBundle[] rbs = this._admin.getRequiredBundles(bundle.getSymbolicName());
        if (rbs != null) {
            for (int i = 0; i < rbs.length; i++) {
                Bundle[] bundles = rbs[i].getRequiringBundles();
                addDependencyToBundle(rbs[i].getBundle(), bundles);
            }
        }
    }

    /**
     * @param bundle bundle which is required by other bundles
     * @param reqBundles the bundles which require this bundle
     * @throws ServiceException
     */
    synchronized void addDependencyToBundle(final Bundle bundle, final Bundle[] reqBundles) {
        IBundleNode node = null;
        // check if the "bundle" was already added to our tree
        if ((node = this._rootNode.getNode(new Long(bundle.getBundleId()))) == null) {
            // no, so create the node
            try {
                node = new BundleNode(bundle.getBundleId());
                // and add the node to the tree
                this._rootNode.addNode(node);
            } catch (ServiceException e) {
                e.printStackTrace();
                // id was not longer available...
                // adding required bundles doesn't make sense, so stop here
            }

        }
        // after being sure, that this bundle is in our tree, go on with adding
        // it to the requiring bundles
        if (reqBundles != null
            && reqBundles.length > 0) {
            for (int i = 0; i < reqBundles.length; i++) {
                /*
                 * ok, call the add method of the tree. If the node was already added to this very
                 * node (the long value of the bundle), it is just ignored, because overwriting the
                 * _reference may delete the other containing dependencies in the structure. (new
                 * creates a new instance which doesn't know anything about the already added
                 * dependencies and a lookup followed by a decision, if to add or to drop is not
                 * that efficient in comparison.)
                 */
                try {
                    this._rootNode.addNodeToTree(new BundleNode(reqBundles[i].getBundleId()), node);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    // id was not longer available...
                }
            }
        }
    }

    /**
     * remove the bundle with all dependencies from the dependency tree
     * 
     * @param bundle which is unresolved
     */
    protected synchronized void removeDependencyOfBundle(final Bundle bundle) {
        if (bundle != null) {
        	this._rootNode.removeNodeFromTree(bundle.getBundleId(), true);
        }
    }

    /**
     * get dependent bundles of a given Bundle
     * 
     * @param bundle for which dependent bundles are searched
     * @return all bundles that the given bundle requires (only the first hierarchy.)
     */
    public static Bundle[] getDependantBundles(final Bundle bundle) {
        return ComponentService.getDependantBundles(bundle, false);
    }

    /**
     * get dependent bundles of a given Bundle
     * 
     * @param bundle for which dependent bundles are searched
     * @param recursive indicates if in the first dependency hierarchy should be stopped or not. If
     *        yes, it is the same like <code>getDependantBundles(bundle)</code>. If not it
     *        searches recursive all required bundles and returns a flat list of all of them.
     * @return all bundles that the given bundle requires
     */
    public static Bundle[] getDependantBundles(final Bundle bundle, final boolean recursive) {
        Long bundleID = new Long(bundle.getBundleId());
        IBundleNode node = componentService._rootNode.getNode(bundleID);
        return node.getNodes(recursive);
    }

    public static void startComponentService(final BundleContext context) {
        // needed by the data structure to resolve Bundles
        if (componentService == null) {
            componentService = new ComponentService(context);
        }
        _context = context;
        componentService._bListener = new BundleChangeListener();
        context.addBundleListener(componentService._bListener);
        // root element of the data structure
        componentService._rootNode = BundleNode.getRootNode();
        // get the service to discover dependent bundles
        ServiceTracker tracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        tracker.open();
        componentService._reference = tracker.getServiceReference();
        componentService._admin = (PackageAdmin)tracker.getService(componentService._reference);
        if (componentService._reference != null) {
            // get all bundles which are required, null means ALL
            // (if provided with a String only bundles which symbolic
            // name matches the String are returned - not that obvious/intuitive)
            RequiredBundle[] rbs = componentService._admin.getRequiredBundles(null);
            if (rbs != null) {
                // ok, more than null bundles seam to be required
                for (int i = 0; i < rbs.length; i++) {
                    // let's find out, who requires this bundles...
                    Bundle[] bundles = rbs[i].getRequiringBundles();
                    // now we know enough to build a according structure
                    componentService.addDependencyToBundle(rbs[i].getBundle(), bundles);
                }
            }
        }
    }

    public static void stopComponentService() {
        _context.removeBundleListener(componentService._bListener);
        // help GC
        componentService._bListener = null;
        componentService._rootNode = null;
        componentService._reference = null;
        componentService._admin = null;
        componentService = null;
    }

}
