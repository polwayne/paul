package de.paulwein.paul.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.paulwein.paul.R;
import de.paulwein.paul.activities.TaskActivity;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.contentprovider.TasksProvider;
import de.paulwein.paul.database.DatabaseTables.TasksColumns;

public class TasksListFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {
		
	
	private SimpleCursorAdapter adapter;
	private long mListID;
	
	
	public static Fragment newInstance(long listID) {
		TasksListFragment f = new TasksListFragment();
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
	    
	    String[] uiBindFrom = { TasksColumns.TASK_SUBJECT};
	    int[] uiBindTo = { android.R.id.text1 };
	 
	    adapter = new SimpleCursorAdapter(
	            getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,
	            null, uiBindFrom, uiBindTo,
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    adapter.setViewBinder(new CheckedViewBinder());
	    setListAdapter(adapter);
	    getLoaderManager().initLoader(1, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState); 
	}
	
	@Override 
	public void onViewCreated(View v, Bundle onSavedInstanceState){
		getListView().setBackgroundColor(getResources().getColor(R.color.orange));
	    getListView().setCacheColorHint(Color.TRANSPARENT);	
	    getListView().setOnItemLongClickListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getLoaderManager().restartLoader(1, null, this);
	}
	
	@Override
	public void onListItemClick(ListView lv,View v,int position,long id){
		super.onListItemClick(lv, v, position, id);
		Intent taskIntent = new Intent(getActivity(),TaskActivity.class); 
		taskIntent.putExtra(Config.EXTRA_TASK_ID, id);
		startActivity(taskIntent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {	
		
		String selection = TasksColumns.LIST_ID + "=?";
		String[] selectionArgs = new String[]{String.valueOf(mListID)};
		String sortOrder = TasksColumns.TABLE_NAME + "." + TasksColumns.ID + " ASC";
	    CursorLoader cursorLoader = new CursorLoader(getActivity(),
	            TasksProvider.CONTENT_URI, TasksProvider.ALL_COLUMNS, selection, selectionArgs, sortOrder);
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
	
	private class CheckedViewBinder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			
			CheckedTextView ctv = (CheckedTextView) view;
			
			int task_index =  cursor.getColumnIndex(TasksColumns.TASK_SUBJECT);
			int done_index =  cursor.getColumnIndex(TasksColumns.DONE);
			String task_subject = cursor.getString(task_index);
			boolean done = cursor.getInt(done_index) == 1;
			ctv.setText(task_subject);
			ctv.setChecked(done);
			if(done)
				ctv.setPaintFlags(ctv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			return true;
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
			long id) {
		
		Log.e("TAG","longClick " + pos + " # " + id);
		CheckedTextView ctv = (CheckedTextView) v;
		ContentValues values = new ContentValues();
		values.put(TasksColumns.DONE, !ctv.isChecked());
		values.put(TasksColumns.DONE_TIME, System.currentTimeMillis());
		getActivity().getContentResolver().update(ContentUris.withAppendedId(TasksProvider.CONTENT_URI,id), values, null, null);
		getLoaderManager().restartLoader(1, null, this);
		return true;
	}
}
