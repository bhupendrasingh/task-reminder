package com.android.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

public class ReminderService extends WakeReminderIntentService {
public ReminderService()
{
	super("RemindeService");
}
@Override
void doReminderWork(Intent intent){
	Long rowId=intent.getExtras().getLong(ReminderDB.KEY_ROWID);
	NotificationManager nmgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	Intent nintent=new Intent(this,ViewTask.class);
	nintent.putExtra(ReminderDB.KEY_ROWID,rowId);
	PendingIntent pi=PendingIntent.getActivity(this,0, nintent, PendingIntent.FLAG_ONE_SHOT);
	Notification note= new Notification(android.R.drawable.stat_sys_warning,
			getString(R.string.notify_new_task_msg),System.currentTimeMillis());
	note.setLatestEventInfo(this,getString(R.string.notify_new_task_title),
			getString(R.string.notify_new_task_msg),pi);
	note.defaults|=Notification.DEFAULT_LIGHTS;
	note.flags|=Notification.FLAG_AUTO_CANCEL;
	int id=(int)((long)rowId);
	nmgr.notify(id,note);
	}
}
