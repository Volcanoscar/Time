package com.beryleo.time.alarmp;
//this does not have any ui action
//instead it schedules the next alarm, and the intent to be called then. 
//the alarm scheduled then runs this to reschedule the next alarm
//so this is called when anything happens:
//	regarding deleting/adding an alarm
//	when an alarm goes off
//	when the device is first powered on

//LOGIC PLAN
//THIS (ALARM SCHEDULER)
//will remove any scheduled alarms/stuff at the start of the onreceive
//will then assemble a list of all the times of the alarms from the SQL database
//will then parse through the list, and find the index of the alarm that is closest to being after the present time
//this id will be used to identify what information to be displayed when the alarm actually goes off
//store id in some way(probably in sharedpreferences)
//schedule the broadcast receiver for the alarm notification
//display a notification so that the user knows this action has occurred
//NOTIFICATION
//will retrieve id of alarm(probably from sharedpreferences)
//then will pull a list of values from SQL columns
//then will display these values in some way
//then will call the alarm scheduler to schedule the next alarm
//BOOT
//will call the alarm scheduler to schedule the next alarm
//display a notification so that the user knows this action has occurred
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class schedulealarmservice extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {	
		context.stopService(new Intent(context, 
                schedulerservice.class));
	    context.startService(new Intent(context, 
                schedulerservice.class));
    }
} 
