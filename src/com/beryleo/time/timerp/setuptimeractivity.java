package com.beryleo.time.timerp;

import java.util.ArrayList;

import com.beryleo.time.R;
import com.beryleo.time.lvadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewAnimator;
import com.beryleo.time.fontbutton;
import com.beryleo.time.fonttextview;

//@SuppressWarnings("deprecation")
public class setuptimeractivity extends Activity {
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public Typeface digitalfont, clockfont;
	public int largefont = 40;
	private ViewAnimator viewanimator;
	private boolean right = true, left;
	// random
	// switcher variables
	private fontbutton previousbutton, nextbutton;
	//@SuppressWarnings("unused")
	private fonttextview previoustextview, currenttextview, nexttextview;
	private int viewindex = 0;
	private static final int move = 120;
	private GestureDetector gesture;
	// first [main] screen variables
	//@SuppressWarnings("unused")
	private fonttextview timernametextview;
	private EditText timernameedittext;
	private TimePicker timertimepicker, timersecondtimepicker;
	private fontbutton finishtimerbutton, canceltimerbutton, addpresettimerbutton;
	private int hour, minute, second;
	private boolean timeset;
	private String timername = "";
	// fourth [ringtone] screen variables
	private ListView timerpresetslistview;
	public ArrayList<presetvalue> presetlist = new ArrayList<presetvalue>();
	private lvadapter presetadapter;
	private presetvalue pv;
	private presetvalue temp;
	// animation
	private LayoutInflater inflater;
	private View tmpView;
	private Animation animation;
	private ViewGroup root;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		addpresetsfromsave();
		gesture = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityx, float velocityy) {
				if (e1.getY() - e2.getY() > move) {
					getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					return true;
				} else if (e2.getY() - e1.getY() > move) {
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					return true;
				} else if (e1.getX() - e2.getX() > move) {
					if (viewindex == 0) {
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						timeset = true;
						timername = timernameedittext.getText().toString();
						// timeset is to show that the timepicker object has
						// been initialised
					}
					if (viewindex < 1) {
						right = true;
						left = false;
						try {
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(), 0);
						} catch (Exception e) {
						}
						viewindex++;
						init();
					}
					return true;
				} else if (e2.getX() - e1.getX() > move) {
					if (viewindex == 0) {
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						timeset = true;
						timername = timernameedittext.getText().toString();
						// timeset is to show that the timepicker object has
						// been initialised
					}
					if (viewindex > 0) {
						right = false;
						left = true;
						try {
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(), 0);
						} catch (Exception e) {
						}
						viewindex--;
						init();
					}
					return true;
				}
				return false;
			}
		});
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gesture.onTouchEvent(event);
	}

	@Override
	public void onSaveInstanceState(Bundle outstate) {
		// when rotate happens this is called to save data
		super.onSaveInstanceState(outstate);
		outstate.putInt("viewindex", viewindex);
		try {
			hour = timertimepicker.getCurrentHour();
			minute = timertimepicker.getCurrentMinute();
			second = timersecondtimepicker.getCurrentMinute();
		} catch (Exception e) {
			// the only reason this fails is if the person has not set any time
			// yet, so they won't notice the failure
		}
		outstate.putInt("hour", hour);
		outstate.putInt("minute", minute);
		outstate.putInt("second", second);
		outstate.putBoolean("timeset", timeset);
		if (viewindex == 0) {
			outstate.putString("timername", timernameedittext.getText()
					.toString());
		} else {
			outstate.putString("timername", timername);
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedinstancestate) {
		// and when rotate is finished, this is called to restore that saved
		// data
		super.onRestoreInstanceState(savedinstancestate);
		viewindex = savedinstancestate.getInt("viewindex");
		timername = savedinstancestate.getString("timername");
		hour = savedinstancestate.getInt("hour");
		minute = savedinstancestate.getInt("minute");
		second = savedinstancestate.getInt("second");
		timeset = savedinstancestate.getBoolean("timeset");
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				boolean success = data.getBooleanExtra("success", false);
				if (success) {
					// temp was set earlier, so it should not have to be set
					// again
					Intent returnintent = new Intent();
					returnintent.putExtra("success", true);
					returnintent.putExtra("hour", temp.gethours());
					returnintent.putExtra("minute", temp.getminutes());
					returnintent.putExtra("second", temp.getseconds());
					returnintent.putExtra("name", temp.getname());
					setResult(RESULT_OK, returnintent);
					finish();
				}
			}
		}
	}

	public void init() {
		// which font will be the display font, based upon user preference
		if (settings.getBoolean("newfont", false)) {
			digitalfont = Typeface.createFromAsset(getAssets(), "NEWDIGI.ttf");
		} else {
			digitalfont = Typeface.createFromAsset(getAssets(), "DS-DIGI.ttf");
		}
		clockfont = Typeface.createFromAsset(getAssets(), "Sony_Sketch_EF.ttf");
		setview();
		listenswitcher();
		whichview(viewindex);
	}

	private void listenswitcher() {
		// this is the listener for the navigation buttons at the top
		if (settings.getBoolean("topnavigation", false)) {
			previousbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (viewindex == 0) {
						timername = timernameedittext.getText().toString();
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						timeset = true;
					}
					if (viewindex > 0) {
						right = false;
						left = true;
						try {
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(), 0);
						} catch (Exception e) {
						}
						viewindex--;
						init();
					}
				}
			});
			nextbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (viewindex == 0) {
						timername = timernameedittext.getText().toString();
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						timeset = true;
					}
					if (viewindex < 1) {
						right = true;
						left = false;
						try {
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus()
									.getWindowToken(), 0);
						} catch (Exception e) {
						}
						viewindex++;
						init();
					}
				}
			});
		}
	}

	private void whichview(int index) {
		// this starts methods, and calls the classes related to functions
		// based on which index in the main view that the user is on
		if (viewindex == 0) {
			timereditor();

		} else if (viewindex == 1) {
			presettimereditor();
		}
	}

	private void setview() {
		// adds views into current layout
		inflater = getLayoutInflater();
		// just a default, will be overridden
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(300);
		if (left) {
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					-1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(300);
		} else if (right) {
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(300);
		}

		root = (ViewGroup) inflater.inflate(R.layout.blank, null);
		viewanimator = new ViewAnimator(this);
		viewanimator.setAnimateFirstView(true);
		viewanimator.setOutAnimation(animation);
		viewanimator.removeAllViews();
		// adds top navigation
		if (settings.getBoolean("topnavigation", false)) {
			tmpView = inflater.inflate(R.layout.switcher, null);
			root.addView(tmpView);
		} else {
			setContentView(R.layout.blank);
		}
		// adds the main function at that view index
		if (viewindex == 0) {
			tmpView = inflater.inflate(R.layout.timersetup, null);
			root.addView(tmpView);
		} else if (viewindex == 1) {
			tmpView = inflater.inflate(R.layout.timersetuppresets, null);
			root.addView(tmpView);
		}
		// adds a digital clock to the bottom of the screen
		if (settings.getBoolean("bottomclock", false)) {
			tmpView = inflater.inflate(R.layout.clock, null);
			// just sets it to a number that's way too big to ever possibly be
			// reached(in this day and age)
			tmpView.setMinimumHeight(5000);
			root.addView(tmpView);
		}
		tmpView = inflater.inflate(R.layout.blank, root);
		viewanimator.addView(tmpView);
		viewanimator.setAnimation(animation);
		this.setContentView(viewanimator);
		if (settings.getBoolean("topnavigation", false)) {
			previousbutton = (fontbutton) findViewById(R.id.previousbutton);
			nextbutton = (fontbutton) findViewById(R.id.nextbutton);
			previoustextview = (fonttextview) findViewById(R.id.previoustextview);
			currenttextview = (fonttextview) findViewById(R.id.currenttextview);
			nexttextview = (fonttextview) findViewById(R.id.nexttextview);
		}
		if (settings.getBoolean("bottomclock", false)) {
			DigitalClock clock = (DigitalClock) findViewById(R.id.digitalClock1);
			clock.setTypeface(clockfont);
		}
	}

	private void timereditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.timersetup);
			previousbutton.setText(R.string.none);
			nextbutton.setText(R.string.presettimers);
		}
		timertimepicker = (TimePicker) findViewById(R.id.timertimepicker);
		timersecondtimepicker = (TimePicker) findViewById(R.id.timersecondtimepicker);
		timersecondtimepicker.setIs24HourView(true);
		timertimepicker.setIs24HourView(true);
		if (!timeset) {
			timertimepicker.setCurrentHour(0);
			timertimepicker.setCurrentMinute(0);
			timersecondtimepicker.setCurrentMinute(0);
		} else {
			timertimepicker.setCurrentHour(hour);
			timertimepicker.setCurrentMinute(minute);
			timersecondtimepicker.setCurrentMinute(second);
		}
		Drawable plus = getResources().getDrawable(R.drawable.plus);
		Drawable minus = getResources().getDrawable(R.drawable.minus);
		//TODO something broke
		if (Build.VERSION.SDK_INT > 7) {
			try {
				for (int i = 0; i < 2; i++) {
					((ViewGroup) ((ViewGroup) timertimepicker.getChildAt(0))
							.getChildAt(i)).getChildAt(0)
							.setBackgroundResource(R.drawable.whitebutton);
					((ViewGroup) ((ViewGroup) timertimepicker.getChildAt(0))
							.getChildAt(i)).getChildAt(1)
							.setBackgroundResource(R.drawable.white_button);
					((ViewGroup) ((ViewGroup) timertimepicker.getChildAt(0))
							.getChildAt(i)).getChildAt(2)
							.setBackgroundResource(R.drawable.whitebutton);
					((ImageButton) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(i)).getChildAt(0))
							.setImageDrawable(plus);
					((EditText) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(i)).getChildAt(1))
							.setTypeface(clockfont);
					((EditText) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(i)).getChildAt(1))
							.setTextColor(0xFFFFFFFF);
					((ImageButton) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(i)).getChildAt(2))
							.setImageDrawable(minus);
				}
			} catch (Throwable t) {
			}
			try {
				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						((ViewGroup) timersecondtimepicker.getChildAt(0))
								.getChildAt(i).setVisibility(View.GONE);
					}
					if (i == 1) {
						((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(0)
								.setBackgroundResource(R.drawable.whitebutton);
						((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(1)
								.setBackgroundResource(R.drawable.white_button);
						((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(2)
								.setBackgroundResource(R.drawable.whitebutton);
						((ImageButton) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(0))
								.setImageDrawable(plus);
						((EditText) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(1))
								.setTypeface(clockfont);
						((EditText) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(1))
								.setTextColor(0xFFFFFFFF);
						((ImageButton) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(2))
								.setImageDrawable(minus);
					}
				}
			} catch (Throwable t) {
			}
		} else {
			try {
				for (int i = 0; i < 2; i++) {
					((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(0).setBackgroundResource(
									R.drawable.whitebutton);
					((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(1).setBackgroundResource(
									R.drawable.white_button);
					((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(2).setBackgroundResource(
									R.drawable.whitebutton);
					((ImageButton) ((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(0)).setImageDrawable(plus);
					((EditText) ((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(1)).setTypeface(clockfont);
					((EditText) ((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(1)).setTextColor(0xFFFFFFFF);
					((ImageButton) ((ViewGroup) ((ViewGroup) ((ViewGroup) timertimepicker
							.getChildAt(0)).getChildAt(0)).getChildAt(i))
							.getChildAt(2)).setImageDrawable(minus);
				}
			} catch (Throwable t) {
			}
			try {
				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(i)).getChildAt(0)
								.setVisibility(View.GONE);
					}
					if (i == 1) {
						((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(0).setBackgroundResource(
										R.drawable.whitebutton);
						((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(1).setBackgroundResource(
										R.drawable.white_button);
						((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(2).setBackgroundResource(
										R.drawable.whitebutton);
						((ImageButton) ((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(0)).setImageDrawable(plus);
						((EditText) ((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(1)).setTypeface(clockfont);
						((EditText) ((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(1)).setTextColor(0xFFFFFFFF);
						((ImageButton) ((ViewGroup) ((ViewGroup) ((ViewGroup) timersecondtimepicker
								.getChildAt(0)).getChildAt(0)).getChildAt(i))
								.getChildAt(2)).setImageDrawable(minus);
					}
				}
			} catch (Throwable t) {
			}
		}
		timernametextview = (fonttextview) findViewById(R.id.timernametextview);
		timernameedittext = (EditText) findViewById(R.id.timernameedittext);
		finishtimerbutton = (fontbutton) findViewById(R.id.finishtimerbutton);
		canceltimerbutton = (fontbutton) findViewById(R.id.canceltimerbutton);
		addpresettimerbutton = (fontbutton) findViewById(R.id.addpresettimerbutton);
		if (!timername.equals("")) {
			timernameedittext.setText(timername);
		}
		finishtimerbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timertimepicker.getCurrentHour() != 0
						|| timertimepicker.getCurrentMinute() != 0
						|| timersecondtimepicker.getCurrentMinute() != 0) {
					try {
						timername = timernameedittext.getText().toString();
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						Intent returnintent = new Intent();
						returnintent.putExtra("success", true);
						returnintent.putExtra("hour", hour);
						returnintent.putExtra("minute", minute);
						returnintent.putExtra("second", second);
						returnintent.putExtra("name", timername);
						setResult(RESULT_OK, returnintent);
						finish();
					} catch (Exception e) {
						displaytoast("One or more fields is not filled out, please inspect each field");
					}
				} else {
					displaytoast("One or more fields is not filled out, please inspect each field");
				}
			}
		});
		canceltimerbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent returnintent = new Intent();
				returnintent.putExtra("success", false);
				setResult(RESULT_OK, returnintent);
				finish();
			}
		});
		addpresettimerbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (timertimepicker.getCurrentHour() != 0
						|| timertimepicker.getCurrentMinute() != 0
						|| timersecondtimepicker.getCurrentMinute() != 0) {
					try {
						timername = timernameedittext.getText().toString();
						hour = timertimepicker.getCurrentHour();
						minute = timertimepicker.getCurrentMinute();
						second = timersecondtimepicker.getCurrentMinute();
						addpreset();
						displaytoast("Added preset " + timername);
					} catch (Exception e) {
						displaytoast("One or more fields is not filled out, please inspect each field");
					}
				} else {
					displaytoast("One or more fields is not filled out, please inspect each field");
				}
			}
		});
	}

	private void presettimereditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.presettimers);
			previousbutton.setText(R.string.timersetup);
			nextbutton.setText(R.string.none);
		}
		timerpresetslistview = (ListView) findViewById(R.id.timerpresetslistview);
		updatepresetlist();
		final GestureDetector gestureDetector = new GestureDetector(
				new gesturedetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		timerpresetslistview.setOnTouchListener(gestureListener);
	}

	private void addpreset() {
		pv = new presetvalue();
		pv.sethours(hour);
		pv.setminutes(minute);
		pv.setseconds(second);
		pv.setname(timername);
		presetlist.add(pv);
		savepresets();
	}

	private void addpresetsfromsave() {
		String firstpart = "presetvalue";
		for (int n = 0; n < settings.getInt("numberpresets", 0); n++) {
			pv = new presetvalue();
			pv.sethours(settings.getInt(firstpart + Integer.toString(n)
					+ "hour", 0));
			pv.setminutes(settings.getInt(firstpart + Integer.toString(n)
					+ "minute", 0));
			pv.setseconds(settings.getInt(firstpart + Integer.toString(n)
					+ "second", 0));
			pv.setname(settings.getString(firstpart + Integer.toString(n)
					+ "name", ""));
			presetlist.add(pv);
		}
	}

	private void updatepresetlist() {
		String[] listitems = new String[presetlist.size()];
		for (int n = 0; n < presetlist.size(); n++) {
			temp = presetlist.get(n);
			listitems[n] = temp.getname() + ", " + temp.gethours() + ":"
					+ temp.getminutes() + ":" + temp.getseconds();
		}
		presetadapter = new lvadapter(this, listitems, clockfont, null, null,
				largefont);
		timerpresetslistview.setAdapter(presetadapter);
	}

	private void savepresets() {
		String firstpart = "presetvalue";
		for (int n = 0; n < settings.getInt("numberpresets", 0); n++) {
			editor.remove(firstpart + Integer.toString(n) + "name");
			editor.remove(firstpart + Integer.toString(n) + "minute");
			editor.remove(firstpart + Integer.toString(n) + "hour");
			editor.remove(firstpart + Integer.toString(n) + "second");
		}
		editor.putInt("numberpresets", presetlist.size());
		for (int n = 0; n < presetlist.size(); n++) {
			editor.putString(firstpart + Integer.toString(n) + "name",
					presetlist.get(n).getname());
			editor.putInt(firstpart + Integer.toString(n) + "minute",
					presetlist.get(n).getminutes());
			editor.putInt(firstpart + Integer.toString(n) + "hour", presetlist
					.get(n).gethours());
			editor.putInt(firstpart + Integer.toString(n) + "second",
					presetlist.get(n).getseconds());
		}
		editor.commit();
	}

	public void displaytoast(CharSequence text) {
		// displays a toast with the information passed to this method
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	private void onitemclick(int position) {
		temp = presetlist.get(position);
		Intent launch = new Intent(this, preseteditoractivity.class);
		launch.putExtra("hour", temp.gethours());
		launch.putExtra("minute", temp.getminutes());
		launch.putExtra("second", temp.getseconds());
		launch.putExtra("name", temp.getname());
		startActivityForResult(launch, 1);
	}

	class gesturedetector extends SimpleOnGestureListener {
		// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			ListView lv = timerpresetslistview;
			int pos = lv.pointToPosition((int) e.getX(), (int) e.getY());
			onitemclick(pos);
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityx,
				float velocityy) {
			if (e1.getX() - e2.getX() > move) {
				if (viewindex == 0) {
					hour = timertimepicker.getCurrentHour();
					minute = timertimepicker.getCurrentMinute();
					second = timersecondtimepicker.getCurrentMinute();
					timeset = true;
					timername = timernameedittext.getText().toString();
					// timeset is to show that the timepicker object has been
					// Initialised
				}
				if (viewindex < 1) {
					right = true;
					left = false;
					try {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
					} catch (Exception e) {
					}
					viewindex++;
					init();
				}
				return true;
			} else if (e2.getX() - e1.getX() > move) {
				if (viewindex == 0) {
					hour = timertimepicker.getCurrentHour();
					minute = timertimepicker.getCurrentMinute();
					second = timersecondtimepicker.getCurrentMinute();
					timeset = true;
					timername = timernameedittext.getText().toString();
					// timeset is to show that the timepicker object has been
					// Initialised
				}
				if (viewindex > 0) {
					right = false;
					left = true;
					try {
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), 0);
					} catch (Exception e) {
					}
					viewindex--;
					init();
				}
				return true;
			}
			return false;
		}
	}
}
