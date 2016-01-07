package de.paulwein.paul.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.fragments.EditNotesFragment;

public class EditNotesActivity extends Activity {
	
	private EditNotesFragment mEditNotesFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note_screen);
		mEditNotesFragment = (EditNotesFragment) getFragmentManager().findFragmentById(R.id.note_fragment);
		LookAndFeel lof = new LookAndFeel(this);
		Typeface tf2 = lof.getHeadLineTypeFace();
    	TextView tv_title = (TextView) findViewById(R.id.tv_title);
    	tv_title.setTypeface(tf2);
    	checkUpdate();
	}
	
	private void checkUpdate(){
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(Config.EXTRA_NOTE_ID)){
			long id = extras.getLong(Config.EXTRA_NOTE_ID);
			mEditNotesFragment.initForUpdate(id);
		}
	}
	

	   @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.edit_note_menu, menu);
	        return true;
	    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_save:
        	if(mEditNotesFragment.saveOrUpdateNote())
        		finish();
        	return true;
        }
        
        return false;
    }

}
