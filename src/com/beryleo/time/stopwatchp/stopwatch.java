package com.beryleo.time.stopwatchp;

//free of localisation issues
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.beryleo.time.R;
import com.beryleo.time.timeactivity;
import com.beryleo.time.lvadapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
//stopwatch class
//2012-7-23: basically done , unless can think of new features to add
//2012-7-23: has laps, formatting, popup, rotate, lists, swipe, remembers data

public class stopwatch {
	private DecimalFormat placeformat;
	public Handler update = new Handler();
	public Handler blink = new Handler();
	public double seconds;
	public int minutes, hours;
	public long start, end, freeze, melt, difference;
	public boolean started, paused;
	public ArrayList<lapvalue> laplist = new ArrayList<lapvalue>();
	private lvadapter lapsadapter;
	protected timeactivity context;
	private lapvalue lv;
	private lapvalue temp;
	private Calendar rightnow;

	// add a constructor with the Context of your activity
	public stopwatch(timeactivity _context) {
		context = _context;
	}

	public void stopwatchmain() {
		context.runOnUiThread(new Thread() {
			@Override
			public void run() {
				updatelaplist();
				context.elapsedtextview.setTypeface(context.digitalfont);
				context.elapsedtextview.setTextSize(context.largefont);
				context.startpausebutton.setText(R.string.start);
				context.lapresetbutton.setText(R.string.lap);
				elapsedtime();
				context.lapresetbutton.setEnabled(false);
				context.elapsedtextview
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								if (context.settings.getBoolean(
										"popupstopwatch", false)) {
									launchstandoutstopwatch();
									savestopwatchdata();
									context.finish();
									System.exit(0);
								}
							}
						});
				context.startpausebutton
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								if (!started) {
									started = true;
									start = System.nanoTime();
									context.startpausebutton
											.setText(R.string.pause);
									context.lapresetbutton.setEnabled(true);
									update.removeCallbacks(updatestopwatchtask);
									update.postDelayed(updatestopwatchtask, 1);
								} else if (started) {
									if (!paused) {
										paused = true;
										freeze = System.nanoTime();
										context.startpausebutton
												.setText(R.string.resume);
										context.lapresetbutton
												.setText(R.string.reset);
										blink.removeCallbacks(blinkstopwatchtask);
										blink.postDelayed(blinkstopwatchtask, 1);
									} else if (paused) {
										paused = false;
										melt = System.nanoTime();
										difference += melt - freeze;
										context.startpausebutton
												.setText(R.string.pause);
										context.lapresetbutton
												.setText(R.string.lap);
										blink.removeCallbacks(blinkstopwatchtask);
									}
								}
							}
						});
				context.lapresetbutton
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								if (!paused) {
									addlap();
									temp = laplist.get(laplist.size() - 1);
									context.displaytoast("lap added: "
											+ temp.gethours() + ":"
											+ temp.getminutes() + ":"
											+ temp.getseconds());
								} else if (paused) {
									laplist = new ArrayList<lapvalue>();
									savestopwatchlaps();
									updatelaplist();
									started = false;
									paused = false;
									context.startpausebutton
											.setText(R.string.start);
									context.lapresetbutton.setEnabled(false);
									context.lapresetbutton
											.setText(R.string.lap);
									update.removeCallbacks(updatestopwatchtask);
									blink.removeCallbacks(blinkstopwatchtask);
									start = 0;
									end = 0;
									difference = 0;
									hours = 0;
									minutes = 0;
									seconds = 0;
									elapsedtime();
									context.elapsedtextview.setTextColor(Color
											.argb(255, 255, 255, 255));
								}
							}
						});
				@SuppressWarnings("deprecation")
				final GestureDetector gestureDetector = new GestureDetector(
						new gesturedetector());
				View.OnTouchListener gestureListener = new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						// on a custom ics rom on optimus v, encounters errors
						// here
						// with hardware acceleration enabled
						// only happens when hardware acceleration is enabled
						// probably due to bad drivers, is inconsistent in if it
						// crashes or not
						try {
							return gestureDetector.onTouchEvent(event);
						} catch (Exception e) {
							return false;
						}
					}
				};
				context.lapslistview.setOnTouchListener(gestureListener);
			}
		});
	}

	private void elapsedtime() {
		context.elapsedtextview.setText(hours + ":" + minutes + ":"
				+ formatdecimal(seconds));
	}

	String formatdecimal(double number) {
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

	public Runnable updatestopwatchtask = new Runnable() {
		public void run() {
			context.elapsedtextview
					.setTextColor(Color.argb(255, 255, 255, 255));
			end = System.nanoTime();
			if (!paused) {
				hours = (int) ((((end - start - difference) / 1000000000) / 60) / 60);
				minutes = (int) ((((end - start - difference) / 1000000000) - 60 * 60 * hours) / 60);
				seconds = (double) ((double) (end - start - difference) / 1000000000)
						- 60 * 60 * hours - 60 * minutes;
			}
			elapsedtime();
			if (!paused) {
				update.postDelayed(updatestopwatchtask, 1);
			} else {
				update.postDelayed(updatestopwatchtask, 250);
			}
		}
	};
	public Runnable blinkstopwatchtask = new Runnable() {
		public void run() {
			end = System.nanoTime();
			context.elapsedtextview.setTextColor(Color.argb(128, 0, 0, 0));
			blink.postDelayed(blinkstopwatchtask, 500);
		}
	};

	private void addlap() {
		lv = new lapvalue();
		lv.setdifference(difference);
		lv.setend(end);
		lv.sethours(hours);
		lv.setminutes(minutes);
		lv.setseconds(seconds);
		lv.setstart(start);
		lv.setdecimalformat(context.settings.getInt("places", -1));
		laplist.add(lv);
		savestopwatchlaps();
		updatelaplist();
	}

	private void addlapsfromsave() {
		String firstpart = "lapvalue";
		for (int n = 0; n < context.settings.getInt("numberlaps", 0); n++) {
			lv = new lapvalue();
			lv.setdifference(0);
			lv.setend(0);
			lv.sethours(context.settings.getInt(firstpart + Integer.toString(n)
					+ "hours", 0));
			lv.setminutes(context.settings.getInt(
					firstpart + Integer.toString(n) + "minutes", 0));
			lv.setseconds((double) context.settings.getFloat(firstpart
					+ Integer.toString(n) + "seconds", 0));
			lv.setstart(0);
			lv.setdecimalformat(context.settings.getInt("places", -1));
			laplist.add(lv);
		}
	}

	private void updatelaplist() {
		String[] listitems = new String[laplist.size()];

		for (int n = 0; n < laplist.size(); n++) {
			temp = laplist.get(n);
			listitems[n] = temp.gethours() + ":" + temp.getminutes() + ":"
					+ temp.getseconds();
		}
		lapsadapter = new lvadapter(context, listitems, context.digitalfont,
				context.getResources().getDrawable(R.drawable.check), null, context.largefont);
		context.lapslistview.setAdapter(lapsadapter);
	}

	private void launchstandoutstopwatch() {
		Intent launchstandout = new Intent(context,
				standoutstopwatchactivity.class);
		context.startActivity(launchstandout);
	}

	private void savestopwatchlaps() {
		String firstpart = "lapvalue";
		for (int n = 0; n < context.settings.getInt("numberlaps", 0); n++) {
			context.editor.remove(firstpart + Integer.toString(n) + "seconds");
			context.editor.remove(firstpart + Integer.toString(n) + "minutes");
			context.editor.remove(firstpart + Integer.toString(n) + "hours");
		}
		context.editor.putInt("numberlaps", laplist.size());
		for (int n = 0; n < laplist.size(); n++) {
			try {
				context.editor
						.putFloat(firstpart + Integer.toString(n) + "seconds",
								(float) Double.parseDouble(laplist.get(n)
										.getseconds()));
			} catch (Exception e) {
				context.editor.putFloat(firstpart + Integer.toString(n)
						+ "seconds", (float) laplist.get(n)
						.getsecondsfallback());
			}
			context.editor.putInt(firstpart + Integer.toString(n) + "minutes",
					laplist.get(n).getminutes());
			context.editor.putInt(firstpart + Integer.toString(n) + "hours",
					laplist.get(n).gethours());
		}
		context.editor.commit();
	}

	public void savestopwatchdata() {
		context.editor.putBoolean("stopwatchdatasaved", true);
		rightnow = Calendar.getInstance();
		context.editor.putLong("realtime", rightnow.getTimeInMillis());
		context.editor.putBoolean("started", started);
		context.editor.putBoolean("paused", paused);
		context.editor.putFloat("seconds", (float) seconds);
		context.editor.putInt("minutes", minutes);
		context.editor.putInt("hours", hours);
		context.editor.putLong("start", start);
		context.editor.putLong("end", end);
		context.editor.putLong("freeze", freeze);
		context.editor.putLong("difference", difference);
		context.editor.commit();
	}

	public void restorestopwatchdata() {
		started = context.settings.getBoolean("started", false);
		paused = context.settings.getBoolean("paused", false);
		seconds = (double) context.settings.getFloat("seconds", 0);
		minutes = context.settings.getInt("minutes", 0);
		hours = context.settings.getInt("hours", 0);
		if (started) {
			rightnow = Calendar.getInstance();
			start = context.settings.getLong("start", 0)
					- 1000000
					* (rightnow.getTimeInMillis() - context.settings.getLong(
							"realtime", 0));
		}
		if (paused) {
			rightnow = Calendar.getInstance();
			freeze = context.settings.getLong("freeze", 0)
					- 1000000
					* (rightnow.getTimeInMillis() - context.settings.getLong(
							"realtime", 0));
		}
		difference = context.settings.getLong("difference", difference);
		addlapsfromsave();

	}

	public void onitemclick(int position) {
	}

	class gesturedetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityx,
				float velocityy) {
			if (e1.getX() - e2.getX() > timeactivity.move) {
				if (context.viewindex < 6) {
					context.right = true;
					context.left = false;
					context.viewindex++;
					context.init();
				}
				return true;
			} else if (e2.getX() - e1.getX() > timeactivity.move) {
				if (context.viewindex > 0) {
					context.right = false;
					context.left = true;
					context.viewindex--;
					context.init();
				}
				return true;
			}
			return false;
		}
	}
}
