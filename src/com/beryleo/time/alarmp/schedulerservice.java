package com.beryleo.time.alarmp;
//this is just so that I can do actions in the broadcast receiver that would normally time out
//because broadcast receivers are automatically killed after ten seconds
//so this can run longer
import java.util.Calendar;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class schedulerservice extends Service {
	private NotificationManager notificationmanager;
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 606060;
	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		schedulerservice getService() {
			return schedulerservice.this;
		}
	}
	@Override
	public void onCreate() {
		notificationmanager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.  We put an icon in the status bar.
		//showNotification();
		//DOES NOT WORK
		//THE MANAGEDQUERY DOES NOT WORK AS THE CONTEXT CANNOT BE CAST TO AN ACTIVITY
		//SO I HAVE TO LOOK  UP THE SOURCE FOR THE MANAGEDQUERY METHOD IN ACTIVITY
		//AND THEN COPY IT OVER HERE, AND MODIFY IT SO THAT IT WORKS
		//DISABLED FOR NOW 2012-9-15
		//done - just use a contentresolver, that is all that a managed query is
		Uri uri = alarmprovidermetadata.alarmtablemetadata.CONTENTURI;
		Cursor c = this.getApplicationContext().getContentResolver().query(uri, null, null, null, null);
		int hourindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTHOUR);
		int minuteindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTMINUTE);
		int onindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMON);
		int vibrateindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMVIBRATE);
		int mondayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMMONDAYREPEAT);
		int tuesdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMTUESDAYREPEAT);
		int wednesdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMWEDNESDAYREPEAT);
		int thursdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMTHURSDAYREPEAT);
		int fridayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMFRIDAYREPEAT);
		int saturdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSATURDAYREPEAT);
		int sundayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSUNDAYREPEAT);
		int nameindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMNAME);
		int ringtoneindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMRINGTONE);
		int silentindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSILENT);
		int intervalindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINTERVAL);
		int intervalonindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINTERVALON);
		int activationtimeindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMACTIVATIONTIME);
		int repeatindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMREPEATS);
		int infiniterepeatindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINFINITEREPEAT);
		int secondindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTSECOND);
		int[] hours = new int[c.getCount()];
		int[] minutes = new int[c.getCount()];
		int[] seconds = new int[c.getCount()];
		boolean[] ons = new boolean[c.getCount()];
		boolean[] vibrates = new boolean[c.getCount()];
		boolean[] mondays = new boolean[c.getCount()];
		boolean[] tuesdays = new boolean[c.getCount()];
		boolean[] wednesdays = new boolean[c.getCount()];
		boolean[] thursdays = new boolean[c.getCount()];
		boolean[] fridays = new boolean[c.getCount()];
		boolean[] saturdays = new boolean[c.getCount()];
		boolean[] sundays = new boolean[c.getCount()];
		String[] names = new String[c.getCount()];
		String[] ringtones = new String[c.getCount()];
		boolean[] silents = new boolean[c.getCount()];
		int[] intervals = new int[c.getCount()];
		boolean[] intervalons = new boolean[c.getCount()];
		long[] activationtimes = new long[c.getCount()];
		int[] repeats = new int[c.getCount()];
		boolean[] infiniterepeats = new boolean[c.getCount()];
		int n = 0;
		for (c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
			hours[n] = c.getInt(hourindex);
			minutes[n] = c.getInt(minuteindex);
			seconds[n] = c.getInt(secondindex);
			ons[n] = i2b(c.getInt(onindex));
			vibrates[n] = i2b(c.getInt(vibrateindex));
			mondays[n] = i2b(c.getInt(mondayindex));
			tuesdays[n] = i2b(c.getInt(tuesdayindex));
			wednesdays[n] = i2b(c.getInt(wednesdayindex));
			thursdays[n] = i2b(c.getInt(thursdayindex));
			fridays[n] = i2b(c.getInt(fridayindex));
			saturdays[n] = i2b(c.getInt(saturdayindex));
			sundays[n] = i2b(c.getInt(sundayindex));
			names[n] = c.getString(nameindex);
			ringtones[n] = c.getString(ringtoneindex);
			silents[n] = i2b(c.getInt(silentindex));
			intervals[n] = c.getInt(intervalindex);
			intervalons[n] = i2b(c.getInt(intervalonindex));
			activationtimes[n] = c.getLong(activationtimeindex);
			repeats[n] = c.getInt(repeatindex);
			infiniterepeats[n] = i2b(c.getInt(infiniterepeatindex));
			n++;
		}
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		Calendar temp = Calendar.getInstance();
		Calendar alarm = Calendar.getInstance();
		//this will check for whatever alarm it is that will occur next
		int nearestalarmindex = -1;
		Calendar nearest = Calendar.getInstance();
		for(n = 0; n < c.getCount(); n++) {
			alarm.setTimeInMillis(activationtimes[n]);
			//if the alarm is on
			if(ons[n]) {
				//logic for normal alarm type
				if(!intervalons[n]) {
					//check if the time of the alarm has already gone by
					//should actually be called "alarmpassed"
					boolean alarmpassed;
					//will use temp to check if the alarm, and it was scheduled for today
					//, has already been passed for today
					temp.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH)
							, now.get(Calendar.DAY_OF_MONTH), hours[n], minutes[n], seconds[n]);
					if(now.getTimeInMillis()<temp.getTimeInMillis()) {
						alarmpassed = false;
					}
					else {
						alarmpassed = true;
					}
					int nearestamountofdays = -1;
					//have to somehow get the nearest amount of days until the alarm goes off
					switch(now.get(Calendar.DAY_OF_WEEK)) {
					case Calendar.MONDAY:
						if (mondays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 1;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 2;
						else if (thursdays[n])
							nearestamountofdays = 0 + 3;
						else if (fridays[n])
							nearestamountofdays = 0 + 4;
						else if (saturdays[n])
							nearestamountofdays = 0 + 5;
						else if (sundays[n])
							nearestamountofdays = 0 + 6;
						else if (mondays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.TUESDAY:
						if (tuesdays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 1;
						else if (thursdays[n])
							nearestamountofdays = 0 + 2;
						else if (fridays[n])
							nearestamountofdays = 0 + 3;
						else if (saturdays[n])
							nearestamountofdays = 0 + 4;
						else if (sundays[n])
							nearestamountofdays = 0 + 5;
						else if (mondays[n])
							nearestamountofdays = 0 + 6;
						else if (tuesdays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.WEDNESDAY:
						if (wednesdays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (thursdays[n])
							nearestamountofdays = 0 + 1;
						else if (fridays[n])
							nearestamountofdays = 0 + 2;
						else if (saturdays[n])
							nearestamountofdays = 0 + 3;
						else if (sundays[n])
							nearestamountofdays = 0 + 4;
						else if (mondays[n])
							nearestamountofdays = 0 + 5;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 6;
						else if (wednesdays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.THURSDAY:
						if (thursdays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (fridays[n])
							nearestamountofdays = 0 + 1;
						else if (saturdays[n])
							nearestamountofdays = 0 + 2;
						else if (sundays[n])
							nearestamountofdays = 0 + 3;
						else if (mondays[n])
							nearestamountofdays = 0 + 4;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 5;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 6;
						else if (thursdays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.FRIDAY:
						if (fridays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (saturdays[n])
							nearestamountofdays = 0 + 1;
						else if (sundays[n])
							nearestamountofdays = 0 + 2;
						else if (mondays[n])
							nearestamountofdays = 0 + 3;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 4;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 5;
						else if (thursdays[n])
							nearestamountofdays = 0 + 6;
						else if (fridays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.SATURDAY:
						if (saturdays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (sundays[n])
							nearestamountofdays = 0 + 1;
						else if (mondays[n])
							nearestamountofdays = 0 + 2;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 3;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 4;
						else if (thursdays[n])
							nearestamountofdays = 0 + 5;
						else if (fridays[n])
							nearestamountofdays = 0 + 6;
						else if (saturdays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					case Calendar.SUNDAY:
						if (sundays[n]&&!alarmpassed)
							nearestamountofdays = 0;
						else if (mondays[n])
							nearestamountofdays = 0 + 1;
						else if (tuesdays[n])
							nearestamountofdays = 0 + 2;
						else if (wednesdays[n])
							nearestamountofdays = 0 + 3;
						else if (thursdays[n])
							nearestamountofdays = 0 + 4;
						else if (fridays[n])
							nearestamountofdays = 0 + 5;
						else if (saturdays[n])
							nearestamountofdays = 0 + 6;
						else if (sundays[n]&&alarmpassed)
							nearestamountofdays = 0 + 7;
						break;
					}
					//case for one time alarm, eg. the user set the alarm to never repeat on a day of the week
					if((alarm.get(Calendar.DAY_OF_YEAR)==now.get(Calendar.DAY_OF_YEAR)
							&&(alarm.get(Calendar.YEAR)==now.get(Calendar.YEAR)))&&nearestamountofdays==-1&&!alarmpassed) {
						temp.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH)
								, now.get(Calendar.DAY_OF_MONTH), hours[n], minutes[n], seconds[n]);
						if(nearestalarmindex==-1) {
							nearestalarmindex = n;
							nearest.setTimeInMillis(temp.getTimeInMillis());
						}
						else {
							if(nearest.getTimeInMillis()>temp.getTimeInMillis()) {
								nearestalarmindex = n;
								nearest.setTimeInMillis(temp.getTimeInMillis());
							}
						}
					}
					else if (nearestamountofdays!=-1) {
						temp.setTimeInMillis(now.getTimeInMillis());
						temp.add(Calendar.DAY_OF_YEAR, nearestamountofdays);
						int alarmdayofmonth = temp.get(Calendar.DAY_OF_MONTH);
						int alarmmonth = temp.get(Calendar.MONTH);
						int alarmyear = temp.get(Calendar.YEAR);
						temp.set(alarmyear, alarmmonth, alarmdayofmonth, hours[n], minutes[n], seconds[n]);
						if(nearestalarmindex==-1) {
							nearestalarmindex = n;
							nearest.setTimeInMillis(temp.getTimeInMillis());
						}
						else {
							if(nearest.getTimeInMillis()>temp.getTimeInMillis()) {
								nearestalarmindex = n;
								nearest.setTimeInMillis(temp.getTimeInMillis());
							}	
						}
					}	
				}
				else {
					//add code for interval  alarms DONE
					double numrepeats = ((double)(now.getTimeInMillis() - activationtimes[n])) / (intervals[n] * 1000);
					int repeat = (int) (numrepeats - numrepeats%1);
					if ((infiniterepeats[n]||repeats[n] >= numrepeats) && numrepeats >= 0) {
						temp.setTimeInMillis(now.getTimeInMillis());
						if (numrepeats%1==0) {
							temp.add(Calendar.MILLISECOND, (repeat) * (intervals[n] * 1000));
						}
						else {
							temp.add(Calendar.MILLISECOND, (repeat + 1) * (intervals[n] * 1000));
						}
						if (nearestalarmindex==-1) {
							nearestalarmindex = n;
							nearest.setTimeInMillis(temp.getTimeInMillis());
						}
						else {
							if(nearest.getTimeInMillis()>temp.getTimeInMillis()) {
								nearestalarmindex = n;
								nearest.setTimeInMillis(temp.getTimeInMillis());
							}	
						}
					}
				}
			}
		}
		Intent alarmlaunch = new Intent(getBaseContext(), alarmalarmstarter.class);
		PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(),
				0, alarmlaunch, 0);
		AlarmManager am = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
		if (nearestalarmindex!=-1) {
			settings = getSharedPreferences(PREFS_NAME, 0);
			editor = settings.edit();
			editor.putString("alarmtemptitle", names[nearestalarmindex]);
			editor.putBoolean("alarmtempvibrate", vibrates[nearestalarmindex]);
			editor.putBoolean("alarmtempsilent", silents[nearestalarmindex]);
			editor.putString("alarmtempringtone", ringtones[nearestalarmindex]);
			editor.commit();
			sender = PendingIntent.getBroadcast(getBaseContext(),
					0, alarmlaunch, 0);
			//reconstruct the pending intent for the new data added (extras)
			am.set(AlarmManager.RTC_WAKEUP, nearest.getTimeInMillis(), sender);
		}
		//stops self here
		this.stopSelf();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return Service.START_STICKY;
	}
	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		notificationmanager.cancel(NOTIFICATION);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return ibinder;
	}
	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder ibinder = new LocalBinder();
	/**
	 * Show a notification while this service is running.
	 */
	private boolean i2b(int i) {
		//int to boolean
		if (i==0) {
			return false;
		}
		return true;
	}
}