
package com.android.reminder;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReminderManager {
private Context mcontext;
private AlarmManager mAlarmManager;
public ReminderManager(Context context){
	mcontext=context;
	mAlarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
}

public void setReminder(Long taskid,Calendar when){
	Intent i =new Intent(mcontext,onAlarmReceiver.class);
	i.putExtra(ReminderDB.KEY_ROWID,(long)taskid);
PendingIntent pi=PendingIntent.getBroadcast(mcontext, 0,i,PendingIntent.FLAG_ONE_SHOT);
mAlarmManager.set(AlarmManager.RTC_WAKEUP,when.getTimeInMillis(),pi);
}
}
