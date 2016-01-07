package de.paulwein.paul.fragments;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;

public class NoteFragment extends Fragment {
	
	private TextView tv_subject;
	private TextView tv_note;
	private TextView tv_list;
	private long note_id;
	
	
	public NoteFragment(){}
	
	public NoteFragment(long id){
		note_id = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.note_layout, container,false);
		initUi(layout);
		return layout;
	}
	
	private void initUi(View layout) {
		LookAndFeel lof = new LookAndFeel(getActivity());
		Typeface tf1 = lof.getDefaultTypeFace();
		Typeface tf2 = lof.getHeadLineTypeFace();
		
		tv_subject = (TextView) layout.findViewById(R.id.tv_subject);
		tv_note = (TextView) layout.findViewById(R.id.tv_note);
		tv_list = (TextView) layout.findViewById(R.id.tv_list);
		
		tv_subject.setTypeface(tf2);
		tv_note.setTypeface(tf1);
		tv_list.setTypeface(tf1);
		
		
	}
	
	public void setNoteId(long id){
		note_id = id;
		Cursor c = getActivity().getContentResolver().query(ContentUris.withAppendedId(NotesProvider.CONTENT_URI,note_id), NotesProvider.ALL_COLUMNS, null, null, null);
		int subject_index = c.getColumnIndex(NotesColumns.NOTE_SUBJECT);
		int note_index = c.getColumnIndex(NotesColumns.NOTE_TEXT);
		int list_index = c.getColumnIndex(NotesListColumns.LIST_NAME);
		
		if(c.moveToFirst()){
			String subject = c.getString(subject_index);
			String note = c.getString(note_index);
			String list = c.getString(list_index);
			
			tv_subject.setText(subject);
			tv_note.setText(note);
			tv_list.setText(list);
		}
		c.close();
	}
	
	public void delete(){
		getActivity().getContentResolver().delete(ContentUris.withAppendedId(NotesProvider.CONTENT_URI, note_id), null, null);
		getActivity().finish();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

}
