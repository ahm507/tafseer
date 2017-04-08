package org.quran.tafseer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
//		implements NavigationView.OnNavigationItemSelectedListener
{

	protected static final String LOG_TAG = "MainActivity";
	BookRepository dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

//        Right button of dots
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();


/////////////////////////////////////////////////////////////
		//install DB

		//FIXME: Use this instead of getting Context because activity is already extends Context
		Context context = getApplicationContext();
		DatabaseInstaller db = new DatabaseInstaller(context);

//		ProgressDialog hourGlassDlg = new ProgressDialog(this);
//		hourGlassDlg.setMessage("برجاء الإنتظار");
//		hourGlassDlg.setIndeterminate(true);
//		hourGlassDlg.setCancelable(false);
//		hourGlassDlg.show();

		try {
			db.install();
		} catch (IOException exception) {
			Log.e(LOG_TAG, "open >>" + exception.toString());
		}

//		hourGlassDlg.hide();

		//Open DB and display initial view
		dbHelper = new BookRepository(context);
		dbHelper.open();
		displayKids("", "", 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			if (historyStack.size() > 0) {
				displayPreviousContents();
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.nav_home_screen) {
			historyStack.push(new HistoryElement(curPageId, 0)); //gog to home screen.
			displayKids("", "", 0);
			findViewById(R.id.textViewDisplay).setVisibility(View.GONE);
			findViewById(R.id.listViewTabweeb).setVisibility(View.VISIBLE);
			return true;
		} else if (id == R.id.nav_about_us) {
			showAboutDialogue();
			return true;
		} else if (id == R.id.action_exit) {

			finish();
			//Go phone home
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	void displayPreviousContents() {
//		String page_id = historyStack.pop();
		HistoryElement state = historyStack.pop();
		String page_id = state.getPageId();
		int position = state.getPosition();

		WebView display = (WebView) findViewById(R.id.textViewDisplay);
		ListView tabweeb = (ListView) findViewById(R.id.listViewTabweeb);

		if (dbHelper.IsLeafItem(curBookCode, page_id)) {
			display.setVisibility(View.VISIBLE);
			tabweeb.setVisibility(View.GONE);
			displayContent(curBookCode, page_id, "");
		} else {
			display.setVisibility(View.GONE);
			display.loadData("", "text/html; charset=UTF-8", null);
			tabweeb.setVisibility(View.VISIBLE);
			displayKids(curBookCode, page_id, position);
		}
	}

	String curBookCode = "", curPageId = "";
	ArrayList<Book> curRecords = new ArrayList<>();
	Stack<HistoryElement> historyStack = new Stack<>();

	protected void displayContent(String book_code, String page_id, String searchWords) {
		try {
			WebView displayTextView = (WebView) findViewById(R.id.textViewDisplay);
			displayTextView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return handleSwipeLeftAndRight(event);
				}
			});
			ArrayList<Book> records = dbHelper.getDisplayData(book_code, page_id);

			//IF END OF BOOK REACHED
			if(records.size() == 0) {
				return; // DO NO THING
			}
			if (records.size() != 1) {
//				displayTextView.setText(Html.fromHtml("")); //just empty
				displayTextView.loadData("", "text/html; charset=UTF-8", null);

			} else {
				Book record = records.get(0);
				//Usually the book root. We come here as a previous gesture on the first record.
//				if(record.parent_id.equals("NO_PARENT")) {
//					return;
//				}

				String htmlContent = "";
				if(record.book_code.endsWith("_txt")) {
					String content = record.page;
					content = content.replaceAll("##", "<br><hr>");
					content = content.replaceAll("\n", "<br>");
					//        		bodyString += "<hr><p class='footnote-app-text'>" + footnote + "</p>";
					if (searchWords.trim().length() > 0) { //highlight search text
						content = Highlight.highlight(content, searchWords);
					}

					//Add title
					content = "<br><font color=\"blue\">" + record.title + "</font><hr>" + content;
					String htmlPagePrefix = "<html><body style='direction: rtl; text-align:justify; align-content: right;  text-align=right'><span align='right'>";
					String htmlPagePostfix = "</span></body><html>";
					htmlContent = htmlPagePrefix + content + htmlPagePostfix;
				} else if(record.book_code.endsWith("_html")) { //html file
					String content = record.page;
					if (searchWords.trim().length() > 0) { //highlight search text
						content = Highlight.highlight(content, searchWords);
					}
					//Add title
//					content = "<br><font color=\"blue\">" + record.title + "</font><hr>" + content;
//					String htmlPagePrefix = "<html><body style='direction: rtl; text-align:justify; align-content: right;  text-align=right'><span align='right'>";
//					String htmlPagePostfix = "</span></body><html>";
//					htmlContent = htmlPagePrefix + content + htmlPagePostfix;
					htmlContent = content;
				} else {
					throw new Exception("Unknown file type, book_id should end with .txt or .html");
				}
				displayTextView.loadData(htmlContent, "text/html; charset=UTF-8", null);
				curBookCode = record.book_code;
				curPageId = record.page_id;


			}
		} catch (Exception exception) {
			Log.e(LOG_TAG, "exception", exception);
			showErrorDialogue();
		}
	}

	protected void displayKids(String book_code, String page_id, int position) {
		try {
			curBookCode = book_code;
			curPageId = page_id;
			ArrayList<Book> records = dbHelper.getKidsData(book_code, page_id);
			final ArrayList<String> list = new ArrayList<>();
			curRecords.clear();
			for (Book record : records) {
				list.add(record.title);
				curRecords.add(record);
			}
			//populate the list of items into the ListView
			ListView listView = (ListView) findViewById(R.id.listViewTabweeb);
			listView.clearChoices();

			ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1, list);
			listView.setAdapter(adapter);
			//set vertical position as before
			listView.setSelection(position);


			// ListView Item Click Listener
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					Book record = curRecords.get(position);
					historyStack.push(new HistoryElement(curPageId, position)); //is going to change per user click
					WebView display = (WebView) findViewById(R.id.textViewDisplay);
					ListView tabweeb = (ListView) findViewById(R.id.listViewTabweeb);

					if (dbHelper.IsLeafItem(record.book_code, record.page_id)) {
						display.setVisibility(View.VISIBLE);
						tabweeb.setVisibility(View.GONE);
						displayContent(record.book_code, record.page_id, "");
					} else {
						display.setVisibility(View.GONE);
						display.loadData("", "text/html; charset=UTF-8", null);
						tabweeb.setVisibility(View.VISIBLE);
						displayKids(record.book_code, record.page_id, 0);

					}
				}
			});
		} catch (Exception exception) {
			Log.e(LOG_TAG, "exception", exception);
			showErrorDialogue();

		}
	}

	void showErrorDialogue() {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage("Unable to complete operation due to internal error!");
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	//Seems search can has its own class
	ArrayList<Book> curSearchHits = new ArrayList<>();
	int currentSearchPagesCount;
	int currentSearchPageNumber;
	final int pageLength = 50;

	public void onSearch(View view) {
		searchDatabase(1);
	}

	public void searchDatabase(int pageNumber) {
		currentSearchPageNumber = pageNumber;
		EditText searchEditor = (EditText) findViewById(R.id.search_edit_text);
		final String searchWords = searchEditor.getText().toString();
		if (searchWords.trim().length() == 0) {
			return; //just do nothing
		}
		int totalHitsCount = dbHelper.getSearchHitsTotalCount("", searchWords);
		//set text in between next and prev
		TextView paging = (TextView) findViewById(R.id.text_view_paging);
		paging.setText(Html.fromHtml(getPagingString(totalHitsCount)));

		ArrayList<Book> hits = dbHelper.search(searchWords, pageLength, pageNumber);
		curSearchHits.clear();
		final ArrayList<String> list = new ArrayList<>();
		for (Book record : hits) {
			list.add(record.title);
			curSearchHits.add(record);
		}
		ListView listView = (ListView) findViewById(R.id.listView_search_hits);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//				android.R.layout.simple_list_item_1, android.R.id.text1, list);
				R.layout.search_hits_list_view, android.R.id.text1, list);
		listView.setAdapter(adapter);

		// ListView Item Click Listener
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Book record = curSearchHits.get(position);
//				int verticalScroll = view.getVerticalScrollbarPosition()
				int verticalScroll = 0;

				historyStack.push(new HistoryElement(curPageId, verticalScroll)); //is going to change per user click
				findViewById(R.id.textViewDisplay).setVisibility(View.VISIBLE);
				findViewById(R.id.listViewTabweeb).setVisibility(View.GONE);

				//searchWords
				displayContent(record.book_code, record.page_id, searchWords);
				DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawer.closeDrawer(GravityCompat.START);
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0); //hide keyboard

			}
		});

	}

	public void onSearchNextPage(View view) {
		currentSearchPageNumber++;
		if (currentSearchPageNumber > currentSearchPagesCount) {
			currentSearchPageNumber--;
			return;
		}
		searchDatabase(currentSearchPageNumber);
	}

	public void onSearchPreviousPage(View view) {
		currentSearchPageNumber--;
		if (currentSearchPageNumber < 1) {
			currentSearchPageNumber = 1;
			return;
		}
		searchDatabase(currentSearchPageNumber);
	}

	public String getPagingString(int totalHitsCount) {
		//Adjust paging
		currentSearchPagesCount = (int) Math.ceil((double) totalHitsCount / (double) pageLength);
		return new Formatter().format(" ( %d / %d ) ", currentSearchPageNumber, currentSearchPagesCount).toString();
	}

	// Swipe left and right
	private float x1, x2;
	static final int MIN_DISTANCE = 150;

	public boolean handleSwipeLeftAndRight(MotionEvent event) {
		if (findViewById(R.id.listViewTabweeb).getVisibility() == View.VISIBLE) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x1 = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				x2 = event.getX();
				float deltaX = x2 - x1;
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					if (x2 > x1) { // Left to Right swipe action : NEXT
						displayContent(curBookCode, String.valueOf(Integer.parseInt(curPageId) + 1), "");
					} else {  // Right to left swipe action: PREVIOUS
						if (Integer.parseInt(curPageId) > 1) {
							displayContent(curBookCode, String.valueOf(Integer.parseInt(curPageId) - 1), "");
						}
					}
				}
				break;
		}
		return super.onTouchEvent(event);
	}


	void showAboutDialogue() {
		AlertDialog.Builder aboutAlert = new AlertDialog.Builder(
				MainActivity.this);
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		final ImageView view = (ImageView) factory.inflate(R.layout.about_image_view, null);
		aboutAlert.setView(view);
		aboutAlert.setTitle("عن البرنامج");
		aboutAlert.setNeutralButton("إغلاق", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int which) {
				dlg.dismiss();
			}
		});
		aboutAlert.show();
	}


}
