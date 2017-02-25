package org.sonna.www.sonna;

public class DbRecord {

	public String page_id, parent_id, book_code, title, page;
	//Do not retrieve page_fts, as it it no-vowel text used for search only.
}
