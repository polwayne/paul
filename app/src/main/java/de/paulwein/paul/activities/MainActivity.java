package de.paulwein.paul.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHScene;

import java.util.List;

import de.paulwein.paul.R;
import de.paulwein.paul.broadcastreceiver.WifiLocationReceiver;
import de.paulwein.paul.fragments.AvatarFragment;
import de.paulwein.paul.hue.HueSharedPreferences;
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
	private PHHueSDK phHueSDK;
	HueSharedPreferences prefs;
	private boolean mHueConnected = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        initUi();
        mKI = new KI(this, mPaulFragment);
        mVoiceRecognizer = new VoiceRecognizer(this, this);
		initHue();

		if(getIntent().hasExtra(WifiLocationReceiver.HOME)) {
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					mPaulFragment.speak(getString(R.string.welcome_home));
				}
			},400);

			h.postDelayed(new Runnable() {
				@Override
				public void run() {
					mVoiceRecognizer.startRecognizing();
					mSpeechButton.setEnabled(false);
					mPaulFragment.showActionView(R.drawable.microphone);
				}
			},5000);

		}
    }

	private void initHue() {
		phHueSDK = PHHueSDK.create();
		phHueSDK.setAppName(getString(R.string.app_name));
		phHueSDK.setDeviceName(Build.MODEL);
		phHueSDK.getNotificationManager().registerSDKListener(listener);

		prefs = HueSharedPreferences.getInstance(getApplicationContext());
		String lastIpAddress   = prefs.getLastConnectedIPAddress();
		String lastUsername    = prefs.getUsername();

		// Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
		if (lastIpAddress !=null && !lastIpAddress.equals("")) {
			PHAccessPoint lastAccessPoint = new PHAccessPoint();
			lastAccessPoint.setIpAddress(lastIpAddress);
			lastAccessPoint.setUsername(lastUsername);

			if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
				phHueSDK.connect(lastAccessPoint);
			}
		}
	}

	private PHSDKListener listener = new PHSDKListener() {

		@Override
		public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {

		}

		@Override
		public void onBridgeConnected(PHBridge b, String username) {
			phHueSDK.setSelectedBridge(b);
			phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
			phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
			prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
			prefs.setUsername(username);
			mHueConnected = true;
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {

		}

		@Override
		public void onAccessPointsFound(List<PHAccessPoint> list) {

		}

		@Override
		public void onError(int i, String s) {

		}

		@Override
		public void onConnectionResumed(PHBridge phBridge) {

		}

		@Override
		public void onConnectionLost(PHAccessPoint phAccessPoint) {

		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> list) {

		}
	};

	public void toggleLights(boolean onOff){
		if(mHueConnected){
			PHBridge bridge = phHueSDK.getSelectedBridge();
			List<PHLight> lights = bridge.getResourceCache().getAllLights();
			PHLightState lightState = new PHLightState();
			lightState.setOn(onOff);
			for(PHLight l : lights){
				bridge.updateLightState(l,lightState);
			}
		}
	}

	public void activateScene(String key){
		if(mHueConnected) {
			PHBridge bridge = phHueSDK.getSelectedBridge();
			List<PHScene> scences = bridge.getResourceCache().getAllScenes();
			for (PHScene s : scences) {
				if(s.getName().toUpperCase().startsWith(key.toUpperCase())){
					bridge.activateScene(s.getSceneIdentifier(),"0",null);
					Log.e("TAG","activtaed scene " + s.getName());
					return;
				}
			}
		}
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
		PHBridge bridge = phHueSDK.getSelectedBridge();
		if (bridge != null) {

			if (phHueSDK.isHeartbeatEnabled(bridge)) {
				phHueSDK.disableHeartbeat(bridge);
			}

			phHueSDK.disconnect(bridge);
		}
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