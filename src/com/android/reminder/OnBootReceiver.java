package com.android.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver  {
	@Override
	public void onReceive(Context context, Intent intent){
		ReminderManager remindermgr=new ReminderManager(context);
		ReminderDB dbhelper=new ReminderDB(context);
		dbhelper.open();
		Cursor cursor=dbhelper.fetchallreminders();
		if(cursor!=null){
			cursor.moveToFirst();
			int rowidColumnIndex=cursor.getColumnIndex(ReminderDB.KEY_ROWID);
			int dateTimeColumnIndex=cursor.getColumnIndex(ReminderDB.KEY_DATE_TIME);
			while(cursor.isAfterLast()==false){
			Long rowid=cursor.getLong(rowidColumnIndex);
			String datetime=cursor.getString(dateTimeColumnIndex);	
			Calendar cal=Calendar.getInstance();
			SimpleDateFormat format=new SimpleDateFormat(ReminderEditActivity.Date_Time_Format);
			try{
				Date date=format.parse(datetime);
				cal.setTime(date);
				remindermgr.setReminder(rowid,cal);
		}catch (ParseException e){
			Log.e("OnBootReceiver",e.getMessage(),e);
		}
		cursor.moveToNext();
	}
	cursor.close();
}
dbhelper.close();
	}

}
