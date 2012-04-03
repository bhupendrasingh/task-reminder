package com.android.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ViewTask extends Activity {
TextView taskname;
TextView taskbody;
TextView taskdate;
private Long rowid;
private ReminderDB mdbhelper;
static final String Date_Time_Format="yyyy-MM-dd kk:mm:ss";
Calendar c=Calendar.getInstance();
@Override
public void onCreate(Bundle savedInstanceState){
super.onCreate(savedInstanceState);
setContentView(R.layout.view_task);
taskname=(TextView)findViewById(R.id.task_name);
taskbody=(TextView)findViewById(R.id.task_body);
taskdate=(TextView)findViewById(R.id.task_date);
rowid=savedInstanceState!=null?savedInstanceState.getLong(ReminderDB.KEY_ROWID):null;
mdbhelper=new ReminderDB(this);
mdbhelper.open();
filldata();
}
private void filldata()
{
		if(rowid!=null)
		{
			Cursor reminder=mdbhelper.fetchReminder(rowid);
			startManagingCursor(reminder);
			taskname.setText(reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_TITLE)));
			taskbody.setText(reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_BODY)));
			SimpleDateFormat datetimeformat=new SimpleDateFormat(Date_Time_Format); 
			Date date=null;
			try{
				String datestring=reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_DATE_TIME));
				date=datetimeformat.parse(datestring);
				c.setTime(date);
				taskdate.setText(datestring);
		}catch(ParseException e){
			Log.e("ViewTask",e.getMessage(),e);
			}
	}
}
@Override
protected void onPause()
{
	super.onPause();
	mdbhelper.close();
}

protected void onResume()
{
	super.onResume();
	mdbhelper.open();
	setRowIdFromIntent();
	filldata();
}


private void setRowIdFromIntent()
{
	if(rowid==null)
	{
		Bundle extras=getIntent().getExtras();
		rowid=extras!=null?extras.getLong(ReminderDB.KEY_ROWID):null;
    }
}
}
