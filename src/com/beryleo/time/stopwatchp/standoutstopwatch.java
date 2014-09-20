package com.beryleo.time.stopwatchp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wei.mark.standout.ui.Window;

import com.beryleo.time.R;
import com.beryleo.time.stopwatchp.multiwindow;
import com.beryleo.time.stopwatchp.lapvalue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.beryleo.time.fonttextview;
import com.beryleo.time.fontbutton;
//more or less the same code as what is in stopwatch class
//just stripped down, and made to work with multiwindow class

public class standoutstopwatch extends multiwindow {
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	DecimalFormat placeformat;
	private fontbutton startpausebutton;
	private fontbutton lapresetbutton;
	private fonttextview elapsedtextview;
	private Handler update = new Handler();
	private Handler blink = new Handler();
	private double seconds;
	private int minutes, hours;
	private long start, end, freeze, melt, difference;
	private boolean started, paused;
	private Typeface digitalfont;
	private int largefont = 40;
	private ArrayList<lapvalue> laplist = new ArrayList<lapvalue>();

	@Override
	public void createAndAttachView(final int id, FrameLayout frame) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.standoutstopwatch, frame, true);
		view.setBackgroundColor(Color.argb(128, 0, 0, 0));
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		if (settings.contains("stopwatchdatasaved")) {
			restorestopwatchdata();
		}
		if (!settings.contains("poweredon")) {
			editor.putBoolean("poweredon", true);
			editor.putInt("places", 2);
			editor.commit();
		}
		digitalfont = Typeface.createFromAsset(getAssets(),
				"digital-7 (mono).ttf");
		startpausebutton = (fontbutton) view.findViewById(R.id.startpausebutton);
		startpausebutton.setTypeface(digitalfont);
		lapresetbutton = (fontbutton) view.findViewById(R.id.lapresetbutton);
		lapresetbutton.setTypeface(digitalfont);
		elapsedtextview = (fonttextview) view.findViewById(R.id.elapsedtextview);
		elapsedtextview.setTypeface(digitalfont);
		elapsedtextview.setTextSize(largefont);
		elapsedtime();
		if (start == 0) {
			startpausebutton.setText(R.string.start);
			lapresetbutton.setText(R.string.lap);
			lapresetbutton.setEnabled(false);
		} 
		else if (paused) {
			startpausebutton.setText(R.string.resume);
			lapresetbutton.setText(R.string.reset);
			update.removeCallbacks(updatestopwatchtask);
			update.postDelayed(updatestopwatchtask, 1);
			blink.removeCallbacks(blinkstopwatchtask);
			blink.postDelayed(blinkstopwatchtask, 1);
		} 
		else {
			startpausebutton.setText(R.string.pause);
			lapresetbutton.setText(R.string.lap);
			update.removeCallbacks(updatestopwatchtask);
			update.postDelayed(updatestopwatchtask, 1);
		}
		startpausebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!started) {
					started = true;
					start = System.nanoTime();
					startpausebutton.setText(R.string.pause);
					lapresetbutton.setEnabled(true);
					update.removeCallbacks(updatestopwatchtask);
					update.postDelayed(updatestopwatchtask, 1);
				} 
				else if (started) {
					if (!paused) {
						paused = true;
						freeze = System.nanoTime();
						startpausebutton.setText(R.string.resume);
						lapresetbutton.setText(R.string.reset);
						blink.removeCallbacks(blinkstopwatchtask);
						blink.postDelayed(blinkstopwatchtask, 1);
					} 
					else if (paused) {
						paused = false;
						melt = System.nanoTime();
						difference += melt - freeze;
						startpausebutton.setText(R.string.pause);
						lapresetbutton.setText(R.string.lap);
						blink.removeCallbacks(blinkstopwatchtask);
					}
				}
			}
		});
		lapresetbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!paused) {
					addlap();
					lapvalue temp = laplist.get(laplist.size() - 1);
					displaytoast("lap added: " + temp.gethours() + ":"
							+ temp.getminutes() + ":" + temp.getseconds());
				} 
				else if (paused) {
					laplist = new ArrayList<lapvalue>();
					savestopwatchlaps();
					started = false;
					paused = false;
					startpausebutton.setText(R.string.start);
					lapresetbutton.setEnabled(false);
					lapresetbutton.setText(R.string.lap);
					update.removeCallbacks(updatestopwatchtask);
					blink.removeCallbacks(blinkstopwatchtask);
					start = 0;
					end = 0;
					difference = 0;
					hours = 0;
					minutes = 0;
					seconds = 0;
					elapsedtime();
					elapsedtextview.setTextColor(Color.argb(255, 255, 255, 255));
				}
			}
		});
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		// now localises to match the screen size
		return new StandOutLayoutParams(id,
				Integer.parseInt(getString(R.string.width_popout)),
				Integer.parseInt(getString(R.string.height_popout)),
				StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
	}

	@Override
	public String getAppName() {
		return "Stopwatch";
	}

	@Override
	public void onDestroy() {
		savestopwatchdata();
		super.onDestroy();
	}

	@Override
	public boolean onClose(int id, Window window) {
		savestopwatchdata();
		return false;
	}

	@Override
	public boolean onCloseAll() {
		return false;
	}

	@Override
	public boolean onHide(int id, Window window) {
		savestopwatchdata();
		return false;
	}

	private void elapsedtime() {
		elapsedtextview.setText(hours + ":" + minutes + ":"
				+ formatdecimal(seconds));
	}

	private String formatdecimal(double number) {
		String formatfordecimal = "#.";
		for (int n = 0; n < settings.getInt("places", -1); n++) {
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
		for (int n = places; n < settings.getInt("places", -1); n++) {
			if (places == 0 && n == places) {
				tobereturned += ".";
			}
			tobereturned += "0";
		}
		if (settings.getInt("places", -1) == 0) {
			return tobereturned.substring(0, tobereturned.length() - 1);
		}
		return tobereturned;
	}

	private Runnable updatestopwatchtask = new Runnable() {
		public void run() {
			end = System.nanoTime();
			elapsedtextview.setTextColor(Color.argb(255, 255, 255, 255));
			if (!paused) {
				hours = (int) ((((end - start - difference) / 1000000000) / 60) / 60);
				minutes = (int) ((((end - start - difference) / 1000000000) - 60 * 60 * hours) / 60);
				seconds = (double) ((double) (end - start - difference) / 1000000000)
						- 60 * 60 * hours - 60 * minutes;
			}
			elapsedtime();
			if (!paused) {
				update.postDelayed(updatestopwatchtask, 1);
			} 
			else {
				update.postDelayed(updatestopwatchtask, 250);
			}
		}
	};
	private Runnable blinkstopwatchtask = new Runnable() {
		public void run() {
			end = System.nanoTime();
			elapsedtextview.setTextColor(Color.argb(128, 0, 0, 0));
			blink.postDelayed(blinkstopwatchtask, 500);
		}
	};

	private void addlap() {
		lapvalue lv = new lapvalue();
		lv.setdifference(difference);
		lv.setend(end);
		lv.sethours(hours);
		lv.setminutes(minutes);
		lv.setseconds(seconds);
		lv.setstart(start);
		lv.setdecimalformat(settings.getInt("places", -1));
		laplist.add(lv);
		savestopwatchlaps();
	}

	private void addlapsfromsave() {
		String firstpart = "lapvalue";
		lapvalue lv;
		for (int n = 0; n < settings.getInt("numberlaps", 0); n++) {
			lv = new lapvalue();
			lv.setdifference(0);
			lv.setend(0);
			lv.sethours(settings.getInt(firstpart + Integer.toString(n)
					+ "hours", 0));
			lv.setminutes(settings.getInt(firstpart + Integer.toString(n)
					+ "minutes", 0));
			lv.setseconds((double) settings.getFloat(
					firstpart + Integer.toString(n) + "seconds", 0));
			lv.setstart(0);
			lv.setdecimalformat(settings.getInt("places", -1));
			laplist.add(lv);
		}
	}

	private void displaytoast(CharSequence text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	private void savestopwatchlaps() {
		String firstpart = "lapvalue";
		for (int n = 0; n < settings.getInt("numberlaps", 0); n++) {
			editor.remove(firstpart + Integer.toString(n) + "seconds");
			editor.remove(firstpart + Integer.toString(n) + "minutes");
			editor.remove(firstpart + Integer.toString(n) + "hours");
		}
		editor.putInt("numberlaps", laplist.size());
		for (int n = 0; n < laplist.size(); n++) {
			try {
				editor.putFloat(firstpart + Integer.toString(n) + "seconds",
						(float) Double.parseDouble(laplist.get(n).getseconds()));
			} 
			catch (Exception e) {
				editor.putFloat(firstpart + Integer.toString(n) + "seconds",
						(float) laplist.get(n).getsecondsfallback());
			}
			editor.putInt(firstpart + Integer.toString(n) + "minutes", laplist
					.get(n).getminutes());
			editor.putInt(firstpart + Integer.toString(n) + "hours", laplist
					.get(n).gethours());
		}
		editor.commit();
	}

	private void savestopwatchdata() {
		editor.putBoolean("stopwatchdatasaved", true);
		Calendar rightnow = Calendar.getInstance();
		editor.putLong("realtime", rightnow.getTimeInMillis());
		editor.putBoolean("started", started);
		editor.putBoolean("paused", paused);
		editor.putFloat("seconds", (float) seconds);
		editor.putInt("minutes", minutes);
		editor.putInt("hours", hours);
		editor.putLong("start", start);
		editor.putLong("end", end);
		editor.putLong("freeze", freeze);
		editor.putLong("difference", difference);
		editor.commit();
	}

	private void restorestopwatchdata() {
		started = settings.getBoolean("started", false);
		paused = settings.getBoolean("paused", false);
		seconds = (double) settings.getFloat("seconds", 0);
		minutes = settings.getInt("minutes", 0);
		hours = settings.getInt("hours", 0);
		if (started) {
			Calendar rightnow = Calendar.getInstance();
			start = settings.getLong("start", 0)
					- 1000000
					* (rightnow.getTimeInMillis() - settings.getLong(
							"realtime", 0));
		}
		if (paused) {
			Calendar rightnow = Calendar.getInstance();
			freeze = settings.getLong("freeze", 0)
					- 1000000
					* (rightnow.getTimeInMillis() - settings.getLong(
							"realtime", 0));
		}
		difference = settings.getLong("difference", difference);
		addlapsfromsave();
	}
}
