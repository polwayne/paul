package de.paulwein.paul.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.paulwein.paul.service.RasperryService;
import de.paulwein.paul.service.RasperryService.RasperryBinder;

public class RasperryRemoteActivity extends Activity {
	
	private RasperryService mRasperryService;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent serviceIntent = new Intent(this,RasperryService.class);
		bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		Button bt = new Button(this);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(){ public void run(){
				mRasperryService.connect();
				mRasperryService.sendMessage("Hallo 123412313");
				mRasperryService.sendMessage("Hallo 3423423");
				mRasperryService.sendMessage("sers");
				mRasperryService.close();}}.start();
			}
		});
		setContentView(bt);
	}
	
	@Override 
	public void onDestroy(){
		unbindService(mServiceConnection);
		super.onDestroy();
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			RasperryBinder binder = (RasperryBinder) service;
			mRasperryService = binder.getService();
		}
		public void onServiceDisconnected(ComponentName name) {}
	};

}
