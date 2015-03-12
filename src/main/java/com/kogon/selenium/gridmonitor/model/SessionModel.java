package com.kogon.selenium.gridmonitor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.apache.commons.logging.impl.AvalonLogger;

import com.kogon.selenium.gridmonitor.JSONNotificationEvent;

public class SessionModel extends HashMap<String, Session> {
	protected List<Session> theSessionByCreationList = new ArrayList<Session>();
	private TreeNode theRootNode = null;

	public Session put(String aKey, JSONNotificationEvent aValue) {
		Session mySession = this.get(aKey);
		if (mySession == null) {
			mySession = new RequestedSession(aValue, getRootNode());
			theSessionByCreationList.add(mySession);
			this.put(aKey, mySession);
		} else {
			if (mySession instanceof RequestedSession) {
				final int myCreationIndex = theSessionByCreationList.indexOf(mySession);
				mySession = new Session(mySession.get(0), mySession.getParent());
				this.put(aKey, mySession);
				theSessionByCreationList.add(myCreationIndex, mySession);
				theSessionByCreationList.remove(myCreationIndex+1);
			}
			mySession.add(aValue);
		}
		return mySession;
	}

	public TreeNode getRootNode() {
		if (theRootNode == null) {
			theRootNode = new TreeNode() {
				@Override
				public String toString() {
					return "Test Sessions (Historical Order)";
				}

				@Override
				public boolean isLeaf() {
					return this.getChildCount() == 0;
				}

				@Override
				public TreeNode getParent() {
					return null;
				}

				@Override
				public int getIndex(TreeNode aNode) {
					if (aNode instanceof Session) {
						return theSessionByCreationList.indexOf(aNode);
					} else {
						return -1;
					}
				}

				@Override
				public int getChildCount() {
					return theSessionByCreationList.size();
				}

				@Override
				public TreeNode getChildAt(int aChildIndex) {
					return theSessionByCreationList.get(aChildIndex);
				}

				@Override
				public boolean getAllowsChildren() {
					return true;
				}

				@Override
				public Enumeration children() {
					return Collections.enumeration(theSessionByCreationList);
				}
			};
		}
		return theRootNode;
	}

}
