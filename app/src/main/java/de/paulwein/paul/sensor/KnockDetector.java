package de.paulwein.paul.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class KnockDetector {
	
	private SensorManager mgr=null;
	private long lastShakeTimestamp=0;
	private double threshold=1.0d;
	private long gap=0;
	private KnockDetector.Callback cb = null;
	private boolean onDesk = false;
	
	public KnockDetector(Context ctxt, double threshold, long gap, KnockDetector.Callback cb) {
		this.threshold = threshold*threshold;
		this.threshold = this.threshold * SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH;
		this.gap = gap;
		this.cb = cb;
		
		mgr = (SensorManager)ctxt.getSystemService(Context.SENSOR_SERVICE);
		mgr.registerListener(listener, mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
		mgr.registerListener(listener, mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
	}
	
	public void close() {
		mgr.unregisterListener(listener);
	}
	
	private void isShaking() {
		long now = SystemClock.uptimeMillis();
		
		if (lastShakeTimestamp==0) {
			lastShakeTimestamp = now;
			
			if (cb != null) {
				cb.shakingStarted();
			}
		}
		else {
			lastShakeTimestamp=now;
		}
	}
	
	private void isNotShaking() {
		long now = SystemClock.uptimeMillis();
		
		if (lastShakeTimestamp > 0) {
			if (now - lastShakeTimestamp > gap) {
				lastShakeTimestamp=0;
				
				if (cb != null) {
					cb.shakingStopped();
				}
			}
		}
	}
	
	public interface Callback {
		void shakingStarted();
		void shakingStopped();
	}
	
	private SensorEventListener listener=new SensorEventListener() {
		public void onSensorChanged(SensorEvent e) {
			if (e.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				if(!onDesk)
					return;
//				Log.e("TAG", "0: " + e.values[0] + " 1: " + e.values[1] + " 2: " + e.values[2] );
				
				if (e.values[1] > 0.004) {
					isShaking();
				}
				else {
					isNotShaking();
				}
			}
			else if(e.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				onDesk = e.values[2] > 9 && e.values[2] < 10;
			}
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// unused
		}
	};

}
