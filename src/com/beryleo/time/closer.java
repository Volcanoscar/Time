package com.beryleo.time;
import com.beryleo.time.R;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class closer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    //this removes the notification displayed 
    //and then exits
    //this is the activity that is put in the intent 
    //for when the notification is called 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager)getSystemService(ns);
		//my unique id is 606060. Hope nobody else is using it
		nm.cancel(606060);
        this.finish();
    }
}
