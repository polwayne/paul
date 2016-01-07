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
import de.paulwein.paul.fragments.NoteFragment;
import de.paulwein.paul.fragments.AlertDialogFragment.IAlertDialog;

public class NoteActivity extends Activity implements IAlertDialog{
	
	private Long note_id;
	
	NoteFragment noteFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_screen);
		noteFragment = (NoteFragment) getFragmentManager().findFragmentById(R.id.notes_fragment);
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(Config.EXTRA_NOTE_ID)){
			note_id = getIntent().getExtras().getLong(Config.EXTRA_NOTE_ID);
			noteFragment.setNoteId(note_id);
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
    	 Intent editIntent = new Intent(this,EditNotesActivity.class);
    	 editIntent.putExtra(Config.EXTRA_NOTE_ID, note_id);
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
	noteFragment.delete(); 
 }
 
 public void doNegativeClick(){}
}
