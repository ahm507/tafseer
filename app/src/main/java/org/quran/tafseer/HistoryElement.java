package org.quran.tafseer;

public class HistoryElement {

	String pageId;
	private int position;

	public HistoryElement (String pageId, int position) {
		this.pageId = pageId;
		this.position = position;

	}

	public String getPageId() {
		return pageId;
	}


	public int getPosition() {
		return position;
	}

}
