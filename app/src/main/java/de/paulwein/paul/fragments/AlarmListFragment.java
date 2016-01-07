package de.paulwein.paul.fragments;

import java.text.Format;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.AlarmsProvider;
import de.paulwein.paul.database.DatabaseTables.AlarmColumns;

public class AlarmListFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
		
	
	private AlarmAdapter adapter;
	private long mAlarmType;
	private Format dateFormat;
	
	
	public static Fragment newInstance(long listID) {
		AlarmListFragment f = new AlarmListFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("alarmType", listID);
        f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    
	    mAlarmType = getArguments() != null ? getArguments().getLong("alarmType") : 1;	    
	    
	    String[] uiBindFrom = { AlarmColumns.ALARM_SUBJECT, AlarmColumns.ALARM_TIME };
	    int[] uiBindTo = { R.id.textView1 ,R.id.textView2 };
	    dateFormat = DateFormat.getTimeFormat(getActivity());
	    adapter = new AlarmAdapter(
	            getActivity().getApplicationContext(), R.layout.alarm_list_item,
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
		// TODO click
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {	
		
		String selection = AlarmColumns.ALARM_TYPE + "=?";
		String[] selectionArgs = new String[]{String.valueOf(mAlarmType)};
		String sortOrder = AlarmColumns.TABLE_NAME + "." + AlarmColumns.ALARM_TIME + " ASC";
	    CursorLoader cursorLoader = new CursorLoader(getActivity(),
	            AlarmsProvider.CONTENT_URI, AlarmColumns.ALL_COLUMNS, selection, selectionArgs, sortOrder);
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
	
	public class AlarmAdapter extends SimpleCursorAdapter{

		public AlarmAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}
		
	    @Override
	    public View newView(Context context, Cursor cursor, ViewGroup parent) {

	        final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(R.layout.alarm_list_item, parent, false);
	        return v;
	    }

	    @Override
	    public void bindView(View v, Context context, Cursor c) {
	        
	        int alarmTimeIndex = c.getColumnIndex(AlarmColumns.ALARM_TIME);
	        
	        long alarmTime = c.getLong(alarmTimeIndex);

	        TextView tv_alarm_time = (TextView) v.findViewById(R.id.textView2);
	        tv_alarm_time.setText(dateFormat.format(alarmTime));	 
	    }
		
	}
}
