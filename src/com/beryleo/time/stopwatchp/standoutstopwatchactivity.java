package com.beryleo.time.stopwatchp;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;
//launches the standout window of stopwatch
public class standoutstopwatchactivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StandOutWindow.closeAll(this, standoutstopwatch.class);
        StandOutWindow.show(this, standoutstopwatch.class, StandOutWindow.DEFAULT_ID);
        finish();
    }
}