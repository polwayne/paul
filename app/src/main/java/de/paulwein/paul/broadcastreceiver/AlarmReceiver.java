package de.paulwein.paul.broadcastreceiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String BROADCAST_INTENT = "de.paulwein.broadcastreceiver.AlarmReceiver.action.alarm";
	public static final String SNOOZE_INTENT = "de.paulwein.broadcastreceiver.AlarmReceiver.action.snooze";
	
	private NotificationManager mNotificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle extras = intent.getExtras();
		String title = extras.getString(Config.EXTRA_NOTIFICATION_TITLE);
		String subject = extras.getString(Config.EXTRA_NOTIFICATION_SUBJECT);
		mNotificationManager = (NotificationManager) context
			    .getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(action == null) return;
		
		if(action.equals(BROADCAST_INTENT)){	
			
			Uri uri = (Uri) extras.get(Config.EXTRA_ALARM_URI);
			if(uri != null)
				context.getContentResolver().delete(uri, null, null);
			
			Uri weckerSound = Uri.parse("android.resource://" +
					"de.paulwein.paul/" +R.raw.wecker);
			Notification.Builder builder = new Notification.Builder(context)
	        .setContentTitle(title)
	        .setContentText(subject)
	        .setDefaults(Notification.DEFAULT_VIBRATE)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setSound(weckerSound) 
	        .setAutoCancel(true);
			
			Notification notification = null;
			
			if(title.equals("Wecker"))
			{		
				Intent snoozeIntent = new Intent(AlarmReceiver.SNOOZE_INTENT);
				snoozeIntent.putExtra(Config.EXTRA_NOTIFICATION_TITLE, title);
				snoozeIntent.putExtra(Config.EXTRA_NOTIFICATION_SUBJECT, subject);
				builder.addAction(R.drawable.ic_action_alarm, context.getString(R.string.in_10_minuten_nochmal),  PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), snoozeIntent, 0));
			notification = new Notification.BigPictureStyle(builder)
	        .bigPicture(
	                BitmapFactory.decodeResource(context.getResources(),
	                        R.drawable.sleep_notif)).build();
			}
			else{
				notification = builder.build();
			}
			mNotificationManager.notify(1, notification);
		} 
		else if(action.equals(SNOOZE_INTENT)){
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent = new Intent(AlarmReceiver.BROADCAST_INTENT);
			alarmIntent.putExtra(Config.EXTRA_NOTIFICATION_TITLE, title);
			alarmIntent.putExtra(Config.EXTRA_NOTIFICATION_SUBJECT, subject);
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * 10, PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), alarmIntent, 0));
			mNotificationManager.cancel(1);
		}
	
	}
	
}