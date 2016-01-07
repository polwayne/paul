package de.paulwein.paul.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
	
	private TextView tv_pb;
	private TextView tv_paul;
	private Handler mHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mHandler = new Handler();
        initUi();
    }
    
    private void initUi(){
		ILookAndFeel lof = new LookAndFeel(this);
		Typeface tf1 = lof.getOtherTypeFace();
		Typeface tf2 = lof.getHeadLineTypeFace();
    	tv_pb = (TextView) findViewById(R.id.tv_pb);
    	tv_pb.setTypeface(tf1);
    	tv_paul = (TextView) findViewById(R.id.tv_paul);
    	boolean gender = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Config.PREF_AVATAR_GENDER, true);
    	tv_paul.setText(gender ? R.string.paul : R.string.paola);
    	tv_paul.setTypeface(tf2);
    	mHandler.postDelayed(startBouncing, 1000);
    }
    
    private Runnable startBouncing = new Runnable() {
		
		@Override
		public void run() {
			Animation bounce_animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.bounce);
			tv_paul.startAnimation(bounce_animation);
			tv_paul.setVisibility(View.VISIBLE);
			mHandler.postDelayed(startNextActivity, 2500);
		}
	};
	
	private Runnable startNextActivity = new Runnable() {
		
		@Override
		public void run() {
			boolean app_locked = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).
									getBoolean(Config.PREF_APP_LOCKED, false);
			
			Intent intent = new Intent(SplashActivity.this,app_locked ? LockScreenActivity.class : MainActivity.class);
			startActivity(intent);
			finish();			
		}
	};
}