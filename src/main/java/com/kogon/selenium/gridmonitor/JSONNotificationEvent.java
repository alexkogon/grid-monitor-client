package com.kogon.selenium.gridmonitor;

import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class JSONNotificationEvent implements TreeNode {
	private final String theRequest;
	private String theResponse;
	private String theNumber;
	private TreeNode theParentSession;
	private String theSessionKey=null;

	public JSONNotificationEvent(String aRequest, String aResponse,
			long aSequenceNumber) {
		super();
		theRequest = aRequest;
		theResponse = aResponse;
		theNumber = Long.toString(aSequenceNumber);
	}

	public String toString() {
		final StringBuilder myStringBuilder = new StringBuilder();
		myStringBuilder.append(theNumber);
		myStringBuilder.append("    ");
//		myStringBuilder.append(theSessionKey==null?"NO KEY":theSessionKey);
//		myStringBuilder.append("    ");
		myStringBuilder.append(theRequest);
//		myStringBuilder.append("    ");
//		myStringBuilder.append(theResponse);
		
		return myStringBuilder.toString();
	}

	public String getNumber() {
		return theNumber;
	}

	public void setNumber(String aNotification) {
		theNumber = aNotification;
	}

	public String getResponse() {
		return theResponse;
	}

	public void setResponse(String aResponse) {
		theResponse = aResponse;
	}

	public TreeNode getParentSession() {
		return theParentSession;
	}

	public void setParentSession(TreeNode aParentSession) {
		theParentSession = aParentSession;
	}

	public String getRequest() {
		return theRequest;
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
	public TreeNode getParent() {
		return theParentSession;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public void setParent(TreeNode aSession) {
		theParentSession = aSession;
	}

	public void setSessionKey(String aSessionKey) {
		if(theSessionKey==null) {
			theSessionKey = aSessionKey;
		} else {
			throw new RuntimeException("Trying to set Session Key to "+aSessionKey+" but already set to "+theSessionKey+"!\n"+this);
		}
	}

	public String getSessionKey() {
		return theSessionKey;
	}

	public String getObjectPointer() {
		String theObjectPrefix=")@";
		final int myIndexOf = theRequest.indexOf(theObjectPrefix);
		if(myIndexOf<0) {
			return null;
		} else {
			final String mySubstring = theRequest.substring(myIndexOf+theObjectPrefix.length());
			return mySubstring.substring(0, mySubstring.indexOf(' '));
		}
	}
}