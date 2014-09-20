package com.beryleo.time.alarmp;
//never will have localisation issues, never seen by end user
import java.util.HashMap;

import com.beryleo.time.alarmp.alarmprovidermetadata.alarmtablemetadata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
//not exactly sure if this will be able to handle 
//seconds for intervals. It should be able. 
public class alarmprovider extends ContentProvider{
	//setup projection map
	//to rename columns
	private static HashMap<String,String> alarmsprojectionmap;
	static {
		alarmsprojectionmap = new HashMap<String,String>();
		alarmsprojectionmap.put(alarmtablemetadata._ID, alarmtablemetadata._ID);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMNAME, alarmtablemetadata.ALARMNAME);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMON, alarmtablemetadata.ALARMON);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSTARTHOUR, alarmtablemetadata.ALARMSTARTHOUR);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSTARTMINUTE, alarmtablemetadata.ALARMSTARTMINUTE);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMMONDAYREPEAT, alarmtablemetadata.ALARMMONDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMTUESDAYREPEAT, alarmtablemetadata.ALARMTUESDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, alarmtablemetadata.ALARMWEDNESDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, alarmtablemetadata.ALARMTHURSDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMFRIDAYREPEAT, alarmtablemetadata.ALARMFRIDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSATURDAYREPEAT, alarmtablemetadata.ALARMSATURDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSUNDAYREPEAT, alarmtablemetadata.ALARMSUNDAYREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMINTERVAL, alarmtablemetadata.ALARMINTERVAL);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMINTERVALON, alarmtablemetadata.ALARMINTERVALON);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMRINGTONE, alarmtablemetadata.ALARMRINGTONE);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMRINGTONECOLUMN, alarmtablemetadata.ALARMRINGTONECOLUMN);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMVIBRATE, alarmtablemetadata.ALARMVIBRATE);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSILENT, alarmtablemetadata.ALARMSILENT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMACTIVATIONTIME, alarmtablemetadata.ALARMACTIVATIONTIME);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMREPEATS, alarmtablemetadata.ALARMREPEATS);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMINFINITEREPEAT, alarmtablemetadata.ALARMINFINITEREPEAT);
		alarmsprojectionmap.put(alarmtablemetadata.ALARMSTARTSECOND, alarmtablemetadata.ALARMSTARTSECOND);
	}
	//provide a mechanism to identify incoming uris
	private static final UriMatcher urimatcher;
	private static final int INCOMINGALARMCOLLECTIONURIINDICATOR = 1;
	private static final int INCOMINGSINGLEALARMURIINDICATOR = 2;
	static {
		urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
		urimatcher.addURI(alarmprovidermetadata.AUTHORITY, "alarms", INCOMINGALARMCOLLECTIONURIINDICATOR);
		urimatcher.addURI(alarmprovidermetadata.AUTHORITY, "alarms/#", INCOMINGSINGLEALARMURIINDICATOR);
	}
	//setup/create database
	//this will help to open, create, and upgrade database file
	private static class databasehelper extends SQLiteOpenHelper {
		databasehelper(Context context) {
			super(context, alarmprovidermetadata.DATABASENAME, null, alarmprovidermetadata.DATABASEVERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + alarmtablemetadata.TABLENAME + " ("
					+ alarmtablemetadata._ID + " INTEGER PRIMARY KEY,"
					+ alarmtablemetadata.ALARMNAME + " TEXT,"
					+ alarmtablemetadata.ALARMON + " BOOLEAN,"
					+ alarmtablemetadata.ALARMSTARTHOUR + " INTEGER,"
					+ alarmtablemetadata.ALARMSTARTMINUTE + " INTEGER,"
					+ alarmtablemetadata.ALARMMONDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMTUESDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMWEDNESDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMTHURSDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMFRIDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMSATURDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMSUNDAYREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMINTERVAL+ " LONG,"
					+ alarmtablemetadata.ALARMINTERVALON + " BOOLEAN,"
					+ alarmtablemetadata.ALARMRINGTONE + " TEXT,"
					+ alarmtablemetadata.ALARMRINGTONECOLUMN + " INTEGER,"
					+ alarmtablemetadata.ALARMVIBRATE + " BOOLEAN,"
					+ alarmtablemetadata.ALARMSILENT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMACTIVATIONTIME + " LONG,"
					+ alarmtablemetadata.ALARMREPEATS + " INTEGER,"
					+ alarmtablemetadata.ALARMINFINITEREPEAT + " BOOLEAN,"
					+ alarmtablemetadata.ALARMSTARTSECOND + " INTEGER"
					+ ")");
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + alarmtablemetadata.TABLENAME);
			onCreate(db);
		}
	}
	private databasehelper openhelper;
	//component creation callback
	@Override
	public boolean onCreate() {
		openhelper = new databasehelper(getContext());
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (urimatcher.match(uri)) {
		case INCOMINGALARMCOLLECTIONURIINDICATOR:
			qb.setTables(alarmtablemetadata.TABLENAME);
			qb.setProjectionMap(alarmsprojectionmap);
			break;
		case INCOMINGSINGLEALARMURIINDICATOR:
			qb.setTables(alarmtablemetadata.TABLENAME);
			qb.setProjectionMap(alarmsprojectionmap);
			qb.appendWhere(alarmtablemetadata._ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		//if no sort order is specified, use default
		String orderby;
		if (TextUtils.isEmpty(sortOrder)) {
			orderby = alarmtablemetadata.DEFAULTSORTORDER;
		}
		else {
			orderby = sortOrder;
		}
		//get the database and run the query
		SQLiteDatabase db = openhelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderby);
		//tell the cursor what uri to watch so it knows when the data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	@Override
	public String getType(Uri uri) {
		switch (urimatcher.match(uri)) {
		case INCOMINGALARMCOLLECTIONURIINDICATOR:
			return alarmtablemetadata.CONTENTTYPE;
		case INCOMINGSINGLEALARMURIINDICATOR:
			return alarmtablemetadata.CONTENTITEMTYPE;
		default:
			throw new IllegalArgumentException("unknown uri " + uri);
		}
	}
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		//validate the requested uri
		if (urimatcher.match(uri)!=INCOMINGALARMCOLLECTIONURIINDICATOR) {
			throw new IllegalArgumentException("unknown uri " + uri);
		}
		ContentValues v;
		if (initialValues!=null) {
			v = new ContentValues(initialValues);
		}
		else {
			v = new ContentValues();
		}
		if (!v.containsKey(alarmtablemetadata.ALARMNAME)) {
			throw new SQLException("failed to insert a new row because alarm name is needed" + uri);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMON)) {
			v.put(alarmtablemetadata.ALARMON, true);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSTARTHOUR)) {
			v.put(alarmtablemetadata.ALARMSTARTHOUR, 12);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSTARTMINUTE)) {
			v.put(alarmtablemetadata.ALARMSTARTMINUTE, 10);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMMONDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMMONDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMTUESDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMTUESDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMWEDNESDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMWEDNESDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMTHURSDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMTHURSDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMFRIDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMFRIDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSATURDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMSATURDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSUNDAYREPEAT)) {
			v.put(alarmtablemetadata.ALARMSUNDAYREPEAT, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMINTERVAL)) {
			v.put(alarmtablemetadata.ALARMINTERVAL, 0);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMINTERVALON)) {
			v.put(alarmtablemetadata.ALARMINTERVALON, false);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMRINGTONE)) {
			v.put(alarmtablemetadata.ALARMRINGTONE, "silent");
		}
		if (!v.containsKey(alarmtablemetadata.ALARMRINGTONECOLUMN)) {
			v.put(alarmtablemetadata.ALARMRINGTONECOLUMN, 0);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMVIBRATE)) {
			v.put(alarmtablemetadata.ALARMVIBRATE, true);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSILENT)) {
			v.put(alarmtablemetadata.ALARMSILENT, true);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMACTIVATIONTIME)) {
			v.put(alarmtablemetadata.ALARMACTIVATIONTIME, 0);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMREPEATS)) {
			v.put(alarmtablemetadata.ALARMREPEATS, 0);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMINFINITEREPEAT)) {
			v.put(alarmtablemetadata.ALARMINFINITEREPEAT, true);
		}
		if (!v.containsKey(alarmtablemetadata.ALARMSTARTSECOND)) {
			v.put(alarmtablemetadata.ALARMSTARTSECOND, 0);
		}
		SQLiteDatabase db = openhelper.getWritableDatabase();
		long rowid = db.insert(alarmtablemetadata.TABLENAME, alarmtablemetadata.TABLENAME, v);
		if (rowid > 0) {
			Uri insertedbookuri = ContentUris.withAppendedId(alarmtablemetadata.CONTENTURI, rowid);
			getContext().getContentResolver().notifyChange(insertedbookuri, null);
			return insertedbookuri;
		}
		throw new SQLException("failed to insert row into " + uri);
	}
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = openhelper.getWritableDatabase();
		int count;
		switch (urimatcher.match(uri)) {
		case INCOMINGALARMCOLLECTIONURIINDICATOR:
			count = db.delete(alarmtablemetadata.TABLENAME, where, whereArgs);
			break;
		case INCOMINGSINGLEALARMURIINDICATOR:
			String rowid = uri.getPathSegments().get(1);
			count = db.delete(alarmtablemetadata.TABLENAME, 
					alarmtablemetadata._ID + "=" + rowid + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("unknown uri " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = openhelper.getWritableDatabase();
		int count;
		switch (urimatcher.match(uri)) {
		case INCOMINGALARMCOLLECTIONURIINDICATOR:
			count = db.update(alarmtablemetadata.ALARMNAME, values, where, whereArgs);
			break;
		case INCOMINGSINGLEALARMURIINDICATOR:
			String rowid = uri.getPathSegments().get(1);
			count = db.update(alarmtablemetadata.TABLENAME, values,
					alarmtablemetadata._ID + "=" + rowid + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'  : ""), 
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("unknown uri " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
