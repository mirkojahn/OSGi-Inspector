/**
 *
 */
package net.mjahn.inspector.core.tools.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;


import org.osgi.framework.Bundle;

/**
 * @author Mirko Jahn
 * @version 1.0
 */
class BundleNode implements IBundleNode, ITreeModel {

    private WeakHashMap<Long, IBundleNode> nodes;
    private Long bundleId = null;
    private String name = null;
    private static BundleNode rootNode = null;

    public static IBundleNode getRootNode() {
        if (rootNode == null) {
            rootNode = new BundleNode();
        }
        return rootNode;
    }

    private BundleNode() {
        // do not allow anyone else to do this.
        // the root-node has the id null...
        this.name = "root-node"; //$NON-NLS-1$
        initBundleNode();
    }

    public BundleNode(final long id) throws ServiceException {
        this.bundleId = Long.valueOf(id);
        Bundle bundle = ComponentService._context.getBundle(id);
        if (bundle == null)
            throw new ServiceException("Bundle with id "
                + id + "not available.");
        this.name = bundle.getSymbolicName();
        initBundleNode();
    }

    private void initBundleNode() {
        this.nodes = new WeakHashMap<Long, IBundleNode>(50, 0.5f);
    }

    /**
     * @return is it the root node of the tree?
     */
    public boolean isRoot() {
        if (this.bundleId == null)
            return true;
        return false;
    }

    /**
     * @return the bundle symbolic name, or "root-node" for root
     */
    public String getSymbolicName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     */
    public Bundle getBundle() {
        return ComponentService._context.getBundle(this.bundleId.longValue());
    }

    /*
     * (non-Javadoc)
     * 
     */
    public Long getId() {
        return this.bundleId;
    }

    /*
     * (non-Javadoc)
     * 
     */
    public void addNode(final IBundleNode node) {
        // if not in this node, add to this node
        if (!this.nodes.containsKey(node.getId()))
            this.nodes.put(node.getId(), node);
    }

    /*
     * (non-Javadoc)
     * 
     */
    public void removeNode(final long node) {
        this.nodes.remove(node);
        // because of the WeakHashMap, the following might not be needed, but to be sure we do it
        // anyway.
        Iterator<Entry<Long, IBundleNode>> iter = this.nodes.entrySet().iterator();
        if (iter.hasNext()) {
            ((IBundleNode)iter.next()).removeNode(node);
        }
    }

    /*
     * (non-Javadoc)
     * 
     */
    public int countElements() {
        return this.nodes.size();
    }

    /*
     * (non-Javadoc)
     * 
     */
    public Bundle[] getNodes() {
        return getNodes(false);
    }

    /*
     * (non-Javadoc)
     * 
     */
    public Bundle[] getNodes(final boolean recursive) {
        HashSet<Bundle> bundles = new HashSet<Bundle>();
        Iterator<Entry<Long, IBundleNode>> keyValuePairs = this.nodes.entrySet().iterator();
        int mapSize = this.nodes.size();
        for (int i = 0; i < mapSize; i++) {
            Map.Entry<Long, IBundleNode> entry = keyValuePairs.next();
            IBundleNode node = entry.getValue();
            Bundle b = node.getBundle();
            bundles.add(b);
            if (recursive) {
                Bundle[] tBundles = node.getNodes(recursive);
                if (tBundles != null
                    && tBundles.length > 0) {
                    for (int j = 0; j < tBundles.length; j++) {
                        bundles.add(tBundles[j]);
                    }
                }
            }
        }
        if (bundles.isEmpty())
            return null;
        // clean up, not really needed - perhaps remove later...
        bundles.remove(null);

        return bundles.toArray(new Bundle[bundles.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     */
    public IBundleNode getNode(final Long id) {
        return this.nodes.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     */
    public void addNodeToTree(final IBundleNode whereToAdd, final IBundleNode nodeToAdd) {
        if (nodeToAdd == null) {
            return; // nothing to do
        }
        // add to root if nothing specified and exit this method
        if (whereToAdd == null) {
            rootNode.addNode(nodeToAdd);
            return;
        }
        // the _reference of the nodeToAdd in the root node
        IBundleNode rootInstance = null;
        // add to root, if not contained (each bundle must a least exist once in root)
        if ((rootInstance = rootNode.getNode(nodeToAdd.getId())) == null) {
            rootNode.addNode(nodeToAdd);
            rootInstance = nodeToAdd;
        }
        // if the "where to add" object doesn't exist in root... add it!
        IBundleNode whereTo = null;
        if ((whereTo = rootNode.getNode(whereToAdd.getId())) == null) {
            rootNode.addNode(whereToAdd);
            whereTo = whereToAdd;
        }
        // add, if equal
        if (whereTo.getId().equals(this.getId()))
            addNode(rootInstance);
        if (this.nodes.containsKey(whereTo.getId())) {
            (this.nodes.get(whereTo.getId())).addNode(rootInstance);
        }
        // no recursive adding necessary. We only have a two layer tree
        // (root with children and children with dependencies)
    }

    /*
     * (non-Javadoc)
     * 
     */
    public void removeNodeFromTree(final long nodeToRemove, final boolean recursive) {
        this.nodes.remove(nodeToRemove);
        if (recursive) {
            Iterator<Entry<Long, IBundleNode>> iter = this.nodes.entrySet().iterator();
            while (iter.hasNext()) {
                ((iter.next()).getValue()).removeNodeFromTree(nodeToRemove, recursive);
            }
        }
    }
}
