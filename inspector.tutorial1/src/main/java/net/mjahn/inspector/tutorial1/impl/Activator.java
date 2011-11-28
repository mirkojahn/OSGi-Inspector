package net.mjahn.inspector.tutorial1.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mjahn.inspector.core.FrameworkInspector;
import net.mjahn.inspector.core.NotFoundServiceCall;
import net.mjahn.inspector.core.TrackedBundle;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;


public class Activator implements BundleActivator, CommandProvider {
	
	private ServiceTracker fwInspectorTracker = null;
	
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		fwInspectorTracker =
            new ServiceTracker(context, FrameworkInspector.class.getName(), null);
		fwInspectorTracker.open();
		
		context.registerService(CommandProvider.class.getName(), this, null);
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		fwInspectorTracker.close();
	}
	
	public FrameworkInspector getFrameworkInspector(){
		return (FrameworkInspector)fwInspectorTracker.getService();
	}

	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
        buffer.append("--- OSGi Tutorial command line ---\n");
        buffer.append("\tcheck - \n");
        return buffer.toString();
	}
	
	/**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _check(final CommandInterpreter ci) throws Exception {
    	FrameworkInspector fi = getFrameworkInspector();
    	if(fi == null){
    		System.out.println("No FrameworkInspector Service found! Can't perform command.");
    	} else {
    		List<? extends TrackedBundle> tbs = fi.getAllTrackedBundles();
    		System.out.println("The following (potential) problems have been identified: ");
    		Iterator<? extends TrackedBundle> tbsIter = tbs.iterator();
    		while (tbsIter.hasNext()) {
    			TrackedBundle tb = tbsIter.next();
    			if (tb != null) {
    				ArrayList list = new ArrayList();
    				if (!tb.getAllNotFoundServiceCalls().isEmpty()) {
    					Iterator<NotFoundServiceCall> it1 = tb
    							.getAllNotFoundServiceCalls().iterator();
    					while (it1.hasNext()) {
    						list.add(it1.next());
    					}
    				}
//    				if (!tb.getAllAddedServicesListens().isEmpty()) {
//    					Iterator<ListenerInfo> it2 = tb
//    							.getAllAddedServicesListens().iterator();
//    					while (it2.hasNext()) {
//    						list.add(it2.next());
//    					}
//    				}
    				if(list.size() > 0){
    					System.out.println("\nBundle: "
        						+ tb.getBundle().getSymbolicName());
    					Iterator iter = list.iterator();
    					while(iter.hasNext()){
    						System.out.println(iter.next().toString());
    					}
    						
    				}
    			}
    		}
    	}
    }
}
