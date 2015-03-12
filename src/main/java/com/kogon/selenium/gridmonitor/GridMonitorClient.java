package com.kogon.selenium.gridmonitor;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.openqa.grid.selenium.grid.monitor.GridMonitor;
import org.openqa.grid.selenium.grid.monitor.GridMonitorMBean;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.GlazedListsSwing;

import com.kogon.selenium.gridmonitor.controllers.event.RegistrationRequestPanelEvents;
import com.kogon.selenium.gridmonitor.model.Session;
import com.kogon.selenium.gridmonitor.model.SessionModel;
import com.kogon.selenium.gridmonitor.viewfactory.GridMonitorViewFactory;
import com.kogon.selenium.gridmonitor.views.DriverRequestPanel;
import com.kogon.selenium.gridmonitor.views.RegistrationRequestPanel;
import com.kogon.selenium.gridmonitor.views.TreeWithTablePanel;

public class GridMonitorClient {

	private ClientListener theListener = new ClientListener();

	private JTree theSessionBrowserTree;
	private JTable theSessionBrowserTable;
	
	private EventList<JSONNotificationEvent> theProxyEventList;
	private JTable theProxyRequestTable;
	private JTextArea theProxyRequestText;
	private EventList<JSONNotificationEvent> theHubStatusEventList;
	private JTable theHubStatusRequestTable;
	private JTextArea theHubStatusRequestText;
	private EventList<JSONNotificationEvent> theRegistrationEventList;
	private JTable theRegistrationRequestTable;
	private JTextArea theRegistrationRequestText;
	private EventList<JSONNotificationEvent> theDriverEventList;
	private JTable theDriverRequestTable;
	private JTextArea theDriverRequestText;
	private SessionModel theSessionModel = new SessionModel();

	public static class ClientListener implements NotificationListener {

		public void handleNotification(Notification notification,
				Object handback) {
			if (notification instanceof AttributeChangeNotification) {
				AttributeChangeNotification acn = (AttributeChangeNotification) notification;
				final String myAttributeName = acn.getAttributeName();
				final Object myJSON = acn.getNewValue();
				final Object myServer = acn.getOldValue();
				final GridMonitorClient myHandback = (GridMonitorClient) handback;
				switch (GridMonitor.EventTypeKeys.valueOf(myAttributeName)) {
				case LastDriverString:
					myHandback.handleDriverNotification(
							myServer == null ? "null" : myServer.toString(),
							myJSON == null ? "null" : myJSON.toString(),
							acn.getSequenceNumber());
					break;
				case LastRegistrationString:
					myHandback
							.handleRegistrationNotification(
									myServer == null ? "null" : myServer
											.toString(),
									myJSON == null ? "null" : myJSON.toString(),
									acn.getSequenceNumber());
					break;
				case LastProxyStatus:
					if(myHandback.proxyIsActive()) {
						myHandback.handleProxyNotification(
								myServer == null ? "null" : myServer.toString(),
								myJSON == null ? "null" : myJSON.toString(),
								acn.getSequenceNumber());
					}
					break;
				case LastHubStatus:
					myHandback.handleHubStatusNotification(
							myServer == null ? "null" : myServer.toString(),
							myJSON == null ? "null" : myJSON.toString(),
							acn.getSequenceNumber());
					break;
				default:
					System.out.println("bye");
					break;
				}
			}
		}
	}

