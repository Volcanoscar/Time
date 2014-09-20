package com.beryleo.time.stopwatchp;

//good
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class lapvalue implements Parcelable {
	// this contains the data of each individual lap value from the stopwatch
	// BE CAREFUL WHEN PULLING DATA FROM THIS CLASS
	// -not all of the variables will be initialised
	// -i cannot store floats in the shared preferences object of this
	// application
	// so only the int for places, hours, minutes
	// and the double for seconds are guaranteed to be available for use
	// implements parcelable so that it can be passed on screen rotate
	// bundles only accept parcelable objects or primitives
	// also only holds the seconds to the precision that the user selected at
	// the time of the lap
	// e.g. if user pressed lap when precision was at 0, and then raised
	// precision, the decimal would still have null decimal places
	// probably could be fixed by changing the places primitive to match what
	// the user has currently selected
	// TODO
	lapvalue() {
	}

	int places;
	DecimalFormat placeformat;
	private int hours, minutes;
	private double seconds;
	private float end, start, difference;

	public void setdecimalformat(int p) {
		places = p;
	}

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

	public String getseconds() {
		try {
			return "" + Double.parseDouble(formatdecimal(seconds));
			// will fail if the user has a language that has the separator
			// between whole numbers and decimals as anything
			// that is not a period, e.g. Swedish
		} catch (Exception e) {
			try {
				// this catches it for other languages
				return ""
						+ Double.parseDouble(formatcomma(formatdecimal(seconds)));
			} catch (Exception ee) {
				// and since I'm not sure how other languages format numbers,
				// this is a failsafe
				return "" + getsecondsfallback();
			}
		}
	}

	public double getsecondsfallback() {
		return seconds;
	}

	public void setseconds(double s) {
		seconds = s;

	}

	public float getend() {
		return end;
	}

	public void setend(float e) {
		end = e;
	}

	public float getstart() {
		return start;
	}

	public void setstart(float st) {
		start = st;
	}

	public float getdifference() {
		return difference;
	}

	public void setdifference(float d) {
		difference = d;
	}

	private String formatdecimal(double number) {
		String formatfordecimal = "#.";
		for (int n = 0; n < places; n++) {
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
		for (int n = places; n < places; n++) {
			if (places == 0 && n == places) {
				tobereturned += ".";
			}
			tobereturned += "0";
		}
		if (places == 0) {
			return tobereturned.substring(0, tobereturned.length() - 1);
		}
		return tobereturned;
	}

	private String formatcomma(String tobeformatted) {
		// formats out commas and replaces them with periods
		// this allows the string to be parsed to a decimal
		// formatting to a certain length requires this, if the users
		// language/region uses commas as separators
		String tobereturned = "";
		for (int n = 0; n < tobeformatted.length(); n++) {
			if (tobeformatted.substring(n, n + 1).equals(",")) {
				tobereturned += ".";
			} else {
				tobereturned += tobeformatted.substring(n, n + 1);
			}
		}
		return tobereturned;
	}

	// next two methods required for parcelable objects
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
	}

}
