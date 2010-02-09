package net.mjahn.inspector.core.tools.impl;

interface ITreeModel {
	public void addNodeToTree(IBundleNode whereToAdd, IBundleNode nodeToAdd);
	public void removeNodeFromTree(long nodeToRemove, boolean recursive);

}
