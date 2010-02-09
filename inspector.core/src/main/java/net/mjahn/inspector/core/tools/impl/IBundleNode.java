package net.mjahn.inspector.core.tools.impl;

import org.osgi.framework.Bundle;

interface IBundleNode extends ITreeModel{

	public Bundle[] getNodes();

	public Bundle[] getNodes(boolean recursive);

	public void addNode(IBundleNode node);

	public void removeNode(long node);

	public int countElements();

	public IBundleNode getNode(Long id);

	/**
	 * @return the id of a bundle or null if it is the root of the tree.
	 */
	public Long getId();

	public Bundle getBundle();
}
