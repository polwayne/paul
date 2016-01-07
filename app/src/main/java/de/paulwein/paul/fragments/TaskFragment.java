package de.paulwein.paul.fragments;

import java.text.Format;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.TasksProvider;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;
import de.paulwein.paul.database.DatabaseTables.TasksColumns;

public class TaskFragment extends Fragment {
	
	private TextView tv_subject;
	private TextView tv_task;
	private TextView tv_list;
	private RatingBar rb_priority;
	private RadioButton r_none;
	private RadioButton r_ten_mins;
	private RadioButton r_one_day;
	private TextView tv_settlement_date;
	private Switch switch_done;
	private long task_id;
	
	
	public TaskFragment(){}
	
	public TaskFragment(long id){
		task_id = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.task_layout, container,false);
		initUi(layout);
		return layout;
	}
	
	private void initUi(View layout) {
		LookAndFeel lof = new LookAndFeel(getActivity());
		Typeface tf1 = lof.getDefaultTypeFace();
		Typeface tf2 = lof.getHeadLineTypeFace();
		
		tv_subject = (TextView) layout.findViewById(R.id.tv_subject);
		tv_list = (TextView) layout.findViewById(R.id.tv_list);
		tv_task = (TextView) layout.findViewById(R.id.tv_task);
		rb_priority = (RatingBar) layout.findViewById(R.id.rb_priority);
		r_none = (RadioButton) layout.findViewById(R.id.r_none);
		r_ten_mins = (RadioButton) layout.findViewById(R.id.r_ten_mins);
		r_one_day = (RadioButton) layout.findViewById(R.id.r_one_day);
		tv_settlement_date = (TextView) layout.findViewById(R.id.tv_settlement_date);
		switch_done = (Switch) layout.findViewById(R.id.switch_done);
		
		tv_subject.setTypeface(tf2);
		tv_task.setTypeface(tf1);
		tv_list.setTypeface(tf1);
		tv_settlement_date.setTypeface(lof.getOtherTypeFace());
		
		TextView tv_lbl_priority = (TextView) layout.findViewById(R.id.tv_lbl_priority);
		tv_lbl_priority.setTypeface(lof.getHeadLineTypeFace());
		TextView tv_lbl_reminder = (TextView) layout.findViewById(R.id.tv_lbl_reminder);
		tv_lbl_reminder.setTypeface(lof.getHeadLineTypeFace());
		TextView tv_lbl_date = (TextView) layout.findViewById(R.id.tv_lbl_date);
		tv_lbl_date.setTypeface(lof.getHeadLineTypeFace());
		
	}
	
	public void setTaskId(long id){
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
			
			Format df = DateFormat.getDateFormat(getActivity());
			
			tv_subject.setText(subject);
			tv_task.setText(task);
			tv_list.setText(list);
			tv_settlement_date.setText(df.format(settlement_date));
			switch_done.setChecked(done);
			switch_done.setEnabled(false);
			rb_priority.setRating(priority);
			rb_priority.setEnabled(false);
			
			switch(reminder){
			case 1:
				r_ten_mins.setChecked(true);
				break;			
			case 2:
				r_one_day.setChecked(true);
				break;
			default:
				r_none.setChecked(true);
			}
			
		}
		c.close();
	}
	
	public void delete(){
		getActivity().getContentResolver().delete(ContentUris.withAppendedId(TasksProvider.CONTENT_URI, task_id), null, null);
		getActivity().finish();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

}
