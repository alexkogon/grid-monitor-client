package com.kogon.selenium.gridmonitor.controllers.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RegistrationRequestPanelEvents {
	public static MouseAdapter addRegistrationRequestTableDoubleClick(
			JTextArea aTextArea) {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					// int column = target.getSelectedColumn();
					final Object myValueAt = target.getValueAt(row, 1);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Gson gson = new GsonBuilder().setPrettyPrinting()
									.create();
							JsonParser jp = new JsonParser();
							JsonElement je = jp.parse(myValueAt.toString());
							String prettyJsonString = gson.toJson(je);
							aTextArea.setText(prettyJsonString);
						}
					});
				}
			}
		};
	}

	public static MouseListener addPrefixJSONTableDoubleClick(
			JTextArea aTextArea, int aColumnIndex) {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					// int column = target.getSelectedColumn();
					final Object myValueAt = target.getValueAt(row,
							aColumnIndex - 1);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Gson gson = new GsonBuilder().setPrettyPrinting()
									.create();
							JsonParser jp = new JsonParser();
							final String myString = myValueAt.toString();
							final String myJSONPrefix = "{";
							final int myIndexOf = myString
									.indexOf(myJSONPrefix);
							if (myIndexOf >= 0) {
								final String myTrim = myString.substring(
										myIndexOf).trim();
								JsonElement je = jp.parse(myTrim);
								String prettyJsonString = gson.toJson(je);
								if (myIndexOf == 0) {
									aTextArea.setText(prettyJsonString);
								} else {
									aTextArea.setText(myString.substring(row,
											myIndexOf - 1)
											+ "\n"
											+ prettyJsonString);
								}
							} else {
								aTextArea.setText(myString);
							}
						}
					});
				}
			}
		};
	}

	public static MouseListener addPrefixJSONTableDoubleClick(
			JTextArea aTextArea) {
		return addPrefixJSONTableDoubleClick(aTextArea, 1);
	}
}
