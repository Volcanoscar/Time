package com.beryleo.time.timerp;

//super hacky
//only class without rotation support as of 16:37 on the 29th of august
//added rotation support
//still super hacky
//mostly bug-free, wierd stuff happens when rotating right before the alarm goes off, but that's it
import java.text.DecimalFormat;
import java.util.Calendar;

import com.beryleo.time.R;
import com.beryleo.time.timeactivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

public class timer {
	protected timeactivity context;
	public int hour, minute;
	public double second = 0;
	public long freeze, melt, totalamount, start;
	private Handler update = new Handler();
	DecimalFormat placeformat;
	Calendar calendar;
	public boolean paused;
	AlarmManager am;
	PendingIntent sender;
	public boolean started;
	public String whenpaused = "";

	// add a constructor with the Context of your activity
	public timer(timeactivity _context) {
		context = _context;
	}

	public void timermain() {
		// this runs on data obtained from context passed to it
		// [this is from the main activity thread]
		// runs on a separate thread from the main stuff
		context.runOnUiThread(new Thread() {
			@Override
			public void run() {
				context.timertimetextview.setTypeface(context.digitalfont);
				context.timertimetextview.setTextSize(context.largefont);
				context.setuptimerbutton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent launch = new Intent(context,
								setuptimeractivity.class);
						context.startActivityForResult(launch, 1);
					}
				});
				if (context.settings.getBoolean("timerstarted", false)) {
					Intent intent = new Intent(context, timeralarm.class);
					sender = PendingIntent.getBroadcast(context, 0, intent, 0);
					started = true;
					paused = context.settings.getBoolean("timerpaused", false);
					freeze = context.settings.getLong("timerfreeze", 0);
					totalamount = context.settings.getLong("timertotalamount",
							0);
					calendar = Calendar.getInstance();
					calendar.setTimeInMillis(context.settings.getLong(
							"timercalendarmillis", 0));
					hour = context.settings.getInt("timerminute", 0);
					minute = context.settings.getInt("timerhour", 0);
					second = context.settings.getFloat("timersecond", 0);
					elapsedtime();
					if (!paused) {
						update.removeCallbacks(updatestopwatchtask);
						update.postDelayed(updatestopwatchtask, 1);
					} else {
						whenpaused = context.settings.getString("whenpaused",
								"");
					}
					context.startpausetimerbutton.setEnabled(true);
					context.resettimerbutton.setEnabled(true);
					context.setuptimerbutton.setEnabled(false);
					if (!paused) {
						context.startpausetimerbutton.setText(R.string.pause);
					} else {
						context.startpausetimerbutton.setText(R.string.resume);
					}
				}
				if (hour != 0 || minute != 0 || second != 0 && !paused
						&& !started) {
					// context.timertimetextview.setText("" + hour + ":" +
					// minute);
					elapsedtime();
				}
				if (hour == 0 && minute == 0 && second == 0 && !started) {
					context.startpausetimerbutton.setEnabled(false);
					context.resettimerbutton.setEnabled(false);
				}
				if (paused) {
					context.timertimetextview.setText(whenpaused);
				}
				context.startpausetimerbutton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (!started && !paused) {
							context.editor.putBoolean("timerstarted",
									true);
							context.editor.commit();
							start = System.nanoTime();
							Intent intent = new Intent(context,
									timeralarm.class);
							sender = PendingIntent.getBroadcast(
									context, 0, intent, 0);
							calendar = Calendar.getInstance();
							calendar.setTimeInMillis(System
									.currentTimeMillis());
							calendar.add(Calendar.MINUTE, minute);
							calendar.add(Calendar.HOUR, hour);
							calendar.add(Calendar.SECOND, (int) second);
							// Schedule the alarm!
							am = (AlarmManager) context
									.getSystemService(Context.ALARM_SERVICE);
							am.set(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), sender);
							String alarmscheduled = context
									.getString(R.string.alarmscheduled);
							context.displaytoast(alarmscheduled);
							update.removeCallbacks(updatestopwatchtask);
							update.postDelayed(updatestopwatchtask, 1);
							started = true;
							context.resettimerbutton.setEnabled(true);
							context.setuptimerbutton.setEnabled(false);
							context.startpausetimerbutton
							.setText(R.string.pause);
							savetimerdata();
						} else if (started && !paused) {
							am = (AlarmManager) context
									.getSystemService(Context.ALARM_SERVICE);
							am.cancel(sender);
							freeze = System.nanoTime();
							paused = true;
							context.startpausetimerbutton
							.setText(R.string.resume);
							update.removeCallbacks(updatestopwatchtask);
							savetimerdata();
						} else {
							melt = System.nanoTime();
							totalamount += melt - freeze;
							am = (AlarmManager) context
									.getSystemService(Context.ALARM_SERVICE);
							am.set(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis()
									+ totalamount / 1000000,
									sender);
							paused = false;
							context.startpausetimerbutton
							.setText(R.string.pause);
							update.removeCallbacks(updatestopwatchtask);
							update.postDelayed(updatestopwatchtask, 1);
							savetimerdata();
						}
					}
				});
				context.resettimerbutton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (started) {
							context.editor.putBoolean("timerstarted",
									false);
							context.editor.commit();
							Intent intent = new Intent(context,
									timeralarm.class);
							sender = PendingIntent.getBroadcast(
									context, 0, intent, 0);
							am = (AlarmManager) context
									.getSystemService(Context.ALARM_SERVICE);
							am.cancel(sender);
							String alarmreset = context
									.getString(R.string.alarmreset);
							context.displaytoast(alarmreset);
							hour = 0;
							minute = 0;
							second = 0;
							update.removeCallbacks(updatestopwatchtask);
							started = false;
							paused = false;
							context.resettimerbutton.setEnabled(false);
							context.startpausetimerbutton
							.setEnabled(false);
							context.setuptimerbutton.setEnabled(true);
							context.timertimetextview.setText("" + 0);
							context.startpausetimerbutton
							.setText(R.string.start);
							elapsedtime();
							savetimerdata();
						}
					}
				});
			}
		});
	}

	private void savetimerdata() {
		context.editor.putBoolean("timerpaused", paused);
		context.editor.putInt("timerhour", hour);
		context.editor.putInt("timerminute", minute);
		context.editor.putFloat("timerseconds", (float) second);
		context.editor.putLong("timerfreeze", freeze);
		context.editor.putLong("timertotalamount", totalamount);
		context.editor.putLong("timercalendarmillis",
				calendar.getTimeInMillis());
		context.editor.putString("whenpaused", context.timertimetextview
				.getText().toString());
		context.editor.commit();
	}

	public void settime(int h, int m, int s) {
		hour = h;
		minute = m;
		second = s;
	}

	private Runnable updatestopwatchtask = new Runnable() {
		public void run() {
			long end = calendar.getTimeInMillis() + totalamount / 1000000;
			Calendar tempc = Calendar.getInstance();
			tempc.setTimeInMillis(System.currentTimeMillis());
			long now = tempc.getTimeInMillis();
			long left = end - now;
			int hours = (int) ((((left) / 1000) / 60) / 60);
			int minutes = (int) ((((left) / 1000) - 60 * 60 * hours) / 60);
			double seconds = (double) ((double) (left) / 1000) - 60 * 60
					* hours - 60 * minutes;
			context.timertimetextview.setText(hours + ":" + minutes + ":"
					+ formatdecimal(seconds));
			if (seconds <= 0) {
				context.timertimetextview.setText("" + 0 + ":" + 0 + ":"
						+ formatdecimal(0));
				context.startpausetimerbutton.setText(R.string.start);
				hour = 0;
				minute = 0;
				second = 0;
				started = false;
				paused = false;
				context.editor.putBoolean("timerstarted", false);
				context.editor.commit();
				timermain();
				context.setuptimerbutton.setEnabled(true);
			} 
			else {
				update.postDelayed(updatestopwatchtask, 1);
			}
		}
	};

	public void elapsedtime() {
		context.timertimetextview.setText(hour + ":" + minute + ":"
				+ formatdecimal(second));
	}

	private String formatdecimal(double number) {
		String formatfordecimal = "#.";
		for (int n = 0; n < context.settings.getInt("places", -1); n++) {
			formatfordecimal += "#";
		}
		placeformat = new DecimalFormat(formatfordecimal);
		int places = 0;
		boolean encounteredperiod = false;
		for (int n = 0; n < placeformat.format(number).length(); n++) {
			if (encounteredperiod) {
				places++;
			}
			if (placeformat.format(number).substring(n, n + 1).equals(".")
					|| placeformat.format(number).substring(n, n + 1)
					.equals(",")) {
				encounteredperiod = true;
			}
		}
		String tobereturned = placeformat.format(number);
		for (int n = places; n < context.settings.getInt("places", -1); n++) {
			if (places == 0 && n == places) {
				tobereturned += ".";
			}
			tobereturned += "0";
		}
		if (context.settings.getInt("places", -1) == 0) {
			return tobereturned.substring(0, tobereturned.length() - 1);
		}
		return tobereturned;
	}
}
