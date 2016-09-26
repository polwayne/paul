package de.paulwein.paul.intelligence;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Instances;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import de.paulwein.paul.R;
import de.paulwein.paul.activities.AlarmActivity;
import de.paulwein.paul.activities.EditNotesActivity;
import de.paulwein.paul.activities.EditTaskActivity;
import de.paulwein.paul.activities.KnockActivity;
import de.paulwein.paul.activities.LockScreenActivity;
import de.paulwein.paul.activities.MainActivity;
import de.paulwein.paul.activities.PHHomeActivity;
import de.paulwein.paul.activities.ShowNotesActivity;
import de.paulwein.paul.activities.ShowTasksActivity;
import de.paulwein.paul.activities.SplashActivity;
import de.paulwein.paul.apis.GoogleImages;
import de.paulwein.paul.apis.GoogleWeather;
import de.paulwein.paul.apis.Wikipedia;
import de.paulwein.paul.broadcastreceiver.AlarmReceiver;
import de.paulwein.paul.broadcastreceiver.WifiLocationReceiver;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.contentprovider.AlarmsProvider;
import de.paulwein.paul.database.DatabaseTables.AlarmColumns;
import de.paulwein.paul.fragments.AvatarFragment;
import de.paulwein.paul.service.ExportService;

public class KI implements IKI {
	
	
	private Context mContext;
	private AvatarFragment mAvatarFragment;
	
	public KI(Context context, AvatarFragment avatarFragment){
		mContext = context;
		mAvatarFragment = avatarFragment;
	}

