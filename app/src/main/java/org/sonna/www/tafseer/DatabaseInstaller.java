package org.sonna.www.tafseer;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseInstaller extends SQLiteOpenHelper {
	private static String LOG_TAG = "DatabaseInstaller"; // Tag just for the LogCat window
	//destination path (location) of our database on device
	private static String DB_PATH = "";
	private static String DB_NAME = "books.sqlite";// Database name
	private SQLiteDatabase mDataBase;
	private final Context mContext;

	public DatabaseInstaller(Context context) {
		super(context, DB_NAME, null, 1);// 1? Its database Version
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		} else {
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		}
		this.mContext = context;
	}

	public void install() throws IOException {
		boolean mDataBaseExist = isDatabaseExist();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			this.close();
			try {
				Log.e(LOG_TAG, "Started copying database from assets to database");
				copyDataBaseFromAssets();
				Log.e(LOG_TAG, "finished database copy");
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	private boolean isDatabaseExist() {
		File dbFile = new File(DB_PATH + DB_NAME);
		//Log.v("dbFile", dbFile + "   "+ dbFile.exists());
		return dbFile.exists();
	}

	private void copyDataBaseFromAssets() throws IOException {
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	//Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		//Log.v("mPath", mPath);
		mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		//mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return mDataBase != null;
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}
}

//String outFileName = DB_PATH + DB_NAME;
//File dbFile = new File(outFileName);
//dbFile.delete();