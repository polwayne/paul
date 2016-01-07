package de.paulwein.paul.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.paulwein.paul.R;
import de.paulwein.paul.activities.NoteActivity;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;

public class NotesListFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
		
	
	private SimpleCursorAdapter adapter;
	private long mListID;
	
	
	public static Fragment newInstance(long listID) {
		NotesListFragment f = new NotesListFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("listID", listID);
        f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    
	    mListID = getArguments() != null ? getArguments().getLong("listID") : 1;	    
	    
	    String[] uiBindFrom = { NotesColumns.NOTE_SUBJECT };
	    int[] uiBindTo = { android.R.id.text1 };

	 
	    adapter = new SimpleCursorAdapter(
	            getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,
	            null, uiBindFrom, uiBindTo,
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    setListAdapter(adapter);
	    getLoaderManager().initLoader(1, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState); 
	}
	
	@Override 
	public void onViewCreated(View v, Bundle onSavedInstanceState){
		getListView().setBackgroundColor(getResources().getColor(R.color.blue));
	    getListView().setCacheColorHint(Color.TRANSPARENT);		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getLoaderManager().restartLoader(1, null, this);
	}
	
	@Override
	public void onListItemClick(ListView lv,View v,int position,long id){
		super.onListItemClick(lv, v, position, id);
		Intent noteIntent = new Intent(getActivity(),NoteActivity.class);
		noteIntent.putExtra(Config.EXTRA_NOTE_ID, id);
		startActivity(noteIntent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {	
		
		String selection = NotesColumns.LIST_ID + "=?";
		String[] selectionArgs = new String[]{String.valueOf(mListID)};
		String sortOrder = NotesColumns.TABLE_NAME + "." + NotesColumns.ID + " ASC";
	    CursorLoader cursorLoader = new CursorLoader(getActivity(),
	            NotesProvider.CONTENT_URI, NotesProvider.ALL_COLUMNS, selection, selectionArgs, sortOrder);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		adapter.swapCursor(cursor);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
}