	public GridMonitorClient() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// create the browser
				JFrame myJFrame = setupUI();
				final DefaultTreeModel myModel = (DefaultTreeModel) theSessionBrowserTree.getModel();
				myModel.setRoot(theSessionModel.getRootNode());
				
//				theProxyEventList = new BasicEventList<JSONNotificationEvent>();
//				theProxyRequestTable.setModel(GlazedListsSwing.eventTableModel(
//						theProxyEventList, new JSONEventTableModel()));
				theHubStatusEventList = new BasicEventList<JSONNotificationEvent>();
				theHubStatusRequestTable.setModel(GlazedListsSwing
						.eventTableModel(theHubStatusEventList,
								new JSONEventTableModel()));
				theRegistrationEventList = new BasicEventList<JSONNotificationEvent>();
				theRegistrationRequestTable.setModel(GlazedListsSwing
						.eventTableModel(theRegistrationEventList,
								new JSONEventTableModel()));
				theDriverEventList = new BasicEventList<JSONNotificationEvent>();
				theDriverRequestTable.setModel(GlazedListsSwing
						.eventTableModel(theDriverEventList,
								new JSONEventTableModel()));
				finishUISetup(myJFrame);
			}

			private void finishUISetup(JFrame myJFrame) {
				setupJSONTable(theDriverRequestTable, theDriverRequestText);
				setupJSONTable(theRegistrationRequestTable,
						theRegistrationRequestText);
				setupJSONTable(theProxyRequestTable, theProxyRequestText);
				setupJSONTable(theHubStatusRequestTable,
						theHubStatusRequestText);
				myJFrame.setVisible(true);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tile(GridMonitorViewFactory.getInstance(
								GridMonitorClient.this).getParentWindow());
					}
				});
			}
		});
		setupJMXMBeans();
	}

	public boolean proxyIsActive() {
		return theProxyEventList!=null;
	}

	public void handleHubStatusNotification(String aServer,
			String aNotification, long aSequenceNumber) {
		final JSONNotificationEvent myJsonNotificationEvent2 = new JSONNotificationEvent(
				aServer, aNotification, aSequenceNumber);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				theHubStatusEventList.add(myJsonNotificationEvent2);
			}
		});
	}

	public void handleProxyNotification(String aServer, String aNotification,
			long aSequenceNumber) {
		final JSONNotificationEvent myJsonNotificationEvent2 = new JSONNotificationEvent(
				aServer, aNotification, aSequenceNumber);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				theProxyEventList.add(myJsonNotificationEvent2);
			}
		});
	}

	public void handleRegistrationNotification(String aRequest,
			String aResponse, long aSequenceNumber) {
		final JSONNotificationEvent myJsonNotificationEvent2 = new JSONNotificationEvent(
				aRequest, aResponse, aSequenceNumber);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				theRegistrationEventList.add(myJsonNotificationEvent2);
			}
		});
	}

	public void handleDriverNotification(String aRequest, String aResponse,
			long aSequenceNumber) {
		final JSONNotificationEvent myJsonNotificationEvent = new JSONNotificationEvent(
				aRequest, aResponse, aSequenceNumber);
		String mySessionObjectPointer = myJsonNotificationEvent.getObjectPointer();
		final String mySessionKeyString = "/wd/hub/session/";
		final int mySessionKeyIndex = aRequest.indexOf(mySessionKeyString);
		if(mySessionKeyIndex>=0) {
			final String mySubstring = aRequest.substring(mySessionKeyIndex+mySessionKeyString.length());
			final String mySessionKey = mySubstring.substring(0,mySubstring.indexOf('/'));
			myJsonNotificationEvent.setSessionKey(mySessionKey);
		}
		theSessionModel.put(mySessionObjectPointer, myJsonNotificationEvent);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((DefaultTreeModel) theSessionBrowserTree.getModel()).reload();
				theDriverEventList.add(myJsonNotificationEvent);
			}
		});
	}

	private JFrame setupUI() {
		JFrame myJFrame = new JFrame();
		myJFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		final GridMonitorViewFactory myViewFactory = GridMonitorViewFactory
				.getInstance(this);
		final JDesktopPane myParentWindow = myViewFactory.getParentWindow();
		myJFrame.getContentPane().add(myParentWindow);

		JInternalFrame myDriverPanelFrame = myViewFactory
				.createInternalWindow();
		myParentWindow.add(myDriverPanelFrame);
		DriverRequestPanel myDriverRequestPanel = new DriverRequestPanel();
		myDriverPanelFrame.getContentPane().add(myDriverRequestPanel);
		myDriverRequestPanel.setBorder(BorderFactory
				.createTitledBorder("Driver"));
		theDriverRequestTable = myDriverRequestPanel.getTable();
		theDriverRequestText = myDriverRequestPanel.getTextArea();
		myDriverPanelFrame.setSize(myDriverRequestPanel.getPreferredSize());
		myDriverPanelFrame.setVisible(true);

		JInternalFrame theJWindow = myViewFactory.createInternalWindow();
		myParentWindow.add(theJWindow);
		final RegistrationRequestPanel myRegistrationRequestPanel = new RegistrationRequestPanel();
		theJWindow.getContentPane().add(myRegistrationRequestPanel);
		myRegistrationRequestPanel.setBorder(BorderFactory
				.createTitledBorder("Registration"));
		theRegistrationRequestTable = myRegistrationRequestPanel.getTable();
		theRegistrationRequestText = myRegistrationRequestPanel.getTextArea();
		theJWindow.setSize(myRegistrationRequestPanel.getPreferredSize());
		theJWindow.setVisible(true);
		// theJWindow.setLocationRelativeTo(myJFrame);

		JInternalFrame myProxyMessageFrame = myViewFactory.createInternalWindow();
//		myParentWindow.add(myProxyMessageFrame);
		final RegistrationRequestPanel myProxyStatusPanel = new RegistrationRequestPanel();
		myProxyMessageFrame.getContentPane().add(myProxyStatusPanel);
		myProxyStatusPanel.setBorder(BorderFactory.createTitledBorder("Proxy"));
		theProxyRequestTable = myProxyStatusPanel.getTable();
		theProxyRequestText = myProxyStatusPanel.getTextArea();
		myProxyMessageFrame.setSize(myProxyStatusPanel.getPreferredSize());
//		myProxyMessageFrame.setVisible(true);
		// theJWindow2.setLocationRelativeTo(myJFrame);

		JInternalFrame theJWindow3 = myViewFactory.createInternalWindow();
		myParentWindow.add(theJWindow3);
		final RegistrationRequestPanel myHubStatusPanel = new RegistrationRequestPanel();
		theJWindow3.getContentPane().add(myHubStatusPanel);
		theJWindow3.setVisible(true);
		myHubStatusPanel.setBorder(BorderFactory
				.createTitledBorder("HubStatus"));
		theHubStatusRequestTable = myHubStatusPanel.getTable();
		theHubStatusRequestText = myHubStatusPanel.getTextArea();
		theJWindow3.setSize(myHubStatusPanel.getPreferredSize());
		// theJWindow3.setLocationRelativeTo(myJFrame);

		JInternalFrame mySessionTreeFrame = myViewFactory.createInternalWindow();
		myParentWindow.add(mySessionTreeFrame);
		final TreeWithTablePanel mySessionBrowserPanel = new TreeWithTablePanel();
		mySessionTreeFrame.getContentPane().add(mySessionBrowserPanel);
		mySessionTreeFrame.setVisible(true);
		mySessionBrowserPanel.setBorder(BorderFactory
				.createTitledBorder("SessionBrowser"));
		theSessionBrowserTable = mySessionBrowserPanel.getTable();
		theSessionBrowserTree = mySessionBrowserPanel.getTree();
		mySessionTreeFrame.setSize(mySessionBrowserPanel.getPreferredSize());
		// mySessionTreeFrame.setLocationRelativeTo(myJFrame);

		myJFrame.setSize(theJWindow.getWidth() * 2, theJWindow.getHeight() * 2);
		myJFrame.setLocationRelativeTo(null);

		return myJFrame;
	}

	public void tile(JDesktopPane desk) {

		// How many frames do we have?
		JInternalFrame[] allframes = desk.getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = desk.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and
		// then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed() && f.isIcon()) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				desk.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

	private void setupJMXMBeans() throws MalformedURLException, IOException,
			MalformedObjectNameException, InstanceNotFoundException {
		System.out.println("\nCreate an RMI connector client and "
				+ "connect it to the RMI connector server");
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		echo("\nDomains:");
		String domains[] = mbsc.getDomains();
		Arrays.sort(domains);
		for (String domain : domains) {
			echo("\tDomain = " + domain);
		}
		echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

		echo("\nMBean count = " + mbsc.getMBeanCount());
		echo("\nQuery MBeanServer MBeans:");
		Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null,
				null));
		for (ObjectName name : names) {
			echo("\tObjectName = " + name);
		}
		ObjectName mbeanName = new ObjectName(
				"org.openqa.grid.selenium.grid.monitor:type=GridMonitor");
		GridMonitorMBean gridMonitorMBean = JMX.newMBeanProxy(mbsc, mbeanName,
				GridMonitorMBean.class, true);
		echo("\nAdd notification listener...");
		mbsc.addNotificationListener(mbeanName, theListener, null, this);
	}

	private void setupJSONTable(final JTable myTable, final JTextArea myTextArea) {
		myTable.getColumnModel().getColumn(0).setMaxWidth(100);
		myTable.getColumnModel().getColumn(0).setMinWidth(20);
		myTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		myTable.addMouseListener(RegistrationRequestPanelEvents
				.addPrefixJSONTableDoubleClick(myTextArea, 2));
	}

	public static void main(String[] args) throws Exception {
		new GridMonitorClient();
	}

	static void echo(String a) {
		System.out.println(a);
	}
}
