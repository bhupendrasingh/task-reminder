package com.android.reminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class WakeReminderIntentService extends IntentService {
	abstract void doReminderWork(Intent intent);
	public static final String Lock_Name_Static="com.android.reminder.static";
	private static PowerManager.WakeLock lockStatic=null;
	public static void acquireStaticLock(Context context){
		getLock(context).acquire();
	}
	synchronized private static PowerManager.WakeLock getLock(Context context){
		if(lockStatic==null){
			PowerManager pmgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=pmgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Lock_Name_Static);
			lockStatic.setReferenceCounted(true);
			}
	return (lockStatic);
	}
public WakeReminderIntentService(String name){
	super(name);
	
}
@Override
final protected void onHandleIntent(Intent intent){
	try{
		doReminderWork(intent);
	}finally {
		getLock(this).release();
		}

}
}
