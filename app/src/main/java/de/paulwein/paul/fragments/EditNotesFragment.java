package de.paulwein.paul.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.NotesListProvider;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;

public class EditNotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private EditText et_subject;
	private EditText et_note;
	private Spinner spinner_list;
	private SimpleCursorAdapter adapter;
	private long note_id = -1;
	private long list_id = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.note_mask, container,false);
		initUi(layout);
		return layout;
	}
	
	private void initUi(View layout) {
		et_subject = (EditText) layout.findViewById(R.id.et_subject);
		et_note = (EditText) layout.findViewById(R.id.et_note);
		spinner_list = (Spinner) layout.findViewById(R.id.spinner_list);
		getLoaderManager().initLoader(1, null, this);		
		adapter = new SimpleCursorAdapter( getActivity(),
				R.layout.simple_dropdown_item_color,
                null,
                new String[] { NotesListColumns.LIST_NAME },
                new int[] { android.R.id.text1 },
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		spinner_list.setAdapter(adapter);
	}
	
	private boolean validateInput(){
		
		boolean result = true;
		
		String subject = et_subject.getText().toString();
		String note = et_note.getText().toString();
		
		if(TextUtils.isEmpty(subject)){
			et_subject.setError(getActivity().getString(R.string.bitte_betreff_angeben));
			result = false;
		}
		if(TextUtils.isEmpty(note)){
			et_note.setError(getActivity().getString(R.string.bitte_notiz_eintragen));
			result = false;
		}
		
		return result;
	}
	
	public void initForUpdate(long id){
		note_id = id;
		Cursor c = getActivity().getContentResolver().query(ContentUris.withAppendedId(NotesProvider.CONTENT_URI,note_id), NotesProvider.ALL_COLUMNS, null, null, null);
		int subject_index = c.getColumnIndex(NotesColumns.NOTE_SUBJECT);
		int note_index = c.getColumnIndex(NotesColumns.NOTE_TEXT);
		int list_index = c.getColumnIndex(NotesColumns.LIST_ID);
		
		if(c.moveToFirst()){
			String subject = c.getString(subject_index);
			String note = c.getString(note_index);
			list_id = c.getLong(list_index);
			
			et_subject.setText(subject);
			et_note.setText(note);
		}
		c.close();
	}
	
	public boolean saveOrUpdateNote(){
		
		if(!validateInput()) return false;
		
		String subject = et_subject.getText().toString();
		String note = et_note.getText().toString();
		long list_id = spinner_list.getSelectedItemId();
		long timeStamp = System.currentTimeMillis();
		
		ContentValues values = new ContentValues();
		values.put(NotesColumns.NOTE_SUBJECT, subject);
		values.put(NotesColumns.NOTE_TEXT, note);
		values.put(NotesColumns.LIST_ID, list_id);
		values.put(NotesColumns.CREATED_TIMESTAMP, timeStamp);
		if(note_id < 0)
			getActivity().getContentResolver().insert(NotesProvider.CONTENT_URI, values);
		else
			getActivity().getContentResolver().update(ContentUris.withAppendedId(NotesProvider.CONTENT_URI,note_id), values,null,null);
		
		return true;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
	            NotesListProvider.CONTENT_URI, NotesListColumns.ALL_COLUMNS, null, null, null);
		return cursorLoader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		if(list_id != -1){
			for(int i = 0; i < adapter.getCount(); i++){
				if(spinner_list.getItemIdAtPosition(i) == list_id){
					spinner_list.setSelection(i);
				}
			}
		}
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		
	}

}
