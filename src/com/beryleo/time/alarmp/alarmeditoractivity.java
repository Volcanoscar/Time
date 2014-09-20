package com.beryleo.time.alarmp;
//fixed all localisation issues
//no localisation issues present
//fixed spacing issues when using localised strings
import java.util.Calendar;

import com.beryleo.time.R;
import com.beryleo.time.lvadapter;
import com.beryleo.time.alarmp.alarmprovidermetadata.alarmtablemetadata;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import 	android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewAnimator;
import com.beryleo.time.fonttextview;
import com.beryleo.time.fontbutton;
import com.beryleo.time.fonttogglebutton;
@SuppressWarnings("deprecation")
public class alarmeditoractivity extends Activity{
	//for scheduling alarms
	private AlarmManager am;
	private PendingIntent sender;
	Calendar calendar;
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public Typeface digitalfont, clockfont;
	public int largefont = 40;
	private ViewAnimator viewanimator;
	private boolean right = true, left;
	//switcher variables
	private fontbutton previousbutton, nextbutton;
	@SuppressWarnings("unused")
	private fonttextview previoustextview, currenttextview, nexttextview;
	private int viewindex = 0;
	private static final int move = 120;
	private GestureDetector gesture;
	//first [main] screen variables
	@SuppressWarnings("unused")
	private fonttextview alarmnametextview, alarmontextview, alarmvibratetextview;
	private EditText alarmnameedittext;
	private fonttogglebutton alarmontogglebutton, alarmvibratetogglebutton;
	private fontbutton alarmaddbutton, alarmdeletebutton;
	private boolean alarmon = true, alarmvibrate;
	private String alarmname = "";
	//second [time] screen variables
	private TimePicker alarmtimetimepicker, alarmsecondtimepicker;
	private fonttextview repeatinfinitelytextview,alarmrepeatstextview;
	private fonttogglebutton repeatinfinitelytogglebutton;
	private EditText alarmrepeatsedittext;
	private int hour, minute, second = 0;
	private String repeats = "0";
	private boolean timeset, infiniterepeat = true;
	//third [day] screen variables
	@SuppressWarnings("unused")
	private fonttextview mondaytextview, tuesdaytextview, wednesdaytextview, 
	thursdaytextview, fridaytextview, saturdaytextview, sundaytextview;
	private fonttogglebutton mondaytogglebutton, tuesdaytogglebutton, wednesdaytogglebutton,
	thursdaytogglebutton, fridaytogglebutton, saturdaytogglebutton, sundaytogglebutton;
	private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	//fourth [ringtone] screen variables
	private fonttextview alarmringtonecurrenttextview;
	private ListView alarmringtonelistview;
	private fonttogglebutton alarmsilenttogglebutton;
	private lvadapter alarmringtoneadapter;
	private RingtoneManager ringtonemanager;
	private boolean alarmsilent = true;
	private int ringtoneid;
	private String ringtonetitle = "";
	private String[] listitems;
	//universal
	private boolean alarm;
	private boolean editing;
	private int id;
	private int interval;
	//animation
	private LayoutInflater inflater;
	private View tmpView;
	private Animation animation;
	private ViewGroup root;
	//other
	private Cursor c;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		Bundle extras = getIntent().getExtras(); 
		alarm = extras.getBoolean("alarm");
		if(extras.containsKey("editing")) {
			editing = true;
			hour = extras.getInt("hour");
			minute = extras.getInt("minute");
			second = extras.getInt("second");
			timeset = true;
			alarmon = extras.getBoolean("alarmon");
			alarmvibrate = extras.getBoolean("alarmvibrate");
			alarmname = extras.getString("alarmname");
			monday = extras.getBoolean("monday");
			tuesday = extras.getBoolean("tuesday");
			wednesday = extras.getBoolean("wednesday");
			thursday = extras.getBoolean("thursday");
			friday = extras.getBoolean("friday");
			saturday = extras.getBoolean("saturday");
			sunday = extras.getBoolean("sunday");
			alarmsilent = extras.getBoolean("alarmsilent");
			ringtoneid = extras.getInt("ringtoneid");
			ringtonetitle = extras.getString("ringtonetitle");
			id = extras.getInt("id");
			interval = extras.getInt("interval");
			infiniterepeat = extras.getBoolean("infiniterepeat");
			repeats = "" + extras.getInt("repeats");
			if (!alarm) {
				if (interval>=60*60) {
					hour = interval/60/60;
					minute = (interval - (60*60*hour))/60;
					second = (interval - (60*minute) - (60*60*hour));
				}
				else if (interval>=60) {
					hour = 0;
					minute = interval/60;
					second = (interval - (60*minute));
				}
				else {
					hour = 0;
					minute = 0;
					second = interval;
				}
			}
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
					if (viewindex==0) {
						alarmname = alarmnameedittext.getText().toString();
					}
					else if (viewindex==1) {
						hour = alarmtimetimepicker.getCurrentHour();
						minute = alarmtimetimepicker.getCurrentMinute();
						second = alarmsecondtimepicker.getCurrentMinute();
						timeset = true;
						//timeset is to show that the timepicker object has been initialised
						repeats = alarmrepeatsedittext.getText().toString();
					}
					if (viewindex < 3) {
						right = true;
						left = false;
						try {
							InputMethodManager imm = (InputMethodManager) getSystemService(
									INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
						catch(Exception e){}
						viewindex++;
						init();
					}
					return true;
				}
				else if (e2.getX() - e1.getX() > move) {
					if (viewindex==0) {
						alarmname = alarmnameedittext.getText().toString();
					}
					else if (viewindex==1) {
						hour = alarmtimetimepicker.getCurrentHour();
						minute = alarmtimetimepicker.getCurrentMinute();
						second = alarmsecondtimepicker.getCurrentMinute();
						timeset = true;
						repeats = alarmrepeatsedittext.getText().toString();
					}
					if (viewindex > 0) {
						right = false;
						left = true;
						try{
							InputMethodManager imm = (InputMethodManager) getSystemService(
									INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
						catch(Exception e){}
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
		//when rotate happens this is called to save data
		super.onSaveInstanceState(outstate);
		outstate.putInt("viewindex", viewindex);
		outstate.putBoolean("monday", monday);
		outstate.putBoolean("tuesday", tuesday);
		outstate.putBoolean("wednesday", wednesday);
		outstate.putBoolean("thursday", thursday);
		outstate.putBoolean("friday", friday);
		outstate.putBoolean("saturday", saturday);
		outstate.putBoolean("sunday", sunday);
		outstate.putBoolean("alarmon", alarmon);
		outstate.putBoolean("alarmvibrate", alarmvibrate);
		outstate.putBoolean("alarmsilent", alarmsilent);
		outstate.putInt("ringtoneid", ringtoneid);
		outstate.putString("ringtonetitle", ringtonetitle);
		outstate.putBoolean("alarm", alarm);
		outstate.putString("repeats", repeats);
		outstate.putBoolean("infiniterepeat", infiniterepeat);
		outstate.putInt("id", id);
		try {
			hour = alarmtimetimepicker.getCurrentHour();
			minute = alarmtimetimepicker.getCurrentMinute();
			second = alarmsecondtimepicker.getCurrentMinute();
		}
		catch(Exception e) {
			//the only reason this fails is if the person has not set any time yet, so they won't notice the failure
		}
		outstate.putInt("hour", hour);
		outstate.putInt("minute", minute);
		outstate.putInt("second", second);
		outstate.putBoolean("timeset", timeset);
		if (viewindex==0) {
			outstate.putString("alarmname", alarmnameedittext.getText().toString());
		}
		else {
			outstate.putString("alarmname", alarmname);
		}

	}
	@Override
	public void onRestoreInstanceState(Bundle savedinstancestate) {
		//and when rotate is finished, this is called to restore that saved data
		super.onRestoreInstanceState(savedinstancestate);
		viewindex = savedinstancestate.getInt("viewindex");
		monday = savedinstancestate.getBoolean("monday");
		tuesday = savedinstancestate.getBoolean("tuesday");
		wednesday = savedinstancestate.getBoolean("wednesday");
		thursday = savedinstancestate.getBoolean("thursday");
		friday = savedinstancestate.getBoolean("friday");
		saturday = savedinstancestate.getBoolean("saturday");
		sunday = savedinstancestate.getBoolean("sunday");
		alarmon = savedinstancestate.getBoolean("alarmon");
		alarmvibrate = savedinstancestate.getBoolean("alarmvibrate");
		alarmsilent = savedinstancestate.getBoolean("alarmsilent");
		ringtoneid = savedinstancestate.getInt("ringtoneid");
		ringtonetitle = savedinstancestate.getString("ringtonetitle");
		alarmname = savedinstancestate.getString("alarmname");
		hour = savedinstancestate.getInt("hour");
		minute = savedinstancestate.getInt("minute");
		second = savedinstancestate.getInt("second");
		timeset = savedinstancestate.getBoolean("timeset");
		alarm = savedinstancestate.getBoolean("alarm");
		infiniterepeat = savedinstancestate.getBoolean("infiniterepeat");
		repeats = savedinstancestate.getString("repeats");
		id = savedinstancestate.getInt("id");
		init();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK) {
				boolean success = data.getBooleanExtra("success", false);
				if (success) {
					alarmsilent = false;
					alarmsilenttogglebutton.setChecked(alarmsilent);
					ringtoneid = data.getIntExtra("id", 0);
					ringtonetitle = data.getStringExtra("title");
					alarmringtonecurrenttextview.setText(R.string.currentringtoneis);
					alarmringtonecurrenttextview.setText(alarmringtonecurrenttextview.getText() +" "+ ringtonetitle);
					String selected = getString(R.string.selectedringtoneat);
					String commacalled = getString(R.string.commacalled);
					displaytoast(selected +" "+ ringtoneid + commacalled +" "+ ringtonetitle);
				}
			}
		}
	}
	public void init() {
		//which font will be the display font, based upon user preference
		if (settings.getBoolean("newfont", false)) {
			digitalfont = Typeface.createFromAsset(getAssets(),"NEWDIGI.ttf");
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
					if (viewindex==0) {
						alarmname = alarmnameedittext.getText().toString();
					}
					else if (viewindex==1) {
						hour = alarmtimetimepicker.getCurrentHour();
						minute = alarmtimetimepicker.getCurrentMinute();
						second = alarmsecondtimepicker.getCurrentMinute();
						timeset = true;
						repeats = alarmrepeatsedittext.getText().toString();
					}
					if (viewindex > 0) {
						right = false;
						left = true;try{
							InputMethodManager imm = (InputMethodManager) getSystemService(
									INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
						catch(Exception e){}
						viewindex--;
						init();
					}
				}
			});
			nextbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (viewindex==0) {
						alarmname = alarmnameedittext.getText().toString();
					}
					else if (viewindex==1) {
						hour = alarmtimetimepicker.getCurrentHour();
						minute = alarmtimetimepicker.getCurrentMinute();
						second = alarmsecondtimepicker.getCurrentMinute();
						timeset = true;
						repeats = alarmrepeatsedittext.getText().toString();
					}
					if (viewindex < 3) {
						right = true;
						left = false;try{
							InputMethodManager imm = (InputMethodManager) getSystemService(
									INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
						catch(Exception e){}
						viewindex++;
						init();
					}
				}
			});
		}
	}
	private void whichview(int index) {
		//this starts methods, and calls the classes related to functions
		//based on which index in the main view that the user is on
		if (viewindex==0) {
			alarmeditor();
		}
		else if (viewindex==1) {
			alarmtimeeditor();
		}
		else if (viewindex==2) {
			alarmdayeditor();
		}
		else if (viewindex==3) {
			alarmringtoneeditor();
		}
	}
	private void setview() {
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
			tmpView = inflater.inflate(R.layout.alarmeditor, null);
			root.addView(tmpView);
		}
		else if (viewindex==1) {
			tmpView = inflater.inflate(R.layout.alarmtimeeditor, null);
			root.addView(tmpView);
		}
		else if (viewindex==2) {
			tmpView = inflater.inflate(R.layout.alarmdayeditor, null);
			root.addView(tmpView);
		}
		else if (viewindex==3) {
			tmpView = inflater.inflate(R.layout.alarmringtoneeditor, null);
			root.addView(tmpView);
		}
		//adds a digital clock to the bottom of the screen
		if (settings.getBoolean("bottomclock", false)) {
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
		if (settings.getBoolean("bottomclock", false)) {
			DigitalClock clock = (DigitalClock) findViewById(R.id.digitalClock1);
			clock.setTypeface(clockfont);
		}
	}
	private void alarmeditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarmeditor);
			previousbutton.setText(R.string.none);
			nextbutton.setText(R.string.alarmtimeeditor);
		}
		alarmnametextview = (fonttextview) findViewById(R.id.alarmnametextview);
		alarmontextview = (fonttextview) findViewById(R.id.alarmontextview);
		alarmvibratetextview = (fonttextview) findViewById(R.id.alarmvibratetextview);
		alarmnameedittext = (EditText) findViewById(R.id.alarmnameedittext);
		alarmontogglebutton = (fonttogglebutton) findViewById(R.id.alarmontogglebutton);
		alarmvibratetogglebutton = (fonttogglebutton) findViewById(R.id.alarmvibratetogglebutton);
		alarmaddbutton = (fontbutton) findViewById(R.id.alarmaddbutton);
		alarmdeletebutton = (fontbutton) findViewById(R.id.alarmdeletebutton);
		if (!alarmname.equals("")) {
			alarmnameedittext.setText(alarmname);
		}
		alarmontogglebutton.setChecked(alarmon);
		alarmvibratetogglebutton.setChecked(alarmvibrate);
		alarmontogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				alarmon = alarmontogglebutton.isChecked();
			}
		});
		alarmvibratetogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				alarmvibrate = alarmvibratetogglebutton.isChecked();
			}
		});
		if(alarm) {
			alarmaddbutton.setText(R.string.addalarm);
		}
		else {
			alarmaddbutton.setText(R.string.addinterval);
		}
		if (editing&&alarm) {
			alarmaddbutton.setText(R.string.udpatealarm);
		}
		else if (editing) {
			alarmaddbutton.setText(R.string.updateintervalalarm);
		}
		alarmaddbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				boolean success = addalarm();
				if (success) {
					//start the intent for scheduling all the alarms at boot time
					Intent scheduler = new Intent(getBaseContext(), schedulealarmservice.class);
					sender = PendingIntent.getBroadcast(getBaseContext(),
							0, scheduler, 0);
					calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					//schedules alarms 5 seconds after this
					calendar.add(Calendar.SECOND, 5);
					am = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
					finish();
				}
			}
		});
		alarmdeletebutton.setEnabled(editing);
		alarmdeletebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ContentResolver cr = getContentResolver();
				Uri uri = alarmprovidermetadata.alarmtablemetadata.CONTENTURI;
				Uri deluri = Uri.withAppendedPath(uri, Integer.toString(id));
				cr.delete(deluri, null, null);
				String deletedalarm = getString(R.string.deletedalarm);
				displaytoast(deletedalarm + id);
				//start the intent for scheduling all the alarms at boot time
				Intent scheduler = new Intent(getBaseContext(), schedulealarmservice.class);
				sender = PendingIntent.getBroadcast(getBaseContext(),
						0, scheduler, 0);
				calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				//schedules alarms 5 seconds after this
				calendar.add(Calendar.SECOND, 5);
				am = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
				finish();
			}
		});
	}

	private void alarmtimeeditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarmtimeeditor);
			previousbutton.setText(R.string.alarmeditor);
			nextbutton.setText(R.string.alarmdayeditor);
		}
		alarmtimetimepicker = (TimePicker) findViewById(R.id.alarmtimetimepicker);
		alarmsecondtimepicker = (TimePicker) findViewById(R.id.alarmsecondtimepicker);
		repeatinfinitelytextview = (fonttextview) findViewById(R.id.repeatinfinitelytextview);
		alarmrepeatstextview = (fonttextview) findViewById(R.id.alarmrepeatstextview);
		repeatinfinitelytogglebutton = (fonttogglebutton) findViewById(R.id.repeatinfinitelytogglebutton);
		alarmrepeatsedittext = (EditText) findViewById(R.id.alarmrepeatsedittext);
		repeatinfinitelytogglebutton.setChecked(infiniterepeat);
		if(alarm) {
			repeatinfinitelytextview.setVisibility(View.GONE);
			alarmrepeatstextview.setVisibility(View.GONE);
			repeatinfinitelytogglebutton.setVisibility(View.GONE);
			alarmrepeatsedittext.setVisibility(View.GONE);
		}
		if (!repeats.equals("")) {
			alarmrepeatsedittext.setText(repeats);
		}
		alarmtimetimepicker.setIs24HourView(true);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		alarmtimetimepicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		alarmsecondtimepicker.setIs24HourView(true);
		if (!alarm&&!timeset) {
			alarmtimetimepicker.setCurrentHour(0);
			alarmtimetimepicker.setCurrentMinute(0);
		}
		if (timeset) {
			alarmtimetimepicker.setCurrentHour(hour);
			alarmtimetimepicker.setCurrentMinute(minute);
		}
		//the "minute" represents secs
		alarmsecondtimepicker.setCurrentMinute(second);
		Drawable plus = getResources().getDrawable( R.drawable.plus);
		Drawable minus = getResources().getDrawable( R.drawable.minus);
		//TODO this is not functioning
		//at least on 4.0+ i believe, no real error, just graphical deficiency
		if (Build.VERSION.SDK_INT > 7) {
			try { 
				for (int i=0; i<2; i++) {
					((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.whitebutton);
					((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1).setBackgroundResource(R.drawable.white_button);
					((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(2).setBackgroundResource(R.drawable.whitebutton);
					((ImageButton)((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(0)).setImageDrawable(plus);
					((EditText)((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1)).setTypeface(clockfont);
					((EditText)((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1)).setTextColor(0xFFFFFFFF);
					((ImageButton)((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(i)).getChildAt(2)).setImageDrawable(minus);
				}
			} 
			catch (Throwable t) {}
			try { 
				for (int i=0; i<2; i++) {
					if(i==0) {
						((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i).setVisibility(View.GONE);
					}
					if (i==1) {
						((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.whitebutton);
						((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1).setBackgroundResource(R.drawable.white_button);
						((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(2).setBackgroundResource(R.drawable.whitebutton);
						((ImageButton)((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(0)).setImageDrawable(plus);
						((EditText)((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1)).setTypeface(clockfont);
						((EditText)((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(1)).setTextColor(0xFFFFFFFF);
						((ImageButton)((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(2)).setImageDrawable(minus);
					}
				}
			} 
			catch (Throwable t) {}
		}
		else {
			try { 
				for (int i=0; i<2; i++) {
					((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.whitebutton);
					((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1).setBackgroundResource(R.drawable.white_button);
					((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(2).setBackgroundResource(R.drawable.whitebutton);
					((ImageButton)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(0)).setImageDrawable(plus);
					((EditText)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1)).setTypeface(clockfont);
					((EditText)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1)).setTextColor(0xFFFFFFFF);
					((ImageButton)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmtimetimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(2)).setImageDrawable(minus);
				}
			} 
			catch (Throwable t) {}
			try { 
				for (int i=0; i<2; i++) {
					if(i==0) {
						((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(i)).getChildAt(0).setVisibility(View.GONE);
					}
					if (i==1) {
						((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(0).setBackgroundResource(R.drawable.whitebutton);
						((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1).setBackgroundResource(R.drawable.white_button);
						((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(2).setBackgroundResource(R.drawable.whitebutton);
						((ImageButton)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(0)).setImageDrawable(plus);
						((EditText)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1)).setTypeface(clockfont);
						((EditText)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(1)).setTextColor(0xFFFFFFFF);
						((ImageButton)((ViewGroup) ((ViewGroup) ((ViewGroup) alarmsecondtimepicker.getChildAt(0)).getChildAt(0)).getChildAt(i)).getChildAt(2)).setImageDrawable(minus);
					}
				}
			} 
			catch (Throwable t) {}

		}
		repeatinfinitelytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				infiniterepeat = repeatinfinitelytogglebutton.isChecked();
			}
		});
	}
	private void alarmdayeditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarmdayeditor);
			previousbutton.setText(R.string.alarmtimeeditor);
			nextbutton.setText(R.string.alarmringtoneeditor);
		}
		mondaytextview = (fonttextview) findViewById(R.id.mondaytextview);
		tuesdaytextview = (fonttextview) findViewById(R.id.tuesdaytextview);
		wednesdaytextview = (fonttextview) findViewById(R.id.wednesdaytextview);
		thursdaytextview = (fonttextview) findViewById(R.id.thursdaytextview);
		fridaytextview = (fonttextview) findViewById(R.id.fridaytextview);
		saturdaytextview = (fonttextview) findViewById(R.id.saturdaytextview);
		sundaytextview = (fonttextview) findViewById(R.id.sundaytextview);
		mondaytogglebutton = (fonttogglebutton) findViewById(R.id.mondaytogglebutton);
		tuesdaytogglebutton = (fonttogglebutton) findViewById(R.id.tuesdaytogglebutton);
		wednesdaytogglebutton = (fonttogglebutton) findViewById(R.id.wednesdaytogglebutton);
		thursdaytogglebutton = (fonttogglebutton) findViewById(R.id.thursdaytogglebutton);
		fridaytogglebutton = (fonttogglebutton) findViewById(R.id.fridaytogglebutton);
		saturdaytogglebutton = (fonttogglebutton) findViewById(R.id.saturdaytogglebutton);
		sundaytogglebutton = (fonttogglebutton) findViewById(R.id.sundaytogglebutton);
		mondaytogglebutton.setChecked(monday);
		tuesdaytogglebutton.setChecked(tuesday);
		wednesdaytogglebutton.setChecked(wednesday);
		thursdaytogglebutton.setChecked(thursday);
		fridaytogglebutton.setChecked(friday);
		saturdaytogglebutton.setChecked(saturday);
		sundaytogglebutton.setChecked(sunday);
		mondaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				monday = mondaytogglebutton.isChecked();
			}
		});
		tuesdaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				tuesday = tuesdaytogglebutton.isChecked();
			}
		});
		wednesdaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				wednesday = wednesdaytogglebutton.isChecked();
			}
		});
		thursdaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				thursday = thursdaytogglebutton.isChecked();
			}
		});
		fridaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				friday = fridaytogglebutton.isChecked();
			}
		});
		saturdaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				saturday = saturdaytogglebutton.isChecked();
			}
		});
		sundaytogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				sunday = sundaytogglebutton.isChecked();
			}
		});
	}
	private void alarmringtoneeditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarmringtoneeditor);
			previousbutton.setText(R.string.alarmdayeditor);
			nextbutton.setText(R.string.none);
		}
		alarmringtonecurrenttextview = (fonttextview) findViewById(R.id.alarmringtonecurrenttextview);
		alarmringtonelistview = (ListView) findViewById(R.id.alarmringtonelistview);
		alarmsilenttogglebutton = (fonttogglebutton) findViewById(R.id.alarmsilenttogglebutton);
		alarmsilenttogglebutton.setChecked(alarmsilent);
		if (alarmsilent) {
			alarmringtonecurrenttextview.setText(R.string.ringtonesilent);
		}
		else {
			if (!ringtonetitle.equals(null)) {
				String currentringtoneis = getString(R.string.currentringtoneis);
				alarmringtonecurrenttextview.setText(currentringtoneis + " " + ringtonetitle);
			}
			else {
				alarmringtonecurrenttextview.setText(R.string.noringtone);
			}
		}
		alarmsilenttogglebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				alarmsilent = alarmsilenttogglebutton.isChecked();
				if (alarmsilent) {
					alarmringtonecurrenttextview.setText(R.string.ringtonesilent);
				}
				else {
					if (!ringtonetitle.equals(null)) {
						String currentringtoneis = getString(R.string.currentringtoneis);
						alarmringtonecurrenttextview.setText(currentringtoneis + " " + ringtonetitle);
					}
					else {
						alarmringtonecurrenttextview.setText(R.string.noringtone);
					}
				}
			}
		});
		ringtonemanager = new RingtoneManager(this);
		c = ringtonemanager.getCursor();
		int idindex = 0;
		int titleindex = 1;
		listitems = new String[c.getCount()];
		int n = 0;
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
			listitems[n] = c.getString(idindex) + ", " + c.getString(titleindex);
			n++;
		}
		alarmringtoneadapter = 
				new lvadapter(this, listitems, clockfont, getResources().getDrawable(R.drawable.note), null, largefont);
		alarmringtonelistview.setAdapter(alarmringtoneadapter);
		final GestureDetector gestureDetector = new GestureDetector(new gesturedetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				try {
					return gestureDetector.onTouchEvent(event); 
				}
				catch(Exception e) {
					return false;
				} 
			}};
			alarmringtonelistview.setOnTouchListener(gestureListener);
	}
	private void launchringtoneeditoractivity(int position, int id) {
		Intent launch = new Intent(this, ringtoneeditoractivity.class);
		launch.putExtra("column", position);
		launch.putExtra("id", id);
		startActivityForResult(launch, 1);
	}
	private boolean addalarm() {
		//also handles alarm updates
		try {
			ContentValues cv = new ContentValues();
			cv.put(alarmtablemetadata.ALARMACTIVATIONTIME, System.currentTimeMillis());
			//putting as seconds because they get truncated otherwise
			if(!editing) {
				if (alarm) {
					cv.put(alarmtablemetadata.ALARMNAME, alarmnameedittext.getText().toString());
					cv.put(alarmtablemetadata.ALARMON, alarmontogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSTARTHOUR, alarmtimetimepicker.getCurrentHour());
					cv.put(alarmtablemetadata.ALARMSTARTMINUTE, alarmtimetimepicker.getCurrentMinute());
					cv.put(alarmtablemetadata.ALARMSTARTSECOND, alarmsecondtimepicker.getCurrentMinute());
					cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, mondaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, fridaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sundaytogglebutton.isChecked());
					//this is an alarm, not an interval, so the next two values will always be 0 and false
					//at least in this alarmeditor
					cv.put(alarmtablemetadata.ALARMINTERVAL, 0);
					cv.put(alarmtablemetadata.ALARMINTERVALON, false);
					cv.put(alarmtablemetadata.ALARMRINGTONE, ringtoneid + "," + ringtonetitle);
					cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibratetogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSILENT, alarmsilenttogglebutton.isChecked());
					//next two are inapplicable
					cv.put(alarmtablemetadata.ALARMREPEATS, 0);
					cv.put(alarmtablemetadata.ALARMINFINITEREPEAT, false);
				}
				else {
					cv.put(alarmtablemetadata.ALARMNAME, alarmnameedittext.getText().toString());
					cv.put(alarmtablemetadata.ALARMON, alarmontogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSTARTHOUR, 0);
					cv.put(alarmtablemetadata.ALARMSTARTMINUTE, 0);
					cv.put(alarmtablemetadata.ALARMSTARTSECOND, 0);
					cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, mondaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, fridaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturdaytogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sundaytogglebutton.isChecked());
					//this is an interval, not an interval
					if (alarmtimetimepicker.getCurrentHour()==0&&alarmtimetimepicker.getCurrentMinute()==0
							&&alarmsecondtimepicker.getCurrentMinute()==0) {
						throw new Exception();
					}
					cv.put(alarmtablemetadata.ALARMINTERVAL, alarmtimetimepicker.getCurrentHour() * 60 * 60
							+ alarmtimetimepicker.getCurrentMinute() * 60 + alarmsecondtimepicker.getCurrentMinute());
					cv.put(alarmtablemetadata.ALARMINTERVALON, true);
					cv.put(alarmtablemetadata.ALARMRINGTONE, ringtoneid + "," + ringtonetitle);
					cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibratetogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMSILENT, alarmsilenttogglebutton.isChecked());
					cv.put(alarmtablemetadata.ALARMREPEATS, Integer.parseInt(alarmrepeatsedittext.getText().toString()));
					cv.put(alarmtablemetadata.ALARMINFINITEREPEAT, repeatinfinitelytogglebutton.isChecked());
				}
			}
			else {
				if (alarm) {
					try {
						cv.put(alarmtablemetadata.ALARMNAME, alarmnameedittext.getText().toString());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMNAME, alarmname);
					}
					try {
						cv.put(alarmtablemetadata.ALARMON, alarmontogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMON, alarmon);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSTARTHOUR, alarmtimetimepicker.getCurrentHour());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSTARTHOUR, hour);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSTARTMINUTE, alarmtimetimepicker.getCurrentMinute());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSTARTMINUTE, minute);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSTARTSECOND, alarmsecondtimepicker.getCurrentMinute());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSTARTSECOND, second);
					}
					try {
						cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, mondaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, monday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesdaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesdaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursdaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, fridaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, friday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturdaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sundaytogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sunday);
					}
					//this is an alarm, not an interval, so the next two values will always be 0 and false
					//at least in this alarmeditor
					cv.put(alarmtablemetadata.ALARMINTERVAL, 0);
					cv.put(alarmtablemetadata.ALARMINTERVALON, false);
					cv.put(alarmtablemetadata.ALARMRINGTONE, ringtoneid + "," + ringtonetitle);
					try {
						cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibratetogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibrate);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSILENT, alarmsilenttogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSILENT, alarmsilent);
					}
					cv.put(alarmtablemetadata.ALARMREPEATS, 0);
					cv.put(alarmtablemetadata.ALARMINFINITEREPEAT, false);
				}
				else {
					try {
						cv.put(alarmtablemetadata.ALARMNAME, alarmnameedittext.getText().toString());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMNAME, alarmname);
					}
					try {
						cv.put(alarmtablemetadata.ALARMON, alarmontogglebutton.isChecked());}
					catch(Exception e) {
						cv.put(alarmtablemetadata.ALARMON, alarmon);
					}
					cv.put(alarmtablemetadata.ALARMSTARTHOUR, 0);
					cv.put(alarmtablemetadata.ALARMSTARTMINUTE, 0);
					cv.put(alarmtablemetadata.ALARMSTARTSECOND, 0);
					try {
						cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, mondaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMMONDAYREPEAT, monday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesdaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMTUESDAYREPEAT, tuesday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesdaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, wednesday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursdaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, thursday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, fridaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMFRIDAYREPEAT, friday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturdaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSATURDAYREPEAT, saturday);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sundaytogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSUNDAYREPEAT, sunday);
					}
					//this is an interval, not an interval
					try {
						cv.put(alarmtablemetadata.ALARMINTERVAL, alarmtimetimepicker.getCurrentHour() * 60 * 60
								+ alarmtimetimepicker.getCurrentMinute() * 60 + alarmsecondtimepicker.getCurrentMinute());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMINTERVAL, hour * 60 * 60 + minute * 60 + second);
					}
					cv.put(alarmtablemetadata.ALARMINTERVALON, true);
					cv.put(alarmtablemetadata.ALARMRINGTONE, ringtoneid + "," + ringtonetitle);
					try {
						cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibratetogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMVIBRATE, alarmvibrate);
					}
					try {
						cv.put(alarmtablemetadata.ALARMSILENT, alarmsilenttogglebutton.isChecked());}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMSILENT, alarmsilent);
					}
					try {
						cv.put(alarmtablemetadata.ALARMREPEATS, Integer.parseInt(alarmrepeatsedittext.getText().toString()));
					}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMREPEATS, Integer.parseInt(repeats));
					}
					try {
						cv.put(alarmtablemetadata.ALARMINFINITEREPEAT, repeatinfinitelytogglebutton.isChecked());
					}
					catch (Exception e) {
						cv.put(alarmtablemetadata.ALARMINFINITEREPEAT, infiniterepeat);
					}
				}
			}
			ContentResolver cr = getContentResolver();
			Uri uri = alarmprovidermetadata.alarmtablemetadata.CONTENTURI;
			if (!editing) {
				cr.insert(uri, cv);
				String addedalarm = getString(R.string.addedalarm);
				String at = getString(R.string.at);
				String ringtoneis = getString(R.string.ringtoneis);
				String vibrateset = getString(R.string.vibrateset);
				String silentset = getString(R.string.silentset);
				displaytoast(addedalarm +" "+ alarmnameedittext.getText().toString()
						+ at +" " + alarmtimetimepicker.getCurrentHour() + ":" + alarmtimetimepicker.getCurrentMinute() + ":" + alarmsecondtimepicker.getCurrentMinute()
						+ ringtoneis +" "+ ringtonetitle + vibrateset +" "+ alarmvibratetogglebutton.isChecked()
						+ silentset +" "+ alarmvibratetogglebutton.isChecked());

			}
			else {
				Uri updateuri = Uri.withAppendedPath(uri, Integer.toString(id));
				cr.update(updateuri, cv, null, null);
				String updatedalarm = getString(R.string.updatedalarm);
				String at = getString(R.string.at);
				String ringtoneis = getString(R.string.ringtoneis);
				String vibrateset = getString(R.string.vibrateset);
				String silentset = getString(R.string.silentset);
				try {
					displaytoast(updatedalarm +" "+ alarmnameedittext.getText().toString()
							+ at +" "+ alarmtimetimepicker.getCurrentHour() + ":" + alarmtimetimepicker.getCurrentMinute() + ":" + alarmsecondtimepicker.getCurrentMinute()
							+ ringtoneis +" "+ ringtonetitle + vibrateset +" "+ alarmvibratetogglebutton.isChecked()
							+ silentset +" "+ alarmvibratetogglebutton.isChecked());
				}
				catch (Exception e) {
					displaytoast(updatedalarm +" "+ alarmname
							+ at +" "+ hour + ":" + minute + ":" + second
							+ ringtoneis +" "+ ringtonetitle + vibrateset+" " + alarmvibrate
							+ silentset +" "+ alarmsilent);
				}
			}
			return true;
		}
		catch(Exception e) {
			String alarmerror = getString(R.string.alarmerror);
			displaytoast(alarmerror);
			return false;
		}
	}
	private void displaytoast(CharSequence text) {
		//displays a toast with the information passed to this method
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	private void onitemclick(int positionlist) {
		String idnum = "";
		String temp = "";
		try {
			idnum = "";
			temp = listitems[positionlist];
			for (int n = 0; n < temp.length(); n++) {
				if (!temp.substring(n, n+1).equals(",")) {
					idnum += temp.substring(n, n+1);
				}
				else {
					break;
				}
			}
			launchringtoneeditoractivity(positionlist, Integer.parseInt(idnum));
		}
		catch(Exception e) {
		}
	}
	class gesturedetector extends SimpleOnGestureListener { 
		// Detect a single-click and call my own handler.
		@Override 
		public boolean onSingleTapUp(MotionEvent e) {
			ListView lv = alarmringtonelistview;
			int pos = lv.pointToPosition((int)e.getX(), (int)e.getY());
			onitemclick(pos);
			return false;
		}

		@Override 
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityx, float velocityy) {
			if (e1.getX() - e2.getX() > move) {
				if (viewindex==0) {
					alarmname = alarmnameedittext.getText().toString();
				}
				else if (viewindex==1) {
					hour = alarmtimetimepicker.getCurrentHour();
					minute = alarmtimetimepicker.getCurrentMinute();
					second = alarmsecondtimepicker.getCurrentMinute();
					timeset = true;
					repeats = alarmrepeatsedittext.getText().toString();
					//timeset is to show that the timepicker object has been initialised
				}
				if (viewindex < 3) {
					right = true;
					left = false;
					try {
						InputMethodManager imm = (InputMethodManager) getSystemService(
								INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
					catch (Exception e) {}
					viewindex++;
					init();
				}
				return true;
			}
			else if (e2.getX() - e1.getX() > move) {
				if (viewindex==0) {
					alarmname = alarmnameedittext.getText().toString();
				}
				else if (viewindex==1) {
					hour = alarmtimetimepicker.getCurrentHour();
					minute = alarmtimetimepicker.getCurrentMinute();
					second = alarmsecondtimepicker.getCurrentMinute();
					timeset = true;
					repeats = alarmrepeatsedittext.getText().toString();
				}
				if (viewindex > 0) {
					right = false;
					left = true;
					try {
						InputMethodManager imm = (InputMethodManager) getSystemService(
								INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
					catch(Exception e){}
					viewindex--;
					init();
				}
				return true;
			}
			return false;
		}
	} 
}
