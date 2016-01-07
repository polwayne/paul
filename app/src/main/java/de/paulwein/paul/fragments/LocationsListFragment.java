package de.paulwein.paul.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.LocationsProvider;
import de.paulwein.paul.database.DatabaseTables.LocationsColumns;

public class LocationsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
		
	
	private SimpleCursorAdapter adapter;	
	
	public static Fragment newInstance(long listID) {
		LocationsListFragment f = new LocationsListFragment();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    	    
	    String[] uiBindFrom = { LocationsColumns.LOCATION_NAME};
	    int[] uiBindTo = { android.R.id.text1 };

	    adapter = new SimpleCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.simple_list_item_color,
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
//		Intent noteIntent = new Intent(getActivity(),NoteActivity.class);
//		noteIntent.putExtra(Config.EXTRA_NOTE_ID, id);
//		startActivity(noteIntent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {	
		
	    CursorLoader cursorLoader = new CursorLoader(getActivity(),
	            LocationsProvider.CONTENT_URI, LocationsProvider.ALL_COLUMNS, null, null, LocationsColumns.LOCATION_NAME + " ASC");
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
