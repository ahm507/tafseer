package org.sonna.www.tafseer;

public class HistoryElement {

	String pageId;
	private int position;

	HistoryElement (String pageId, int position) {
		this.pageId = pageId;
		this.position = position;

	}

	String getPageId() {
		return pageId;
	}


	public int getPosition() {
		return position;
	}

}
