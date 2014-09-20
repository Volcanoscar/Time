package com.beryleo.time.timerp;

import android.os.Parcel;
import android.os.Parcelable;

public class presetvalue implements Parcelable {
	// this contains the data of each individual preset value from the timer
	// implements parcelable so that it can be passed on screenrotate
	// bundles only accept parcelable objects or primitives
	presetvalue() {
	}

	private int hours, minutes, seconds;
	private String name;

	public int gethours() {
		return hours;
	}

	public void sethours(int h) {
		hours = h;
	}

	public int getminutes() {
		return minutes;
	}

	public void setminutes(int m) {
		minutes = m;
	}

	public int getseconds() {
		return seconds;
	}

	public void setseconds(int s) {
		seconds = s;
	}

	public String getname() {
		return name;
	}

	public void setname(String n) {
		name = n;
	}

	// next two methods required for parcelable objects
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