	@Override
	public void respond(String action) {
		action = action.toLowerCase();
		if(action.contains("zeige notiz"))
		{
			Intent intent = new Intent(mContext,ShowNotesActivity.class);
        	mContext.startActivity(intent);
		}
		else if(action.contains("neue notiz")){
			Intent intent = new Intent(mContext,EditNotesActivity.class);
        	mContext.startActivity(intent);
		}
		else if(action.contains("zeige aufgaben")){
			Intent intent = new Intent(mContext,ShowTasksActivity.class);
        	mContext.startActivity(intent);
		}
		else if(action.contains("neue aufgabe")){
			Intent intent = new Intent(mContext,EditTaskActivity.class);
        	mContext.startActivity(intent);
		}
		else if(action.contains("termin")){
			String termin = getNextAppointment();
			mAvatarFragment.saySomething(termin);
			mAvatarFragment.showQuickActionView(R.drawable.calendar);
		}
		else if(action.contains("zeige wecker") || action.contains("zeige erinner")){
			Intent alarmsIntent = new Intent(mContext,AlarmActivity.class);
			mContext.startActivity(alarmsIntent);
		}		
		else if(action.contains("wecke mich")){
			long time = Semantics.extractAlarmSchedule(action);
			setAlarm(time,"Wecker","Aufwachen Paul!!",AlarmColumns.ALARM_TYPE_ALARM_CLOCK);
			Format tf = DateFormat.getTimeFormat(mContext);
			mAvatarFragment.saySomething("Aufwecken um " + tf.format(time));
			mAvatarFragment.showQuickActionView(R.drawable.alarm_clock);
		}
		else if(action.contains("erinner")){
			long time = Semantics.extractAlarmSchedule(action);
			int indexOfSubject = action.indexOf(" an ");
			String subject = "Erinnere dich Paul!!";
			if(indexOfSubject > -1){
				subject = action.substring(indexOfSubject + 4);
			}
			setAlarm(time,"Erinnerung",subject,AlarmColumns.ALARM_TYPE_REMINDER);
			Format tf = DateFormat.getTimeFormat(mContext);
			mAvatarFragment.saySomething("Erinnerung um " + tf.format(time));
			mAvatarFragment.showQuickActionView(R.drawable.alarm_clock);
		}
		else if(action.contains("passwortschutz")){
			Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putBoolean(Config.PREF_APP_LOCKED, true).apply();
			Intent intent = new Intent(mContext,LockScreenActivity.class);
			mContext.startActivity(intent);
			((Activity) mContext).finish();
		}
		else if(action.contains("spiele ")){
			String search = action.replace("spiele ", "");
			search = search.replace("musik von ", "");
			Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
			intent.putExtra(SearchManager.QUERY, search);
			mContext.startActivity(intent);
		}
		else if(action.startsWith("zeige video")){
			 String search = action.replace("zeige videos ", "");
			 search = action.replace("zeige video ", "");
			 search = search.replace("von ", "");
			 Intent intent = new Intent(Intent.ACTION_SEARCH);
			 intent.setPackage("com.google.android.youtube");
			 intent.putExtra(SearchManager.QUERY, search);
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 mContext.startActivity(intent);
		}
		else if(action.startsWith("was ist") || action.startsWith("wer ist")){
			String search = action.replace("was ist", "");
			search = search.replace("wer ist", "");
			search = search.replace("eine", "");
			search = search.replace("ein", "");
			search = search.trim();
			executeWikipediaSearch(search);
		}
		else if(action.startsWith("wo ist")){
			String search = action.replace("wo ist ", "");
			Intent geoIntent = new Intent (android.content.Intent.ACTION_VIEW, Uri.parse ("geo:0,0?q=" + search));
			mContext.startActivity(geoIntent);	// Initiate lookup
		}
//		else if(action.startsWith("wie ist das wetter") || action.startsWith("wie wird das wetter")){
//			executeWeatherForecast(action);
//		}
		else if(action.startsWith("klopf")){
			Intent knock = new Intent(mContext,KnockActivity.class);
			mContext.startActivity(knock);
		}
		else if(action.startsWith("zeige mir")){
			String search = action.replace("zeige mir ", "");
			searchImage(search);
		}
		else if(action.contains("pipapo")){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			boolean gender = prefs.getBoolean(Config.PREF_AVATAR_GENDER, true);
			prefs.edit().putBoolean(Config.PREF_AVATAR_GENDER, !gender).apply();
			mContext.startActivity(new Intent(mContext,SplashActivity.class));
			((Activity) mContext).finish();
		}
		else if(action.startsWith("backup")){
			mContext.startService(new Intent(ExportService.ACTION_EXPORT_NOTES));
			mAvatarFragment.saySomething("Backup wurde angelegt!");
		} else if (action.startsWith("kalibriere lichter")){
			Intent calibrate = new Intent(mContext, PHHomeActivity.class);
			mContext.startActivity(calibrate);
		} else if(action.startsWith("licht an")){
			((MainActivity)mContext).toggleLights(true);
		} else if(action.startsWith("licht aus")){
		((MainActivity)mContext).toggleLights(false);
		} else if(action.equalsIgnoreCase("ja")) {
			if(((MainActivity)mContext).getIntent().hasExtra(WifiLocationReceiver.HOME))
				((MainActivity)mContext).toggleLights(true);
		} else if(action.startsWith("aktiviere")) {
				String[] words = action.split(" ");
				if(words.length > 1) {
					((MainActivity) mContext).activateScene(words[1]);
				}
		} else{
			mAvatarFragment.saySomething(action);
		}
	}
	
	private String getNextAppointment(){
		String[] INSTANCE_PROJECTION = new String[] {
		    Instances.BEGIN,         // 0
		    Instances.TITLE          // 1
		  };
		  
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.DAY_OF_WEEK, 3);
		long endMillis = endTime.getTimeInMillis();
		  
		Cursor cur = null;
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		cur =  mContext.getContentResolver().query(builder.build(), 
		    INSTANCE_PROJECTION, 
		    null, 
		    null, 
		    Instances.BEGIN + " ASC");
		   
