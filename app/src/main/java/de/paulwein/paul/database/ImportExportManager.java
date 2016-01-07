package de.paulwein.paul.database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;

public class ImportExportManager implements INotesExport,Runnable {
	
	private Context mContext;
	
	public ImportExportManager(Context context){
		mContext = context;
	}

	@Override
	public void exportNotes() {
		Cursor c = mContext.getContentResolver().query(NotesProvider.CONTENT_URI, NotesProvider.ALL_COLUMNS, null, null, null);
		int id_index = c.getColumnIndex(NotesColumns.ID);
		int subject_index = c.getColumnIndex(NotesColumns.NOTE_SUBJECT);
		int note_index = c.getColumnIndex(NotesColumns.NOTE_TEXT);
		int timestamp_index = c.getColumnIndex(NotesColumns.CREATED_TIMESTAMP);
		int list_index = c.getColumnIndex(NotesListColumns.LIST_NAME);
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/notes.paul",true));
		} catch (IOException e1) {	e1.printStackTrace(); }
		while(c.moveToNext()){
			
			long id = c.getLong(id_index);
			String subject = c.getString(subject_index);
			String note = c.getString(note_index);
			String list = c.getString(list_index);
			long created = c.getLong(timestamp_index);
			
			StringBuilder sb = new StringBuilder();
			sb.append("ID = " + id  + " List = " + list).append("\n");
			sb.append(subject).append("\n");
			sb.append(note).append("\n");
			sb.append(created).append("\n");
			
			String toWrite = sb.toString();
			try { 
				writer.write(toWrite);
				writer.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		c.close();
	}

	@Override
	public void run() {
		exportNotes();
	}
		

}
