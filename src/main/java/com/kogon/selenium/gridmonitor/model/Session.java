package com.kogon.selenium.gridmonitor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.kogon.selenium.gridmonitor.JSONNotificationEvent;

public class Session extends ArrayList<JSONNotificationEvent> implements TreeNode{
	public Session(JSONNotificationEvent aValue,TreeNode aRootNode) {
		super();
		aValue.setParent(this);
		this.add(aValue);
		theRootNode=aRootNode;
	}
	private final TreeNode theRootNode;

	@Override
	public String toString() {
		final JSONNotificationEvent myJsonNotificationEvent = get(1);
		final String mySessionKey = myJsonNotificationEvent.getSessionKey();
		final String myObjectPointer = myJsonNotificationEvent.getObjectPointer();
		return myObjectPointer+" / "+mySessionKey;
	}
	
	@Override
	public Enumeration children() {
		return Collections.enumeration(this);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int aChildIndex) {
		return this.get(aChildIndex);
	}

	@Override
	public int getChildCount() {
		return this.size();
	}

	@Override
	public int getIndex(TreeNode aNode) {
		return this.getIndex(aNode);
	}

	@Override
	public TreeNode getParent() {
		return theRootNode;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
}