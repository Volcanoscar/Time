package com.beryleo.time.alarmp;
//never seen by end user
import android.net.Uri;
import android.provider.BaseColumns;
//holds information for how to access the data in each of the columns of my custom SQL database 

public class alarmprovidermetadata {
	public static final String AUTHORITY = "com.beryleo.time.alarmp.alarmprovider";
	public static final String DATABASENAME = "alarm.db";
	public static final int DATABASEVERSION = 1;
	public static final String ALARMSTABLENAME = "alarms";
	private alarmprovidermetadata() {}
	public static final class alarmtablemetadata implements BaseColumns {
		private alarmtablemetadata() {};
		public static final String TABLENAME = "alarms";
		//uri and mime type definitions
		public static final Uri CONTENTURI = Uri.parse("content://" + AUTHORITY + "/alarms");
		public static final String CONTENTTYPE = "vnd.android.cursor.dir/vnd.beryleotime.alarm";
		public static final String CONTENTITEMTYPE = "vnd.android.cursor.item/vnd.beryleotime.alarm";
		//sorts by name, ascending
		public static final String DEFAULTSORTORDER = "name ASC";
		//additional columns start here
		//string type
		public static final String ALARMNAME = "name";
		//boolean type
		public static final String ALARMON = "onoff";
		//int type
		public static final String ALARMSTARTHOUR = "starthour";
		//int type
		public static final String ALARMSTARTMINUTE = "startminute";
		//boolean type
		public static final String ALARMMONDAYREPEAT = "mondayrepeat";
		//boolean type
		public static final String ALARMTUESDAYREPEAT = "tuesdayrepeat";
		//boolean type
		public static final String ALARMWEDNESDAYREPEAT = "wednesdayrepeat";
		//boolean type
		public static final String ALARMTHURSDAYREPEAT = "thursdayrepeat";
		//boolean type
		public static final String ALARMFRIDAYREPEAT = "fridayrepeat";
		//boolean type
		public static final String ALARMSATURDAYREPEAT = "saturdayrepeat";
		//boolean type
		public static final String ALARMSUNDAYREPEAT = "sundayrepeat";
		//long type (how often to repeat in milliseconds)
		public static final String ALARMINTERVAL = "interval";
		//boolean type
		public static final String ALARMINTERVALON = "intervalon";
		//string type
		public static final String ALARMRINGTONE = "ringtone";
		//int type
		public static final String ALARMRINGTONECOLUMN = "ringtonecolumn";
		//boolean type
		public static final String ALARMVIBRATE = "vibrate";
		//boolean type
		public static final String ALARMSILENT = "silent";
		//next three values only are applicable to interval alarms
		//int type
		public static final String ALARMACTIVATIONTIME = "activationtime";
		//long type
		public static final String ALARMREPEATS = "alarmrepeats";
		//boolean type
		public static final String ALARMINFINITEREPEAT = "infiniterepeat";
		//int type
		public static final String ALARMSTARTSECOND = "startsecond";

	}
}
