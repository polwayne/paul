package de.paulwein.paul.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import de.paulwein.paul.database.DatabaseTables.AlarmTbl;
import de.paulwein.paul.database.DatabaseTables.LocationPropertiesTbl;
import de.paulwein.paul.database.DatabaseTables.LocationsTbl;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;
import de.paulwein.paul.database.DatabaseTables.NotesListTbl;
import de.paulwein.paul.database.DatabaseTables.NotesTbl;
import de.paulwein.paul.database.DatabaseTables.TasksTbl;

public class PaulDatabase extends SQLiteOpenHelper {
	
	
	private static final String DATABASE_NAME = "paul.db";
	private static final int DATABASE_VERSION = 4;

	
	public PaulDatabase(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NotesTbl.CREATE_NOTES_TBL);
		db.execSQL(NotesListTbl.CREATE_LIST_TBL);
		db.execSQL(TasksTbl.CREATE_TASKS_TBL);
		db.execSQL(AlarmTbl.CREATE_ALARM_TBL);
		db.execSQL(LocationsTbl.CREATE_LOCATIONS_TBL);
		db.execSQL(LocationPropertiesTbl.CREATE_LOCATION_PROPERTIES_TBL);
		insertDefaultList(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(oldVersion == 1 && newVersion == 2)
			db.execSQL(TasksTbl.CREATE_TASKS_TBL);
		
		else if(oldVersion == 2 && newVersion == 3)
			db.execSQL(AlarmTbl.CREATE_ALARM_TBL);
		
		else if(oldVersion == 3 && newVersion == 4){
			db.execSQL(LocationsTbl.CREATE_LOCATIONS_TBL);
			db.execSQL(LocationPropertiesTbl.CREATE_LOCATION_PROPERTIES_TBL);
		}
		
		else{
			db.execSQL(NotesTbl.DROP_NOTES_TBL);
			db.execSQL(NotesListTbl.DROP_LIST_TBL);
			onCreate(db);
		}
	}
	
	private void insertDefaultList(SQLiteDatabase db){
		ContentValues values = new ContentValues();
		values.put(NotesListColumns.LIST_NAME, "Keine Kategorie");
		long rowID = db.insert(NotesListColumns.TABLE_NAME, null, values);
		Log.e("PaulDb", "inserted Default List" + rowID);
	}
	
public static final String DB_FILEPATH = "/data/de.paulwein.paul/databases/paul.db";
	
	public boolean importDatabase(String dbPath) throws IOException {
		close();
	    // Close the SQLiteOpenHelper so it will commit the created empty
	    // database to internal storage.
	    File newDb = new File(dbPath);
	    File oldDb = new File(DB_FILEPATH);
	    if (newDb.exists()) {
	        copyFile(newDb,oldDb);
	        getWritableDatabase().close();
	        return true;
	    }
	    return false;
	}
	
	public void exportDatabase() {
		 File dbFile = new File(Environment.getDataDirectory() + DB_FILEPATH);
		 File exportDir = new File(Environment.getExternalStorageDirectory(), "paul_database");
		   if (!exportDir.exists()) {
	            exportDir.mkdirs();
	         }
	         File file = new File(exportDir, dbFile.getName());

	         try {
	            file.createNewFile();
	            copyFile(dbFile, file);
	         } catch (IOException e) {
	        	e.printStackTrace();
	         }
	}
	
	private void copyFile(File src, File dst) throws IOException {
	       FileChannel inChannel = new FileInputStream(src).getChannel();
	       FileChannel outChannel = new FileOutputStream(dst).getChannel();
	       try {
	          inChannel.transferTo(0, inChannel.size(), outChannel);
	       } finally {
	          if (inChannel != null)
	             inChannel.close();
	          if (outChannel != null)
	             outChannel.close();
	       }
	    }
	
	
	

}
