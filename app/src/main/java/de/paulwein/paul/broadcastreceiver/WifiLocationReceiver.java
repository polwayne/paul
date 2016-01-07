package de.paulwein.paul.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import de.paulwein.paul.R;
import de.paulwein.paul.activities.MainActivity;
import de.paulwein.paul.contentprovider.LocationsProvider;
import de.paulwein.paul.database.DatabaseTables.LocationsColumns;

public class WifiLocationReceiver extends BroadcastReceiver {

	public static final String HOME = "home";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String title = "x";
		String subject = "y";

		NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if(info != null && info.isConnected()) {
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			Cursor c = context.getContentResolver().query(LocationsProvider.CONTENT_URI, LocationsProvider.ALL_COLUMNS, LocationsColumns.LOCATION_IDENTIFIER + "=?", new String[]{ssid},null);
			while(c.moveToNext()){
				String location = c.getString(c.getColumnIndex(LocationsColumns.LOCATION_NAME));
				String location_ssid = c.getString(c.getColumnIndex(LocationsColumns.LOCATION_IDENTIFIER));
				if(location.equalsIgnoreCase("zuhause") && location_ssid.equalsIgnoreCase(ssid)){
					Intent startPaul = new Intent(context, MainActivity.class);
					startPaul.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startPaul.putExtra(HOME,true);
					context.startActivity(startPaul);
					break;
				}
			}
			c.close();
				/*
				mNotificationManager = (NotificationManager) context
			    .getSystemService(Context.NOTIFICATION_SERVICE);

				Notification.Builder builder = new Notification.Builder(context)
		        .setContentTitle(title)
		        .setContentText(subject)
		        .setDefaults(Notification.DEFAULT_VIBRATE)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setAutoCancel(true);

				Notification notification = null;
				notification = builder.build();
				mNotificationManager.notify(1, notification);*/
			}
	}
	
}