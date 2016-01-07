package de.paulwein.paul.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.LocationsProvider;
import de.paulwein.paul.database.DatabaseTables.LocationsColumns;

public class WifiLocationReceiver extends BroadcastReceiver {
	
	private NotificationManager mNotificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String title = "x";
		String subject = "y";

		mNotificationManager = (NotificationManager) context
			    .getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(action == null) return;
		
		if(action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)){	
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifi.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			Log.e("SSID","ssid = " + ssid);
			Cursor c = context.getContentResolver().
				query(LocationsProvider.CONTENT_URI, LocationsProvider.ALL_COLUMNS, LocationsColumns.LOCATION_IDENTIFIER + "=?", new String[]{ssid},null);
			if(c.moveToFirst()){
				if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
					Log.e("SSID","cursor not empty");
				}
				else {
					title = "pass auf dich auf!";
				}
				
				Notification.Builder builder = new Notification.Builder(context)
		        .setContentTitle(title)
		        .setContentText(subject)
		        .setDefaults(Notification.DEFAULT_VIBRATE)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setAutoCancel(true);
				
				Notification notification = null;
				notification = builder.build();
				mNotificationManager.notify(1, notification);
			}
			else
				Log.e("SSID","cursor empty");
			c.close();
		} 
	
	}
	
}