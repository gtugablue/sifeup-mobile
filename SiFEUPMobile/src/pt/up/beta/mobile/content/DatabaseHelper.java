package pt.up.beta.mobile.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME="sigarra.db";
	private static final int SCHEMA=3;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		SubjectsTable.onCreate(db);
		FriendsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SubjectsTable.onUpgrade(db, oldVersion, newVersion);
		FriendsTable.onUpgrade(db, oldVersion, newVersion);
	}

}
