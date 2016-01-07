package de.paulwein.paul.fragments;

import java.util.Calendar;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.NotesListProvider;
import de.paulwein.paul.contentprovider.TasksProvider;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;
import de.paulwein.paul.database.DatabaseTables.TasksColumns;

public class EditTaskFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private Switch switch_done;
	private EditText et_subject;
	private EditText et_task;
	private Spinner spinner_list;
	private RatingBar rb_priority;
	private DatePicker dp_settlement;
	private RadioGroup rg_reminder;
	private SimpleCursorAdapter adapter;
	private long task_id = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.task_mask, container,false);
		initUi(layout);
		return layout;
	}
	
	private void initUi(View layout) {
		ILookAndFeel lof = new LookAndFeel(getActivity());
		TextView tv_lbl_category = (TextView) layout.findViewById(R.id.tv_lbl_category);
		tv_lbl_category.setTypeface(lof.getHeadLineTypeFace());
		TextView tv_lbl_priority = (TextView) layout.findViewById(R.id.tv_lbl_priority);
		tv_lbl_priority.setTypeface(lof.getHeadLineTypeFace());
		TextView tv_lbl_reminder = (TextView) layout.findViewById(R.id.tv_lbl_reminder);
		tv_lbl_reminder.setTypeface(lof.getHeadLineTypeFace());
		TextView tv_lbl_date = (TextView) layout.findViewById(R.id.tv_lbl_date);
		tv_lbl_date.setTypeface(lof.getHeadLineTypeFace());
		
		switch_done = (Switch) layout.findViewById(R.id.switch_done);
		et_subject = (EditText) layout.findViewById(R.id.et_subject);
		et_task = (EditText) layout.findViewById(R.id.et_task);
		spinner_list = (Spinner) layout.findViewById(R.id.spinner_list);
		rb_priority = (RatingBar) layout.findViewById(R.id.rb_priority);
		rg_reminder = (RadioGroup) layout.findViewById(R.id.rg_reminder);
		dp_settlement = (DatePicker) layout.findViewById(R.id.dp_settlement);
		
		getLoaderManager().initLoader(1, null, this);		
		adapter = new SimpleCursorAdapter( getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[] { NotesListColumns.LIST_NAME },
                new int[] { android.R.id.text1 },
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		spinner_list.setAdapter(adapter);
	}
	
	private boolean validateInput(){
		
		boolean result = true;
		
		String subject = et_subject.getText().toString();
		String task = et_task.getText().toString();
		
		if(TextUtils.isEmpty(subject)){
			et_subject.setError(getActivity().getString(R.string.bitte_betreff_angeben));
			result = false;
		}
		if(TextUtils.isEmpty(task)){
			et_task.setError(getActivity().getString(R.string.bitte_eine_aufgabe_eintragen));
			result = false;
		}
		
		return result;
	}
	
	public void initForUpdate(long id){
		task_id = id;
		Cursor c = getActivity().getContentResolver().query(ContentUris.withAppendedId(TasksProvider.CONTENT_URI,task_id), TasksProvider.ALL_COLUMNS, null, null, null);
		int subject_index = c.getColumnIndex(TasksColumns.TASK_SUBJECT);
		int task_index = c.getColumnIndex(TasksColumns.TASK_TEXT);
		int settlement_index = c.getColumnIndex(TasksColumns.SETTLEMENT_DATE);
		int priority_index = c.getColumnIndex(TasksColumns.PRIORITY);
		int reminder_index = c.getColumnIndex(TasksColumns.REMINDER);
		int list_index = c.getColumnIndex(NotesListColumns.LIST_NAME);
		int done_index = c.getColumnIndex(TasksColumns.DONE);
		
		if(c.moveToFirst()){
			String subject = c.getString(subject_index);
			String task = c.getString(task_index);
			String list = c.getString(list_index);
			long settlement_date = c.getLong(settlement_index);
			int priority = c.getInt(priority_index);
			int reminder = c.getInt(reminder_index);
			boolean done = c.getInt(done_index) == 1;
			
			et_subject.setText(subject);
			et_task.setText(task);
			switch_done.setChecked(done);
			rb_priority.setRating(priority);
			
			for(int i = 0; i < adapter.getCount(); i++){
				if(spinner_list.getItemIdAtPosition(i) == task_id){
					spinner_list.setSelection(i);
					break;
				}
			}
		}
		c.close();
	}
	
	public boolean saveOrUpdateNote(){
		
		if(!validateInput()) return false;
		
		String subject = et_subject.getText().toString();
		String task = et_task.getText().toString();
		long list_id = spinner_list.getSelectedItemId();
		long timeStamp = System.currentTimeMillis();
		int reminder = 0;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dp_settlement.getDayOfMonth());
		cal.set(Calendar.MONTH, dp_settlement.getMonth());
		cal.set(Calendar.YEAR, dp_settlement.getYear());
		long settlementDate = cal.getTimeInMillis();
		switch (rg_reminder.getCheckedRadioButtonId()){
			case R.id.r_none:
				reminder = 0;
				break;
			case R.id.r_ten_mins:
				reminder = 1;
				break;
			case R.id.r_one_day:
				reminder = 2;
				break;
			default:
				break;
		}
		
		ContentValues values = new ContentValues();
		values.put(TasksColumns.TASK_SUBJECT, subject);
		values.put(TasksColumns.TASK_TEXT, task);
		values.put(TasksColumns.LIST_ID, list_id);
		values.put(TasksColumns.CREATED_TIMESTAMP, timeStamp);
		values.put(TasksColumns.PRIORITY, Math.round(rb_priority.getRating()));
		values.put(TasksColumns.DONE, switch_done.isChecked());
		values.put(TasksColumns.REMINDER, reminder);
		values.put(TasksColumns.SETTLEMENT_DATE, settlementDate);
		if(task_id < 0)
			getActivity().getContentResolver().insert(TasksProvider.CONTENT_URI, values);
		else
			getActivity().getContentResolver().update(ContentUris.withAppendedId(TasksProvider.CONTENT_URI,task_id), values,null,null);
		
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
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		
	}

}
