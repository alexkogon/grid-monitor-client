package com.kogon.selenium.gridmonitor;

import ca.odell.glazedlists.gui.TableFormat;

final class JSONEventTableModel implements
		TableFormat<JSONNotificationEvent> {
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int aArg0) {
		switch (aArg0) {
		case 0:
			return "Seq #";
		case 1:
			return "JSON Req";
		case 2:
			return "JSON Resp";
		default:
			return "";
		}
	}

	@Override
	public Object getColumnValue(
			JSONNotificationEvent aArg0,
			int aArg1) {
		switch (aArg1) {
		case 0:
			return aArg0.getNumber();
		case 1:
			return aArg0.getRequest();
		case 2:
			return aArg0.getResponse();
		default:
			return "";
		}
	}
}