package de.paulwein.paul.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import de.paulwein.paul.database.DatabaseTables.AlarmColumns;
import de.paulwein.paul.database.PaulDatabase;

public class AlarmsProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI = 
			Uri.parse("content://de.paulwein.paul.alarmsprovider/elements");
	
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	private static final UriMatcher uriMatcher;
	private PaulDatabase mPaulDatabase;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("de.paulwein.paul.alarmsprovider", "elements", ALLROWS);
		uriMatcher.addURI("de.paulwein.paul.alarmsprovider", "elements/#", SINGLE_ROW);
	}
	
	@Override
	public boolean onCreate() {
		mPaulDatabase = new PaulDatabase(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.alarm.elemental";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.alarm.elemental";
		default:
			throw new IllegalArgumentException("Unsopported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mPaulDatabase.getWritableDatabase();
		String nullColumnHack = null;
		
		long id = db.insert(AlarmColumns.TABLE_NAME, nullColumnHack, values);
		
		if(id > -1){
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(insertedId, null);
			return insertedId;
		}
		else
			return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mPaulDatabase.getWritableDatabase();
		
		switch(uriMatcher.match(uri)){
		case SINGLE_ROW: 
			String rowID = uri.getPathSegments().get(1);
			selection = AlarmColumns.TABLE_NAME + "." + AlarmColumns.ID + "=" + rowID + 
				(!TextUtils.isEmpty(selection) ? " AND (" +
				selection + ')' : "");
		default: break;
		}
		
		int updateCount = db.update(AlarmColumns.TABLE_NAME, values,selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mPaulDatabase.getWritableDatabase();
		
		switch(uriMatcher.match(uri)){
			case SINGLE_ROW: 
				String rowID = uri.getPathSegments().get(1);
				selection = AlarmColumns.TABLE_NAME + "." + AlarmColumns.ID + "=" + rowID + 
					(!TextUtils.isEmpty(selection) ? " AND (" +
					selection + ')' : "");
			default: break;
		}
		
		if(selection == null)
			selection = "1";
		
		int deleteCount = db.delete(AlarmColumns.TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deleteCount;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		// Open the database
		SQLiteDatabase db;
		try{
			db = mPaulDatabase.getWritableDatabase();
		} catch(SQLiteException ex){
			db = mPaulDatabase.getReadableDatabase();
		}
		
		String groupBy = null;
		String having = null;
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		switch(uriMatcher.match(uri)){
			case SINGLE_ROW:
				String rowID = uri.getPathSegments().get(1);
				queryBuilder.appendWhere(AlarmColumns.TABLE_NAME + "." + AlarmColumns.ID + "=" + rowID);
			default: break;
		}
		queryBuilder.setTables(AlarmColumns.TABLE_NAME);
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
		
		return cursor;
	}
}
