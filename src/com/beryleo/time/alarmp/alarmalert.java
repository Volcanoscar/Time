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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.beryleo.time.R;

/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm tone
 */
public class alarmalert extends Activity {
	private static final long[] vibratepattern = new long[] { 500, 500 };
	private Vibrator vibrator;
	private static final int UNKNOWN = 0;
	private static final int DISMISS = 2;
	private int mState = UNKNOWN;
	private String title = "", ringtoneraw = "";
	private boolean vibrate, silent;
	private TextView alarmnametextview;
	private TextView alarmalarmtextview;
	private Button alarmdismissbutton;
	public static final String PREFS_NAME = "prefs";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	private RingtoneManager ringtonemanager;
	private Ringtone ringtone;
	private MediaPlayer mediaplayer;
	Uri uri;

	@Override
	protected void onCreate(Bundle icicle) {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		super.onCreate(icicle);
		// Maintain a lock during the playback of the alarm. This lock may have
		// already been acquired in AlarmReceiver. If the process was killed,
		// the global wake lock is gone. Acquire again just to be sure.
		alarmalertwakelock.acquire(this);
		// Popup alert over black screen
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		lp.token = null;
		getWindow().setAttributes(lp);
		title = settings.getString("alarmtemptitle", "FAILURE");
		ringtoneraw = settings.getString("alarmtempringtone", "FAILURE");
		vibrate = settings.getBoolean("alarmtempvibrate", false);
		silent = settings.getBoolean("alarmtempsilent", false);
		setTitle(title);
		if (vibrate) {
			vibrator.vibrate(vibratepattern, 0);
		}
		mediaplayer = new MediaPlayer();
		if (!silent) {
			int wherecomma = 0;
			String id = "";
			String titlematcher = "";
			for (int n = 0; n < ringtoneraw.length(); n++) {
				if (!ringtoneraw.substring(n, n + 1).equals(",")
						&& wherecomma == 0) {
					id += ringtoneraw.substring(n, n + 1);
				} else if (ringtoneraw.substring(n, n + 1).equals(",")) {
					wherecomma = n;
				} else if (n != wherecomma) {
					titlematcher += ringtoneraw.substring(n, n + 1);
				}
			}
			ringtonemanager = new RingtoneManager(this);
			Cursor c = ringtonemanager.getCursor();
			int titleindex = 1;
			String[] listitems = new String[c.getCount()];
			int n = 0;
			int column = 0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				listitems[n] = c.getString(titleindex);
				if (listitems[n].equals(titlematcher)) {
					column = n + 1;
				}
				n++;
			}
			ringtone = ringtonemanager.getRingtone(column);
			Uri uri = ringtonemanager.getRingtoneUri(column);
			// this fixes the issue of not loading ringtones properly
			if (uri == null || ringtone == null) {
				uri = Uri.parse("content://media/internal/audio/media/" + id);
				ringtone = RingtoneManager.getRingtone(getApplicationContext(),
						uri);
			}
			try {
				mediaplayer.setDataSource(this, uri);
			} catch (Exception e) {
				Log.i("ringtone editor",
						"error setting media player data source " + "\n"
								+ e.toString());
			}
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaplayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaplayer.setLooping(true);
				try {
					mediaplayer.prepare();
				} catch (Exception e) {
				}
				mediaplayer.start();
			}
		}
		updateLayout();
	}

	private void updateLayout() {
		Typeface clockfont = Typeface.createFromAsset(getAssets(),
				"Sony_Sketch_EF.ttf");
		setContentView(R.layout.alarmalarm);
		alarmdismissbutton = (Button) findViewById(R.id.alarmdismissbutton);
		alarmnametextview = (TextView) findViewById(R.id.alarmalarmnametextview);
		alarmalarmtextview = (TextView) findViewById(R.id.alarmalarmtextview);
		alarmdismissbutton.setTypeface(clockfont);
		alarmnametextview.setTypeface(clockfont);
		alarmalarmtextview.setTypeface(clockfont);
		alarmnametextview.setText(title);
		alarmdismissbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dismiss();
			}
		});
	}

	// Dismiss the alarm.
	private void dismiss() {
		if (mState != UNKNOWN) {
			return;
		}
		mState = DISMISS;
		mediaplayer.stop();
		vibrator.cancel();
		releaseLocks();
		this.finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		releaseLocks();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		updateLayout();
	}

	/**
	 * release wake and keyguard locks
	 */
	private synchronized void releaseLocks() {
		alarmalertwakelock.release();
	}
}
