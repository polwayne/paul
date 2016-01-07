package de.paulwein.paul.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import de.paulwein.paul.R;
import de.paulwein.paul.fragments.AvatarFragment;
import de.paulwein.paul.intelligence.IKI;
import de.paulwein.paul.intelligence.KI;
import de.paulwein.paul.intelligence.VoiceRecognizer;
import de.paulwein.paul.intelligence.VoiceRecognizer.IVoiceRecognition;

public class MainActivity extends Activity implements IVoiceRecognition, OnItemClickListener{
	
	private AvatarFragment mPaulFragment;
	private IKI mKI;
	private VoiceRecognizer mVoiceRecognizer;
	private MenuItem mSpeechButton;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        initUi();
        mKI = new KI(this, mPaulFragment);
        mVoiceRecognizer = new VoiceRecognizer(this, this);
//        final String regId = GCMRegistrar.getRegistrationId(this);
//        if (regId.equals(""))
//        	GCMRegistrar.register(this, Config.GCM_SENDER_ID);
//        else 
//        	Log.e("TAG",regId);
    }
    
    private void initUi(){
    	mPaulFragment = (AvatarFragment) getFragmentManager().findFragmentById(R.id.avatar_fragment);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        		R.layout.simple_list_item_color, getResources().getStringArray(R.array.main_nav_texts)));
        mDrawerList.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mSpeechButton = menu.findItem(R.id.action_speak);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
        switch (item.getItemId()) {
        case R.id.action_speak:
        	mVoiceRecognizer.startRecognizing();
        	mSpeechButton.setEnabled(false);
        	mPaulFragment.showActionView(R.drawable.microphone);
        	return true;
        case R.id.action_notes:
        	Intent notesIntent = new Intent(this,ShowNotesActivity.class);
        	startActivity(notesIntent);
        }
		return false;
    }

	@Override
	public void onRecognized(String result) {
		mKI.respond(result);
	}

	@Override
	public void onEndOfSpeech() {
		mSpeechButton.setEnabled(true);	
		mPaulFragment.hideActionView();
	}

	@Override
	public void onError() {
		mPaulFragment.saySomething(getString(R.string.es_ist_ein_fehler_aufgetreten));
		mPaulFragment.showActionView(R.drawable.dialog_warning);
	}
	
	@Override
	public void onDestroy(){
		mVoiceRecognizer.destroy();
		super.onDestroy();
	}
	
	 @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }


	@Override
	public void onBufferReceived(byte[] buffer) {}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int itemPosition, long id) {
		switch (itemPosition) {
		case 0:
			Intent notesIntent = new Intent(this,ShowNotesActivity.class);
			startActivity(notesIntent);
			break;
		case 1:
			Intent tasksIntent = new Intent(this,ShowTasksActivity.class); 
			startActivity(tasksIntent);
			break;
		case 2:
			Intent alarmIntent = new Intent(this,AlarmActivity.class); 
			startActivity(alarmIntent);
			break;
		case 3:
			Intent locationIntent = new Intent(this,LocationsActivity.class); 
			startActivity(locationIntent);
			break;
		}		
	}
}