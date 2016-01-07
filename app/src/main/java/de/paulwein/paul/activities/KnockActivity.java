package de.paulwein.paul.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.sensor.KnockDetector;
import de.paulwein.paul.sensor.KnockDetector.Callback;

public class KnockActivity extends Activity implements Callback{
	
	private KnockDetector knockDetector;
	private long lastKnock = -1L;
	private TextView tv_timediff;
	private Intent nextSongIntent;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knock);
		tv_timediff = (TextView) findViewById(R.id.tv_timediff);
		
		nextSongIntent = new Intent();
		nextSongIntent.setAction("com.android.music.musicservicecommand.next");
		nextSongIntent.putExtra("command", "next"); // or "next" or "previous"
		
		knockDetector = new KnockDetector(this, 1.2d, 20, this);
	}

	 @Override
	  public void onDestroy() {
	    super.onDestroy();
	    knockDetector.close();
	  }

	@Override
	public void shakingStarted() {
		long diff = -1L;
		long currentTime = System.currentTimeMillis();
		if(lastKnock != -1){
			diff = currentTime - lastKnock;
		}
//		Log.e("TAG", "knock started! " + diff);		
		tv_timediff.setText(String.valueOf(diff));
		if(diff != -1 && diff < 300){
			sendBroadcast(nextSongIntent);
		}
		lastKnock = currentTime;
		
	}

	@Override
	public void shakingStopped() {}
	
}