		if(cur.moveToFirst()) {
		    String title = null;
		    long beginVal = 0;    
		    
		    beginVal = cur.getLong(0);
		    title = cur.getString(1);
		               
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);  
		    Format df = DateFormat.getDateFormat(mContext);
			Format tf = DateFormat.getTimeFormat(mContext);
			title += " am " + df.format(calendar.getTime())+" um "+tf.format(calendar.getTime());
		    return title;
		}
		return mContext.getString(R.string.keine_termine);
	}
	
	private void setAlarm(long time, String title, String subject,int type){
		ContentValues values = new ContentValues();
		values.put(AlarmColumns.ALARM_TIME, time);
		values.put(AlarmColumns.ENABLED, 1);
		values.put(AlarmColumns.ALARM_SUBJECT, subject);
		values.put(AlarmColumns.ALARM_TYPE, type);
		Uri uri = mContext.getContentResolver().insert(AlarmsProvider.CONTENT_URI, values);
		
		
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(AlarmReceiver.BROADCAST_INTENT);
		alarmIntent.putExtra(Config.EXTRA_NOTIFICATION_TITLE, title);
		alarmIntent.putExtra(Config.EXTRA_NOTIFICATION_SUBJECT, subject);
		alarmIntent.putExtra(Config.EXTRA_ALARM_URI, uri);
		am.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(mContext, (int) System.currentTimeMillis(), alarmIntent, 0));
		
	}
	
	private void executeWikipediaSearch(String search){
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... params) {
				String searchQuery = params[0];
				try { searchQuery = URLEncoder.encode(searchQuery,"iso-8859-1"); } 
				catch (UnsupportedEncodingException e) { e.printStackTrace(); }
				String result = new Wikipedia().search(searchQuery);
				return result;
			}
			
			@Override
			public void onPostExecute(String result){
				mAvatarFragment.speak(result);
				mAvatarFragment.showQuickActionView(R.drawable.wikipedia);
			}
			
		}.execute(search);
	}
	
	private void executeWeatherForecast(String action){
		new AsyncTask<String, String, Bitmap>() {
	
			@Override
			protected Bitmap doInBackground(String... params) {
				
				String question = params[0];
				int indexOfCity = question.indexOf(" in ");
				String city = null;
				if(indexOfCity != -1)
				if(indexOfCity != -1){
					indexOfCity += 4;
					city = question.substring(indexOfCity).replace(" ","+");
					Log.e("TAG","city "  + city);
					
				}
				GoogleWeather weather = new GoogleWeather();
				String sentence = null;
				String[] results = null;
				String icon = null;
				if(question.contains("Übermorgen") || question.contains("über morgen")){
					if(city != null)
						results = weather.getForecastWeather(city,2);
					else
						results = weather.getForecastWeather(2);
					sentence = results[0] + " mit " + results[1] + " bis " + results[2] + " grad celsius";
					icon = results[3];
				}	
				else if(question.contains("morgen")){
					if(city != null)
						results = weather.getForecastWeather(city,1);
					else
						results = weather.getForecastWeather(1);
					sentence = results[0] + " mit " + results[1] + " bis " + results[2] + " grad celsius";
					icon = results[3];
				}
				else{
					if(city != null)
						results = weather.getCurrentWeather(city);
					else
						results = weather.getCurrentWeather();
					sentence = results[0] + " mit " + results[1] + " grad celsius";	
					icon = results[2];
				}
				publishProgress(sentence);
				return weather.getWeatherIcon(icon);
			}

			@Override
			public void onProgressUpdate(String...strings){
				mAvatarFragment.speak(strings[0]);
			}
			
			@Override
			public void onPostExecute(Bitmap result){
				if(result != null)
					mAvatarFragment.showQuickActionView(result);
			}			
		}.execute(action);
	}
	
	private void searchImage(String image){
		
		new AsyncTask<String, String, Bitmap>() {
			
			@Override
			protected Bitmap doInBackground(String... params) {
				String query = params[0];
				try { query = URLEncoder.encode(query,"iso-8859-1"); } 
				catch (UnsupportedEncodingException e) { e.printStackTrace(); }

				GoogleImages googleImages = new GoogleImages();
				return googleImages.searchImage(query);
			}
			
			@Override
			public void onPostExecute(Bitmap result){
				if(result != null)
					mAvatarFragment.showImageInFrame(result);
				else
				{
					mAvatarFragment.saySomething(mContext.getString(R.string.es_ist_ein_fehler_aufgetreten));
					mAvatarFragment.showActionView(R.drawable.dialog_warning);
				}
			}			
		}.execute(image);
		
	}

}
