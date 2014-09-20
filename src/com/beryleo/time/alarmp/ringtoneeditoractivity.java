package com.beryleo.time.alarmp;
//free of localisation issues
import com.beryleo.time.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DigitalClock;
import android.widget.ViewAnimator;
import com.beryleo.time.fonttextview;
import com.beryleo.time.fontbutton;
//@SuppressWarnings("deprecation")
public class ringtoneeditoractivity extends Activity{
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public Typeface digitalfont, clockfont;
	public int largefont = 40;
	private ViewAnimator viewanimator;
	private boolean right = true, left;
	//switcher variables
	private fontbutton previousbutton, nextbutton;
	//@SuppressWarnings("unused")
	private fonttextview previoustextview, currenttextview, nexttextview;
	private int viewindex = 0;
	private static final int move = 120;
	private GestureDetector gesture;
	//ringtone variables
	private fonttextview ringtoneidtextview, ringtonetitletextview;
	private fontbutton ringtoneplaybutton, ringtonesetbutton, cancelbutton;
	private RingtoneManager ringtonemanager;
	private Ringtone ringtone;
	private MediaPlayer mediaplayer;
	private int column, id;
	private String title;
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
					if (viewindex < 0) {
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
		return gesture.onTouchEvent(event);
	}

	@Override
	public void onSaveInstanceState(Bundle outstate) {
		//when rotate happens this is called to save data
		outstate.putInt("viewindex", viewindex);
		outstate.putInt("column", column);
		outstate.putInt("id", id);
		super.onSaveInstanceState(outstate);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedinstancestate) {
		//and when rotate is finished, this is called to restore that saved data
		super.onRestoreInstanceState(savedinstancestate);
		viewindex = savedinstancestate.getInt("viewindex");
		column = savedinstancestate.getInt("column");
		id = savedinstancestate.getInt("id");
		init();
	}
	@Override
	public void onPause() {
		try {
			mediaplayer.stop();
		}
		catch(Exception e) {
		}
		super.onDestroy();
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
					if (viewindex < 0) {
						right = true;
						left = false;
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
			ringtoneeditor();
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
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
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
			tmpView = inflater.inflate(R.layout.ringtoneeditor, null);
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
	private void ringtoneeditor() {
		if (settings.getBoolean("topnavigation", false)) {
			currenttextview.setText(R.string.alarmringtoneeditor);
			previousbutton.setText(R.string.none);
			nextbutton.setText(R.string.none);
		}
		ringtoneidtextview = (fonttextview) findViewById(R.id.ringtoneidtextview);
		ringtonetitletextview = (fonttextview) findViewById(R.id.ringtonetitletextview);
		ringtoneplaybutton = (fontbutton) findViewById(R.id.ringtoneplaybutton);
		ringtonesetbutton = (fontbutton) findViewById(R.id.ringtonesetbutton);
		cancelbutton = (fontbutton) findViewById(R.id.cancelbutton);
		ringtoneidtextview.setTextSize(largefont);
		ringtonetitletextview.setTextSize(largefont);
		Bundle extras = getIntent().getExtras(); 
		column = extras.getInt("column");
		id = extras.getInt("id");
		ringtonemanager = new RingtoneManager(this);
		ringtone = ringtonemanager.getRingtone(column);
		Uri uri = ringtonemanager.getRingtoneUri(column);
		//this fixes the issue of not loading ringtones properly
		if (uri==null||ringtone==null) {
			uri = Uri.parse("content://media/internal/audio/media/" + id);
			ringtone = RingtoneManager.getRingtone(getApplicationContext(),
					uri);
		}
		title = ringtone.getTitle(this);
		ringtoneidtextview.setText("" + id);
		ringtonetitletextview.setText(title);
		mediaplayer = new MediaPlayer();
		try {
			mediaplayer.setDataSource(this, uri);
		} 
		catch (Exception e) {
			Log.i("ringtone editor", "error setting media player data source "
					+ "\n"+ e.toString());
		}
		ringtoneplaybutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					mediaplayer.setAudioStreamType(AudioManager.STREAM_ALARM);
					mediaplayer.setLooping(true);
					try {
						mediaplayer.prepare();
					} 
					catch(Exception  e) {
						Log.i("ringtone editor", "error in preparing media player"
								+ "\n"+ e.toString());
					}
					mediaplayer.start();
				}
			}
		});
		ringtonesetbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mediaplayer.stop();
				Intent returnintent = new Intent();
				returnintent.putExtra("success",true);
				returnintent.putExtra("id",id);
				returnintent.putExtra("title",title);
				setResult(RESULT_OK,returnintent);     
				finish();
			}
		});
		cancelbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mediaplayer.stop();
				Intent returnintent = new Intent();
				returnintent.putExtra("success",false);
				setResult(RESULT_OK,returnintent);     
				finish();
			}
		});
	}
}
