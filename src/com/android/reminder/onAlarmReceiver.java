package com.android.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class onAlarmReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context,Intent intent){
	long rowid=intent.getExtras().getLong(ReminderDB.KEY_ROWID);
	WakeReminderIntentService.acquireStaticLock(context);
    Intent i=new Intent(context,ReminderService.class);
    i.putExtra(ReminderDB.KEY_ROWID, rowid);
    context.startService(i);
}
}
