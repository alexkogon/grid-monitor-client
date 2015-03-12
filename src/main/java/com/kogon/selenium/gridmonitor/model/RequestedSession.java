package com.kogon.selenium.gridmonitor.model;

import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.kogon.selenium.gridmonitor.JSONNotificationEvent;

public class RequestedSession extends Session {

	private final JSONNotificationEvent theValue;

	public RequestedSession(JSONNotificationEvent aValue, TreeNode aRootNode) {
		super(aValue, aRootNode);
		theValue = aValue;
	}

	@Override
	public String toString() {
		return "REQUESTED SESSION - "+theValue.getObjectPointer()+" - Waiting for use; REQ was: " +theValue.toString();
	}

	@Override
	public Enumeration children() {
		return Collections.emptyEnumeration();
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int aChildIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode aNode) {
		return -1;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
