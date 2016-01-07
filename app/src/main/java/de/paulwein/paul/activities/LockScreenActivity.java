package de.paulwein.paul.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.intelligence.VoiceRecognizer;
import de.paulwein.paul.views.VisualizerView;

public class LockScreenActivity extends Activity implements VoiceRecognizer.IVoiceRecognition{
	
	private VoiceRecognizer mVoiceRecognizer;
	private ImageButton mUnlockButton;
	private VisualizerView mVisualizer;
	private int filter = 0;
	private Animation buttonOutAnimation;
	private Animation buttonInAnimation;
	private Animation viszalizerOutAnimation;
	private Animation viszalizerInAnimation;
	private int tryCounter = 0;
	private Handler mHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_screen);
		mHandler = new Handler();
		mUnlockButton = (ImageButton) findViewById(R.id.ib_unlock);
		mVoiceRecognizer = new VoiceRecognizer(this, this);
		mVisualizer = (VisualizerView) findViewById(R.id.visualizerView1);
		createAnimations();
	}
	
	public void unlock(View button){
		mUnlockButton.startAnimation(buttonOutAnimation);
		mVisualizer.startAnimation(viszalizerInAnimation);
	}

	@Override
	public void onRecognized(String result) {
		if(result.equals("omnibus"))
		{
			Intent unlockIntent = new Intent(this,MainActivity.class);
			startActivity(unlockIntent);
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean(Config.PREF_APP_LOCKED, false).apply();
			finish();
		}
		else
		{
			if(tryCounter++ < 2){
			Toast.makeText(this, R.string.sorry_kein_einlass ,Toast.LENGTH_LONG).show();
			mHandler.postDelayed(resetBackground, 300);
			mVoiceRecognizer.startRecognizing();
			}
			else{
				mVisualizer.startAnimation(viszalizerOutAnimation);
				mUnlockButton.startAnimation(buttonInAnimation);
				tryCounter = 0;
			}
			mVisualizer.setBackgroundResource(R.drawable.round_container_red);
		}
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		mUnlockButton.setEnabled(true);
	}
	
	@Override
	public void onDestroy(){
		mVoiceRecognizer.destroy();
		super.onDestroy();
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		if(filter++ % 10 == 0)
		mVisualizer.updateVisualizer(buffer);
	}
	
	private void createAnimations(){
		buttonOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_down_from_center);
		buttonOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				mUnlockButton.setVisibility(View.GONE);
			}
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
		});
		
		viszalizerInAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_down_from_top);
		viszalizerInAnimation.setAnimationListener(new AnimationListener() {
		public void onAnimationStart(Animation animation) {
			mVisualizer.setVisibility(View.VISIBLE);	
		}
		public void onAnimationEnd(Animation animation) {
			mVoiceRecognizer.startRecognizing();
		}
		public void onAnimationRepeat(Animation animation) {}
		});
		
		buttonInAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_up_to_center);
		buttonInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {}
			public void onAnimationStart(Animation animation) {
				mUnlockButton.setVisibility(View.VISIBLE);
			}
			public void onAnimationRepeat(Animation animation) {}
		});
		
		viszalizerOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_up_to_top);
		viszalizerOutAnimation.setAnimationListener(new AnimationListener() {
		public void onAnimationStart(Animation animation) {}
		public void onAnimationEnd(Animation animation) {
			mVisualizer.setVisibility(View.GONE);
			mVisualizer.setBackgroundResource(R.drawable.round_container);
		}
		public void onAnimationRepeat(Animation animation) {}
		});
		
	}
	
	private Runnable resetBackground = new Runnable() {
		
		@Override
		public void run() {
			mVisualizer.setBackgroundResource(R.drawable.round_container);
		}
	};

}
