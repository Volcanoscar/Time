package com.beryleo.time.alarmp;
//nothing to check yet... 12/8/20
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class bootservice extends BroadcastReceiver {
	//displays a notification
	//should disable this for end-users
	private AlarmManager am;
	private PendingIntent sender;
	Calendar calendar;
	@Override
	public void onReceive(Context context, Intent intent) {
		//start the intent for scheduling all the alarms at boot time
		Intent scheduler = new Intent(context, schedulealarmservice.class);
		sender = PendingIntent.getBroadcast(context,
				0, scheduler, 0);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		//schedules alarms 5 seconds after this
		calendar.add(Calendar.SECOND, 5);
		am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}
} 
