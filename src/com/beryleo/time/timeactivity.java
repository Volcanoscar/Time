package com.beryleo.time;
//free of localisation issues

import com.beryleo.time.alarmp.alarms;
import com.beryleo.time.stopwatchp.stopwatch;
import com.beryleo.time.timerp.timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.DigitalClock;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;
//main activity/class for the whole project
//gives context(itself) to most of the rest of the classes, so that they can operate upon the ui
//@SuppressWarnings("deprecation")
public class timeactivity extends Activity{
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public Typeface digitalfont, clockfont;
	public int largefont = 40;
	private stopwatch stopwatchobject;
	private settings settingsobject;
	private about aboutobject;
	private alarms alarmsobject;
	private timer timerobject;
	private ViewAnimator viewanimator;
	public boolean right = true, left;
	//about variables
	public fonttextview madebytextview, contacttextview, popupcredittextview, popupcreditcontacttextview
	, versiontextview, analogclocksecondstextview;
	//alarms variables
	public fontbutton addalarmbutton;
	public fonttextview alarmcounttextview;
	public ListView alarmslistview;
	//settings variables
	public fontbutton lessdecimalplacesbutton, moredecimalplacesbutton, lowerstartscreenbutton, higherstartscreenbutton
	, lowerclocktypebutton, higherclocktypebutton;
	public fonttogglebutton topnavigationtogglebutton, bottomclocktogglebutton, popupstopwatchtogglebutton
	, newfonttogglebutton, clocksecondhandtogglebutton;
	public fonttextview numberdecimalplacesvaluetextview, decimalplacestextview, topnavigationtextview
	, bottomclocktextview, popupstopwatchtextview, newfonttextview, startscreentextview, startscreenvaluetextview
	, clocktypetextview, clocktypevaluetextview, clocksecondhandtextview;
	//stopwatch variables
	public fontbutton startpausebutton, lapresetbutton;
	public fonttextview elapsedtextview;
	public ListView lapslistview;
	//timer variables
	public fontbutton setuptimerbutton, startpausetimerbutton, resettimerbutton;
	public fonttextview timertimetextview;
	//switcher variables
	private fontbutton previousbutton, nextbutton;
	@SuppressWarnings("unused")
	private fonttextview previoustextview, currenttextview, nexttextview;
	public int viewindex = 0;
	public static final int move = 120;
	private GestureDetector gesture;
	//animation
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
		stopwatchobject = new stopwatch(this);
		settingsobject = new settings(this);
		aboutobject = new about(this);
		alarmsobject = new alarms(this);
		timerobject = new timer(this);
		if (!settings.contains("clocksecondhand"))  {
			editor.putBoolean("clocksecondhand", false);
			editor.commit();
		}
		if (settings.contains("stopwatchdatasaved")&&settings.getBoolean("remember", false))  {
			//doesn't really work
			//haven't really tested either
			stopwatchobject.restorestopwatchdata();
		}
		if (!settings.contains("startscreen")) {
			editor.putInt("startscreen", 0);
			editor.commit();
		}
		if (!settings.contains("clocktype")) {
			editor.putInt("clocktype", 0);
			editor.commit();
		}
		if (!settings.contains("numberpresets")) {
			editor.putInt("numberpresets", 0);
			editor.commit();
		}
		viewindex = settings.getInt("startscreen", -1);
		if (!settings.contains("poweredon")) {
			//initial values
			editor.putInt("numberlaps", 0);
			editor.putBoolean("poweredon", true);
			editor.putInt("places", 2);
			editor.putBoolean("bottomclock", true);
			editor.putBoolean("topnavigation", true);
			editor.putBoolean("popupstopwatch", true);
			editor.putBoolean("newfont", true);
			editor.putBoolean("remember", true);
			editor.commit();
		}
		gesture = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityx, float velocityy) {
				if (e1.getY() - e2.getY() > move) {
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
					return true;
				}
				else if (e2.getY() - e1.getY() > move) {
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					return true;
				}
				else if (e1.getX() - e2.getX() > move) {
					if (viewindex < 6) {
						right = true;
						left = false;
						viewindex++;
						init();
					}
					return true;
				}
				else if (e2.getX() - e1.getX() > move) {
					if (viewindex > 0) {
						right = false;
						left = true;
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
		try {
			return gesture.onTouchEvent(event);
		}
		catch (Exception e) {
			return false;
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		if (viewindex==1) {
			alarms();
		}
		if(viewindex==4) {
			intervals();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				boolean success = data.getBooleanExtra("success", false);
				if (success) {
					int hour = data.getIntExtra("hour", 0);
					int minute = data.getIntExtra("minute", 0);
					int second = data.getIntExtra("second", 0);
					timerobject.settime(hour, minute, second);
					String name = data.getStringExtra("name");
					startpausetimerbutton.setEnabled(true);
					resettimerbutton.setEnabled(false);
					timertimetextview.setText("" + hour + ":" + minute + ":" + second);
					displaytoast("TIMER SET TO: "  + hour + ":" + minute + ":" + second);
					timerobject.timermain();
					displaytoast("Setup timer " + name);
				}
			}
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outstate) {
		//when rotate happens this is called to save data
		//2012-7-23: only holds data for stopwatch and main, as stopwatch is the only complete function currently
		//now also holds stuff for timer
		super.onSaveInstanceState(outstate);
		outstate.putInt("viewindex", viewindex);
		outstate.putDouble("seconds", stopwatchobject.seconds);
		outstate.putInt("minutes", stopwatchobject.minutes);
		outstate.putInt("hours", stopwatchobject.hours);
		outstate.putLong("start", stopwatchobject.start);
		outstate.putLong("end", stopwatchobject.end);
		outstate.putLong("freeze", stopwatchobject.freeze);
		outstate.putLong("melt", stopwatchobject.melt);
		outstate.putLong("difference", stopwatchobject.difference);
		outstate.putBoolean("started", stopwatchobject.started);
		outstate.putBoolean("paused", stopwatchobject.paused);
		outstate.putParcelableArrayList("laplist", stopwatchobject.laplist);
		//timer
		outstate.putInt("timerhour", timerobject.hour);
		outstate.putInt("timerminute", timerobject.minute);
		outstate.putDouble("timersecond", timerobject.second);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedinstancestate) {
		//and when rotate is finished, this is called to restore that saved data
		//2012-7-23: only restores stopwatch data + main ui
		super.onRestoreInstanceState(savedinstancestate);
		viewindex = savedinstancestate.getInt("viewindex");
		stopwatchobject.seconds = savedinstancestate.getDouble("seconds");
		stopwatchobject.minutes = savedinstancestate.getInt("minutes");
		stopwatchobject.hours = savedinstancestate.getInt("hours");
		stopwatchobject.start = savedinstancestate.getLong("start");
		stopwatchobject.end = savedinstancestate.getLong("end");
		stopwatchobject.freeze = savedinstancestate.getLong("freeze");
		stopwatchobject.melt = savedinstancestate.getLong("melt");
		stopwatchobject.difference = savedinstancestate.getLong("difference");
		stopwatchobject.started = savedinstancestate.getBoolean("started");
		stopwatchobject.paused = savedinstancestate.getBoolean("paused");
		stopwatchobject.laplist = savedinstancestate.getParcelableArrayList("laplist");
		timerobject.settime(savedinstancestate.getInt("timerhour"), savedinstancestate.getInt("timerminute"), savedinstancestate.getInt("timersecond"));   	
		init();
	}
	@Override
	public void onDestroy() {
		stopwatchobject.savestopwatchdata();
		super.onDestroy();
	}
	public void init() {
		//which font will be the display font, based upon user preference
		if (settings.getBoolean("newfont", false)) {
			digitalfont = Typeface.createFromAsset(getAssets(),"digital-7 (mono).ttf");
		}
		else {
			digitalfont = Typeface.createFromAsset(getAssets(),"DS-DIGI.ttf");	
		}
		clockfont = Typeface.createFromAsset(getAssets(), "Sony_Sketch_EF.ttf");
		setview();
		listenswitcher();
		whichview(viewindex);
	}
	private void listenswitcher() {
		//this is the listener for the navigation buttons at the top
		if (settings.getBoolean("topnavigation", false)) {
			previousbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (viewindex > 0) {
						right = false;
						left = true;
						viewindex--;
						init();
					}
				}
			});
			nextbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (viewindex < 6) {
						right = true;
						left = false;
						viewindex++;
						init();
					}
				}
			});
		}
	}
	private void whichview(int index)
	{
		//this starts methods, and calls the classes related to functions
		//based on which index in the main view that the user is on
		if (viewindex==0) {
			bigclock();
		}
		else if (viewindex==1) {
			alarms();
		}
		else if (viewindex==2) {
			stopwatch();
			if (stopwatchobject.started) {
				startpausebutton.setText(R.string.pause);
				lapresetbutton.setEnabled(true);
				stopwatchobject.update.removeCallbacks(stopwatchobject.updatestopwatchtask);
				stopwatchobject.update.postDelayed(stopwatchobject.updatestopwatchtask, 1);
			}
			if (stopwatchobject.paused) {
				startpausebutton.setText(R.string.resume);
				lapresetbutton.setText(R.string.reset);
				stopwatchobject.blink.removeCallbacks(stopwatchobject.blinkstopwatchtask);
				stopwatchobject.blink.postDelayed(stopwatchobject.blinkstopwatchtask, 1);
			}
		}
		else if (viewindex==3) {
			timer();
		}
		else if (viewindex==4) {
			intervals();
		}
		else if (viewindex==5) {
			settings();
		}
		else if (viewindex==6) {
			about();
		}
	}
	private void setview()
	{
		//adds views into current layout
		inflater = getLayoutInflater();
		//just a default, will be overridden
		animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
				);
		animation.setDuration(300);
		if (left) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
					);
			animation.setDuration(300);
		}
		else if (right) {
			animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
					);
			animation.setDuration(300);
		}
		root = (ViewGroup )inflater.inflate(R.layout.blank, null);
		viewanimator = new ViewAnimator(this);
		viewanimator.setAnimateFirstView(true);
		viewanimator.setOutAnimation(animation);
		viewanimator.removeAllViews();
		//adds top navigation
		if (settings.getBoolean("topnavigation", false)) {
			tmpView = inflater.inflate(R.layout.switcher, null);
			root.addView(tmpView);
		}
		else {
			setContentView(R.layout.blank);
		}
		//adds the main function at that view index
		if (viewindex==0) {
			if (settings.getBoolean("clocksecondhand", false)) {
				if (settings.getInt("clocktype", 0)==0) {
					tmpView = inflater.inflate(R.layout.widget_main, null);
				}
				else if (settings.getInt("clocktype", 0)==1) {
					tmpView = inflater.inflate(R.layout.widgetsquare_main, null);
				}
				else if (settings.getInt("clocktype", 0)==2) {
					tmpView = inflater.inflate(R.layout.widgetroundedsquare_main, null);
				}
				else if (settings.getInt("clocktype", 0)==3) {
					tmpView = inflater.inflate(R.layout.widgetintersections_main, null);
				}
			}
			else {
				if (settings.getInt("clocktype", 0)==0) {
					tmpView = inflater.inflate(R.layout.widget, null);
				}
				else if (settings.getInt("clocktype", 0)==1) {
					tmpView = inflater.inflate(R.layout.widgetsquare, null);
				}
				else if (settings.getInt("clocktype", 0)==2) {
					tmpView = inflater.inflate(R.layout.widgetroundedsquare, null);
				}
				else if (settings.getInt("clocktype", 0)==3) {
					tmpView = inflater.inflate(R.layout.widgetintersections, null);
				}
			}
			root.addView(tmpView);
		}
		else if (viewindex==1) {
			tmpView = inflater.inflate(R.layout.alarms, null);
			root.addView(tmpView);
		}
		else if (viewindex==2) {
			tmpView = inflater.inflate(R.layout.stopwatch, null);
			root.addView(tmpView);
		}
		else if (viewindex==3) {
			tmpView = inflater.inflate(R.layout.timer, null);
			root.addView(tmpView);
		}
		else if (viewindex==4) {
			tmpView = inflater.inflate(R.layout.alarms, null);
			root.addView(tmpView);
		}
		else if (viewindex==5) {
			tmpView = inflater.inflate(R.layout.settings, null);
			root.addView(tmpView);
		}
		else if (viewindex==6) {
			tmpView = inflater.inflate(R.layout.about, null);
			root.addView(tmpView);
		}
		//adds a digital clock to the bottom of the screen
		if (viewindex!=0&&settings.getBoolean("bottomclock", false)) {
			tmpView = inflater.inflate(R.layout.clock, null);
			//just sets it to a number that's way too big to ever possibly be reached(in this day and age)
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
		if (viewindex!=0&&settings.getBoolean("bottomclock", false)) {
			DigitalClock clock = (DigitalClock) findViewById(R.id.digitalClock1);
			clock.setTypeface(clockfont);
		}
	}
	private void bigclock() {
		//just a big clock as the main function
		//is an analogue clock
		//should probably add an option to make it a digital clock
		//TODO
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.clock);
			previousbutton.setText(R.string.none);
			nextbutton.setText(R.string.alarms);
		}
	}
	private void alarms() {
		//will be the list of alarms that the user has
		//2012-7-23: not done, need to add service and broadcast receiver portions to make this function work
		//2013-10-22: I believe it has been done for some time now
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarms);
			previousbutton.setText(R.string.clock);
			nextbutton.setText(R.string.stopwatch);
		}
		addalarmbutton = (fontbutton) findViewById(R.id.addalarmbutton);
		alarmslistview = (ListView) findViewById(R.id.alarmslistview);
		alarmcounttextview = (fonttextview) findViewById(R.id.alarmcounttextview);
		alarmsobject.alarmsmain(true);
	}
	private void stopwatch() {
		//stopwatch function
		//on first time entering this part of application, shows how to launch popup
		if (!settings.contains("hintpopupstopwatch")) {
			displaytoast("Press the time readout to launch the popup stopwatch");
			editor.putBoolean("hintpopupstopwatch", true);
			editor.commit();
		}
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.stopwatch);
			previousbutton.setText(R.string.alarms);
			nextbutton.setText(R.string.timer);
		}
		startpausebutton = (fontbutton) findViewById(R.id.startpausebutton);
		lapresetbutton = (fontbutton) findViewById(R.id.lapresetbutton);
		elapsedtextview = (fonttextview) findViewById(R.id.elapsedtextview);
		lapslistview = (ListView) findViewById(R.id.lapslistview);
		stopwatchobject.stopwatchmain();
	}
	private void timer() {
		//timer
		//2012-7-23: not done at all, need alarms done first
		//2012-8-11: will be independent of alarms
		//2013-10-22: I believe this is also done
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.timer);
			previousbutton.setText(R.string.stopwatch);
			nextbutton.setText(R.string.intervals);
		}
		setuptimerbutton = (fontbutton) findViewById(R.id.setuptimerbutton);
		startpausetimerbutton = (fontbutton) findViewById(R.id.startpausetimerbutton);
		resettimerbutton = (fontbutton) findViewById(R.id.resettimerbutton);
		timertimetextview = (fonttextview) findViewById(R.id.timertimetextview);
		timerobject.timermain();

	}
	private void intervals() {
		//interval timer; e.g. would beep every fifteen minutes
		//2012-7-23: entirely relies upon the alarm portion, so not done
		//2013-10-22: alarms was entirely finished, hence this
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.intervals);
			previousbutton.setText(R.string.timer);
			nextbutton.setText(R.string.settings);
		}
		addalarmbutton = (fontbutton) findViewById(R.id.addalarmbutton);
		alarmslistview = (ListView) findViewById(R.id.alarmslistview);
		alarmcounttextview = (fonttextview) findViewById(R.id.alarmcounttextview);
		alarmsobject.alarmsmain(false);
	}
	private void settings() {
		//settings
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.settings);
			previousbutton.setText(R.string.intervals);
			nextbutton.setText(R.string.about);
		}
		lessdecimalplacesbutton = (fontbutton) findViewById(R.id.lessdecimalplacesbutton);
		moredecimalplacesbutton = (fontbutton) findViewById(R.id.moredecimalplacesbutton);
		numberdecimalplacesvaluetextview = (fonttextview) findViewById(R.id.numberdecimalplacesvaluetextview);
		topnavigationtogglebutton = (fonttogglebutton) findViewById(R.id.topnavigationtogglebutton);
		bottomclocktogglebutton = (fonttogglebutton) findViewById(R.id.bottomclocktogglebutton);
		popupstopwatchtogglebutton = (fonttogglebutton) findViewById(R.id.popupstopwatchtogglebutton);
		newfonttogglebutton = (fonttogglebutton) findViewById(R.id.newfonttogglebutton);
		decimalplacestextview  = (fonttextview) findViewById(R.id.decimalplacestextview);
		topnavigationtextview  = (fonttextview) findViewById(R.id.topnavigationtextview);
		bottomclocktextview  = (fonttextview) findViewById(R.id.bottomclocktextview);
		popupstopwatchtextview = (fonttextview) findViewById(R.id.popupstopwatchtextview);
		newfonttextview = (fonttextview) findViewById(R.id.newfonttextview);
		lowerstartscreenbutton = (fontbutton) findViewById(R.id.lowerstartscreenbutton);
		higherstartscreenbutton = (fontbutton) findViewById(R.id.higherstartscreenbutton);
		lowerclocktypebutton = (fontbutton) findViewById(R.id.lowerclocktypebutton);
		higherclocktypebutton = (fontbutton) findViewById(R.id.higherclocktypebutton);
		startscreentextview = (fonttextview) findViewById(R.id.startscreentextview);
		startscreenvaluetextview = (fonttextview) findViewById(R.id.startscreenvaluetextview);
		clocktypetextview = (fonttextview) findViewById(R.id.clocktypetextview);
		clocktypevaluetextview = (fonttextview) findViewById(R.id.clocktypevaluetextview);
		clocksecondhandtextview = (fonttextview) findViewById(R.id.clocksecondhandtextview);
		clocksecondhandtogglebutton = (fonttogglebutton) findViewById(R.id.clocksecondhandtogglebutton);
		settingsobject.settingsmain();
	}
	private void about() {
		//about the developer/app
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.about);
			previousbutton.setText(R.string.settings);
			nextbutton.setText(R.string.none);
		}
		madebytextview = (fonttextview) findViewById(R.id.madebytextview);
		contacttextview = (fonttextview) findViewById(R.id.contacttextview);
		popupcredittextview = (fonttextview) findViewById(R.id.popupcredittextview);
		popupcreditcontacttextview = (fonttextview) findViewById(R.id.popupcreditcontacttextview);
		versiontextview = (fonttextview) findViewById(R.id.versiontextview);
		analogclocksecondstextview = (fonttextview) findViewById(R.id.analogclocksecondstextview);
		aboutobject.aboutmain();
	}
	public void displaytoast(CharSequence text) {
		//displays a toast with the information passed to this method
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
