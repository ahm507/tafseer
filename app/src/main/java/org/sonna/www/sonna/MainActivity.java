package org.sonna.www.sonna;

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
	DatabaseAdaptor dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

//        Right button of dots
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

//        Floating bar at the bottom
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

//		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//		navigationView.setNavigationItemSelectedListener(this);


//		Button searchButton = (Button)findViewById(R.id.mysearch_button);
//		searchButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				search();
//			}
//		});


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
		dbHelper = new DatabaseAdaptor(context);
		dbHelper.open();
		displayKids("", "");
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
			historyStack.push(curPageId);
			displayKids("", "");
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

//	@Override
//	public boolean onNavigationItemSelected(MenuItem item) {
//		// Handle navigation view item clicks here.
//		int id = item.getItemId();
//
//		if (id == R.id.nav_home_screen) {
//			historyStack.push(curPageId);
//			displayKids("", "");
//			displayContent("", "");
//		} else if (id == R.id.nav_search) {
//			TextView msgTextView = (TextView) findViewById(R.id.textViewDisplay);
//			msgTextView.setVisibility(View.GONE);
//
//		} else if (id == R.id.nav_about_us) {
//			TextView msgTextView = (TextView) findViewById(R.id.textViewDisplay);
//			msgTextView.setVisibility(View.VISIBLE);
//		}
//
//		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//		drawer.closeDrawer(GravityCompat.START);
//		return true;
//	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	void displayPreviousContents() {
		String page_id = historyStack.pop();

		TextView display = (TextView) findViewById(R.id.textViewDisplay);
		ListView tabweeb = (ListView) findViewById(R.id.listViewTabweeb);

		if (dbHelper.IsLeafItem(curBookCode, page_id)) {
			display.setVisibility(View.VISIBLE);
			tabweeb.setVisibility(View.GONE);
			displayContent(curBookCode, page_id, "");
		} else {
			display.setVisibility(View.GONE);
			tabweeb.setVisibility(View.VISIBLE);
			displayKids(curBookCode, page_id);
		}
	}

	String curBookCode = "", curPageId = "";
	ArrayList<DbRecord> curRecords = new ArrayList<>();
	Stack<String> historyStack = new Stack<>();

	protected void displayContent(String book_code, String page_id, String searchWords) {
		try {
			TextView displayTextView = (TextView) findViewById(R.id.textViewDisplay);
			displayTextView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return handleSwipeLeftAndRight(event);
				}
			});
			ArrayList<DbRecord> records = dbHelper.getDisplayData(book_code, page_id);
			if (records.size() != 1) {
				displayTextView.setText(Html.fromHtml("")); //just empty
			} else {
				DbRecord record = records.get(0);
				String content = record.page;
				content = content.replaceAll("##", "<br><hr>");
				content = content.replaceAll("\n", "<br>");
//        		bodyString += "<hr><p class='footnote-app-text'>" + footnote + "</p>";
				if(searchWords.trim().length() > 0) { //highlight search text
					content = highlight(content, searchWords);
				}

				//Add title
				content = "<font color=\"blue\">" + record.title + "</font><hr><br><br>" + content;

				displayTextView.setText(Html.fromHtml(content));
				curBookCode = record.book_code;
				curPageId = record.page_id;
			}
		} catch (Exception exception) {
			Log.e(LOG_TAG, "exception", exception);
			showErrorDialogue();
		}
	}

	protected void displayKids(String book_code, String page_id) {
		try {
			curBookCode = book_code;
			curPageId = page_id;
			ArrayList<DbRecord> records = dbHelper.getKidsData(book_code, page_id);
			final ArrayList<String> list = new ArrayList<>();
			curRecords.clear();
			for (DbRecord record : records) {
				list.add(record.title);
				curRecords.add(record);
			}
			//populate the list of items into the ListView
			ListView listView = (ListView) findViewById(R.id.listViewTabweeb);
			listView.clearChoices();

			ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1, list);
			listView.setAdapter(adapter);

			// ListView Item Click Listener
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					DbRecord record = curRecords.get(position);
					historyStack.push(curPageId); //is going to change per user click
					TextView display = (TextView) findViewById(R.id.textViewDisplay);
					ListView tabweeb = (ListView) findViewById(R.id.listViewTabweeb);

					if (dbHelper.IsLeafItem(record.book_code, record.page_id)) {
						display.setVisibility(View.VISIBLE);
						tabweeb.setVisibility(View.GONE);
						displayContent(record.book_code, record.page_id, "");
					} else {
						display.setVisibility(View.GONE);
						tabweeb.setVisibility(View.VISIBLE);
						displayKids(record.book_code, record.page_id);

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
	ArrayList<DbRecord> curSearchHits = new ArrayList<>();
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

		ArrayList<DbRecord> hits = dbHelper.search(searchWords, pageLength, pageNumber);
		curSearchHits.clear();
		final ArrayList<String> list = new ArrayList<>();
		for (DbRecord record : hits) {
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
				DbRecord record = curSearchHits.get(position);
				historyStack.push(curPageId); //is going to change per user click
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

	String  processArabicWord(String arabic) {
		String wordBoundary = "[ .\\(\\),،\\-]+";
//		Python line of code
//		word_boundary_re = u'[ ;:,،.«»\'\"\\(\\)\\-\\{\\}\\<\\>]'  # Just one character must exist (+ is one or more)

		String result = "";
		//unicode diacritics letters from url,
		//http://unicode.org/charts/PDF/U0600.pdf
		String vowels = "[\u064B-\u065F]*";

		for (int i = 0; i < arabic.length(); i++) {
			result += arabic.charAt(i) + vowels;
		}
		//Insert word boundary mark
//		result = "\b" + result + "\b"; // DOES NOT WORK with ARABIC
//		result = wordBoundary + result + wordBoundary;
		return result;
	}


	public String highlight(String bodyString, String highlightWords) {
//    highlightWords = highlightWords.trim();
		//Because of word boundary problem, I have to add space at the start and at the end
//		bodyString = " " + bodyString + " ";
//    console.log("highlighted word is:" + result);
//		String words = highlightWords.split(" ");
		String spanStart = "<font color=\"red\">";
		String spanEnd = "</font>";
//		content += "<font color=\"red\">This is some red color text!</font>";
//		for (var i = 0; i < words.length; i++) {

		for (String word : highlightWords.split(" ")) {
//			var word = words[i].trim();
			word = word.trim();
			if (word.length() > 0) {
				String processedWord = processArabicWord(word);

//				bodyString = bodyString.replace(new RegExp(processedWord, 'g'),
//						function (found) {
//					console.log("found word is:[" + found + "]");
//					//can skip first and last word breakers from highlighting
//					var i = 0;
//					var result = "";
//					while (wordBoundary.indexOf(found[i]) != -1) { //is a boundary char
//						result += found[i];
//						i++;
//					}
//					result += spanStart;
//					while (wordBoundary.indexOf(found[i]) == -1) { //is not a boundary char
//						result += found[i];
//						i++;
//					}
//					result += spanEnd;
//					result += found.substring(i, found.length);
//
//					return result;
//				}
//				);
				//bodyString = bodyString.replaceAll(processedWord, "ELLAH");
//				bodyString = bodyString.replaceAll("(" + processedWord + ")", "\\0");
				bodyString = bodyString.replaceAll("(" + processedWord + ")", spanStart + word + spanEnd);



			}
		}

		//Now, remove the inserted space at the start and at the end
//		bodyString = bodyString.substring(1, bodyString.length() - 1);
		return bodyString;
	}

}
