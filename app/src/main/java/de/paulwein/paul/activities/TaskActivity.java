package de.paulwein.paul.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.fragments.AlertDialogFragment;
import de.paulwein.paul.fragments.AlertDialogFragment.IAlertDialog;
import de.paulwein.paul.fragments.TaskFragment;

public class TaskActivity extends Activity implements IAlertDialog{
	
	private Long task_id;
	
	TaskFragment taskFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_screen);
		taskFragment = (TaskFragment) getFragmentManager().findFragmentById(R.id.task_fragment);
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(Config.EXTRA_TASK_ID)){
			task_id = getIntent().getExtras().getLong(Config.EXTRA_TASK_ID);
			taskFragment.setTaskId(task_id);
		}
	}
	

	   @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.note_menu, menu);
	        return true;
	    }
	
 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
     case R.id.action_edit:
    	 Intent editIntent = new Intent(this,EditTaskActivity.class);
    	 editIntent.putExtra(Config.EXTRA_TASK_ID, task_id);
    	 startActivity(editIntent);
    	 finish();
     	return true;
     case R.id.action_delete:
    	 showDialog();
    	 return true;
     }
     
     return false;
 }
 
 public void showDialog(){
	 DialogFragment newFragment = AlertDialogFragment.newInstance(
	            R.string.loeschen_dialog_text,this);
	    newFragment.show(getFragmentManager(), "dialog");
 }
 
 public void doPositiveClick()
 {
	taskFragment.delete(); 
 }
 
 public void doNegativeClick(){}
}
