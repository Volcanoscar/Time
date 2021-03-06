/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beryleo.time.alarmp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Vibrator;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.beryleo.time.R;
import com.beryleo.time.closer;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 */
public class alarmalarmstarter extends BroadcastReceiver {
	private Vibrator mVibrator;
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		final String timerdone = context.getString(R.string.timerdone);
		final String name = context.getString(R.string.app_name);
		final String alarmfinished = context.getString(R.string.alarmdone) + " : " + intent.getStringExtra("title");
		//Get the notification manager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm =
				(NotificationManager)context.getSystemService(ns);
		//Create Notification Object
		int icon = R.drawable.icon_thick;
		CharSequence tickerText = timerdone;
		long when = System.currentTimeMillis();
		Notification notification =
				new Notification(icon, tickerText, when);
		Intent launch = new Intent(context, closer.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, launch, 0);
		notification.setLatestEventInfo(context, name, alarmfinished, pi);
		//makes the device vibrate, until the user clicks on the notification
		try {
			mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			mVibrator.vibrate(1000);
		}
		catch (Exception e) {
			//if the vibrate permission is not obtained
		}
		//Send notification
		//The first argument is a unique id for this notification.
		//This id allows you to cancel the notification later
		nm.notify(606060, notification);
		context.stopService(new Intent(context, 
				schedulerservice.class));
		context.startService(new Intent(context, 
				schedulerservice.class));
		Intent alarmlaunch = new Intent(context, alarmalert.class);
		alarmlaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(alarmlaunch);
	}
}
