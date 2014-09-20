package com.beryleo.time.alarmp;
//free of localisation issues
import com.beryleo.time.R;
import com.beryleo.time.timeactivity;
import com.beryleo.time.lvadapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class alarms {
	//main code for the alarm ui and for the interval ui
	//2012-8-02: is basically just a button for adding new alarms, and a listview with alarms
	private lvadapter alarmsadapter;
	protected timeactivity context;
	private boolean alarm;
	private Uri uri;
	private Activity a;
	private Cursor c;
	//add a constructor with the Context of your activity
	public alarms(timeactivity _context) {
		context = _context;
	}
	public void alarmsmain(boolean a) {
		//this runs on data obtained from context passed to it
		//[this is from the main activity thread]
		//runs on a separate thread from the main stuff
		alarm = a;
		context.runOnUiThread(new Thread() {
			@Override
			public void run() {
				updatealarmlist();
				if (alarm) {
					context.addalarmbutton.setText(R.string.addalarm);
				}
				else {
					context.addalarmbutton.setText(R.string.addinterval);
				}
				context.addalarmbutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent launch = new Intent(context, alarmeditoractivity.class);
						launch.putExtra("alarm", alarm);
						context.startActivity(launch);
					}
				});
				//@SuppressWarnings("deprecation")
				final GestureDetector gestureDetector = new GestureDetector(new gesturedetector());
				View.OnTouchListener gestureListener = new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						//more problems with optimus v on ics
						try {
							return gestureDetector.onTouchEvent(event); 
						}
						catch (Exception e) {
							return false;
						}
					}};
					context.alarmslistview.setOnTouchListener(gestureListener);
			}
		});
	}
	//@SuppressWarnings("deprecation")
	private void onitemclick(int position) {
		ViewGroup viewgroup = (ViewGroup) context.alarmslistview.getChildAt(position);
		View view = viewgroup.getChildAt(1);
		String temp = ((TextView) view).getText().toString();
		String idnum = "";
		for (int n = 0; n < temp.length(); n++) {
			if (!temp.substring(n, n+1).equals(",")) {
				idnum += temp.substring(n, n+ 1);
			}
			else {
				break;
			}
		}
		updatealarmlist();
		uri = alarmprovidermetadata.alarmtablemetadata.CONTENTURI;
		Uri updateuri = Uri.withAppendedPath(uri, idnum);
		a = (Activity)context;
		c = a.managedQuery(updateuri, null, null, null, null);
		int hourindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTHOUR);
		int minuteindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTMINUTE);
		int onindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMON);
		int vibrateindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMVIBRATE);
		int mondayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMMONDAYREPEAT);
		int tuesdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMTUESDAYREPEAT);
		int wednesdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMWEDNESDAYREPEAT);
		int thursdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMTHURSDAYREPEAT);
		int fridayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMFRIDAYREPEAT);
		int saturdayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSATURDAYREPEAT);
		int sundayindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSUNDAYREPEAT);
		int nameindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMNAME);
		int ringtoneindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMRINGTONE);
		int silentindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSILENT);
		int intervalindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINTERVAL);
		//no need to remember when the alarm was activated, if nothing change, nothing happens, otherwise is automatically set
		int repeatsindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMREPEATS);
		int infiniterepeatindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINFINITEREPEAT);
		int secondindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMSTARTSECOND);
		c.moveToFirst();
		Intent launch = new Intent(context, alarmeditoractivity.class);
		launch.putExtra("alarm", alarm);
		launch.putExtra("editing", true);
		launch.putExtra("alarmname", c.getString(nameindex));
		launch.putExtra("hour", c.getInt(hourindex));
		launch.putExtra("minute", c.getInt(minuteindex));
		launch.putExtra("alarmon", i2b(c.getInt(onindex)));
		launch.putExtra("alarmvibrate", i2b(c.getInt(vibrateindex)));
		launch.putExtra("monday", i2b(c.getInt(mondayindex)));
		launch.putExtra("tuesday", i2b(c.getInt(tuesdayindex)));
		launch.putExtra("wednesday", i2b(c.getInt(wednesdayindex)));
		launch.putExtra("thursday", i2b(c.getInt(thursdayindex)));
		launch.putExtra("friday", i2b(c.getInt(fridayindex)));
		launch.putExtra("saturday", i2b(c.getInt(saturdayindex)));
		launch.putExtra("sunday", i2b(c.getInt(sundayindex)));
		launch.putExtra("alarmsilent", i2b(c.getInt(silentindex)));
		launch.putExtra("id", Integer.parseInt(idnum));
		launch.putExtra("interval", c.getInt(intervalindex));
		launch.putExtra("repeats", c.getInt(repeatsindex));
		launch.putExtra("infiniterepeat", i2b(c.getInt(infiniterepeatindex)));
		launch.putExtra("second", c.getInt(secondindex));
		String ringtonedata = c.getString(ringtoneindex);
		if(!i2b(c.getInt(silentindex))) {
			int wherecomma = 0;
			String id = "";
			String title = "";
			for (int n = 0; n < ringtonedata.length(); n++) {
				if (!ringtonedata.substring(n, n+1).equals(",")&&wherecomma==0) {
					id += ringtonedata.substring(n, n+ 1);
				}
				else if (ringtonedata.substring(n, n+1).equals(",")) {
					wherecomma = n;
				}
				else if (n!=wherecomma) {
					title += ringtonedata.substring(n, n + 1);
				}
			}
			launch.putExtra("ringtonetitle", title);
			launch.putExtra("ringtoneid", Integer.parseInt(id));
		}
		else  {
			launch.putExtra("ringtonetitle", "");
			launch.putExtra("ringtoneid", 0);
		}
		context.startActivity(launch);
	}
	private boolean i2b(int i) {
		//int to boolean
		if (i==0) {
			return false;
		}
		return true;
	}
	//@SuppressWarnings("deprecation")
	private void updatealarmlist() {
		uri = alarmprovidermetadata.alarmtablemetadata.CONTENTURI;
		a = (Activity)context;
		c = a.managedQuery(uri, null, null, null, null);
		int idindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata._ID);
		int nameindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMNAME);
		int onindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMON);
		int intervalonindex = c.getColumnIndex(alarmprovidermetadata.alarmtablemetadata.ALARMINTERVALON);
		int n = 0;
		boolean[] indexintervals = new boolean[c.getCount()];
		int numintervals = 0;
		boolean[] indexalarms = new boolean[c.getCount()];
		int numalarms = 0;
		for (c.moveToFirst();!c.isAfterLast();c.moveToNext()) {
			if (c.getString(intervalonindex).equals("0")) {
				indexalarms[n] = true;
				numalarms++;
			}
			else if (c.getString(intervalonindex).equals("1")) {
				indexintervals[n] = true;
				numintervals++;
			}
			n++;
		}
		String[] listitemsalarms = new String[numalarms];
		boolean[] onalarms = new boolean[numalarms];
		String[] listitemsintervals = new String[numintervals];
		boolean[] onintervals = new boolean[numintervals];
		int listitemsalarmsindex = 0;
		int listitemsintervalsindex = 0;
		c.moveToFirst();
		String noname = context.getString(R.string.noname);
		for (int nn = 0; nn < n; nn++) {
			if (indexalarms[nn]) {
				if (!c.getString(nameindex).equals("")&&!c.getString(nameindex).equals(null)) {
					listitemsalarms[listitemsalarmsindex] = c.getString(idindex) + ", "+ c.getString(nameindex);
				}
				else {
					//just so that it says there is no name
					listitemsalarms[listitemsalarmsindex] = c.getString(idindex) + ", "+ noname;
				}
				onalarms[listitemsalarmsindex] = i2b(c.getInt(onindex));
				listitemsalarmsindex++;
			}
			else if (indexintervals[nn]) {
				if (!c.getString(nameindex).equals("")&&!c.getString(nameindex).equals(null)) {
					listitemsintervals[listitemsintervalsindex] = c.getString(idindex) + ", "+ c.getString(nameindex);
				}
				else {
					listitemsintervals[listitemsintervalsindex] = c.getString(idindex) + ", "+ noname;
				}
				onintervals[listitemsintervalsindex] = i2b(c.getInt(onindex));
				listitemsintervalsindex++;
			}
			c.moveToNext();
		}
		if (alarm) {
			alarmsadapter = 
					new lvadapter(context, listitemsalarms, context.clockfont, context.getResources().getDrawable(R.drawable.z), onalarms, context.largefont);
			context.alarmcounttextview.setText("#: " + numalarms);
		}
		else {
			alarmsadapter = new lvadapter(context, listitemsintervals, context.clockfont, context.getResources().getDrawable(R.drawable.z), onintervals, context.largefont);
			context.alarmcounttextview.setText("#: " + numintervals);
		}
		context.alarmslistview.setAdapter(alarmsadapter);
	}
	class gesturedetector extends SimpleOnGestureListener { 
		// Detect a single-click and call my own handler.
		@Override 
		public boolean onSingleTapUp(MotionEvent e) {
			ListView lv = context.alarmslistview;
			int pos = lv.pointToPosition((int)e.getX(), (int)e.getY());
			onitemclick(pos);
			return false;
		}
		@Override 
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityx, float velocityy) {
			if (e1.getX() - e2.getX() > timeactivity.move) {
				if (context.viewindex < 6) {
					context.right = true;
					context.left = false;
					context.viewindex++;
					context.init();
				}
				return true;
			}
			else if (e2.getX() - e1.getX() > timeactivity.move) {
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
