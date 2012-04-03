package com.android.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReminderEditActivity extends Activity {
private Long mRowID;
private ReminderDB mdbhelper;
private EditText mtitle;
private EditText mbody;
private Button mconfirm;
static final String Date_Time_Format="MM-dd-yyyy kk:mm:ss";
private Button mdatebutton;
private Button mcancel;
private int mYear;
private int mMonth;
private int mDay;
private  Calendar c;
private int mHour;
private int mMinute;
private Button timebutton;
private static final int Time_Dialog_Id=1;
private static final int Date_Dialog_Id=0;

@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	mdbhelper=new ReminderDB(this);
	setContentView(R.layout.reminder_edit);
	c=Calendar.getInstance();
	mtitle=(EditText)findViewById(R.id.title);
    mbody=(EditText)findViewById(R.id.body);
	mdatebutton=(Button)findViewById(R.id.reminder_date);
	timebutton=(Button)findViewById(R.id.reminder_time);
	mconfirm=(Button)findViewById(R.id.confirm);
	mcancel=(Button)findViewById(R.id.cancel);
	mRowID=savedInstanceState!=null?savedInstanceState.getLong(ReminderDB.KEY_ROWID):null;
	timebutton.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			showDialog(Time_Dialog_Id);
		}
	});
	
	mHour=c.get(Calendar.HOUR_OF_DAY);
	mMinute=c.get(Calendar.MINUTE);
	updatetime();

	mdatebutton.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View v) {showDialog(Date_Dialog_Id);}
		});
	//getting the date
	mDay=c.get(Calendar.DAY_OF_MONTH);
	mMonth=c.get(Calendar.MONTH);
	mYear=c.get(Calendar.YEAR);
	//DISPLAYING CURRENT DATE
	updateDisplay();

	mconfirm.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			AlertDialog.Builder builder=new AlertDialog.Builder(ReminderEditActivity.this);
			builder.setMessage("Are you sure you want to save the task?").setTitle("Are you sure?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					//saving the database
					saveState();
					setResult(RESULT_OK);
					Toast.makeText(ReminderEditActivity.this,getString(R.string.task_saved_message),
					Toast.LENGTH_SHORT).show();
					finish();	
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		builder.create().show();
		}	
	});
   mcancel.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
				finish();
	}
});
}

private void setRowIdFromIntent()
{
	if(mRowID==null)
	{
		Bundle extras=getIntent().getExtras();
		mRowID=extras!=null?extras.getLong(ReminderDB.KEY_ROWID):null;
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
	populatefields();
}
//update display method
private void updateDisplay(){
mdatebutton.setText(
new StringBuilder()
.append(mMonth+1).append("-")
.append(mDay).append("-")
.append(mYear));
}
//time update display
private void updatetime()
{
timebutton.setText( new StringBuilder()
.append(pad(mHour)).append(":")
.append(pad(mMinute)));
}
private static String pad(int k)
{
if (k >= 10)      
return String.valueOf(k);    
else return "0" + String.valueOf(k);
}
//the call back for time
private TimePickerDialog showtimepicker(){
TimePickerDialog timepicker=new TimePickerDialog(ReminderEditActivity.this,new TimePickerDialog.OnTimeSetListener() {
@Override
public void onTimeSet(TimePicker view, int hourofday, int minute)
{
mHour=hourofday;
mMinute=minute;
c.set(Calendar.HOUR_OF_DAY,hourofday);
c.set(Calendar.MINUTE,minute);
updatetime();
}
},c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true);
return timepicker;
}

//the call back received when the users has set the date
private  DatePickerDialog showdatepicker()
{
DatePickerDialog datepicker=new DatePickerDialog(ReminderEditActivity.this,new DatePickerDialog.OnDateSetListener(){
@Override
public void onDateSet(DatePicker view,int year,int monthofyear,int dayofmonth)
{
mYear=year;
mMonth=monthofyear;
mDay=dayofmonth;
c.set(Calendar.YEAR,year);
c.set(Calendar.MONTH,monthofyear);
c.set(Calendar.DAY_OF_MONTH,dayofmonth);
updateDisplay();	
}
},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
	return datepicker;
}
//activity call back method that is passed the id of the button that is clicked
@Override
protected Dialog onCreateDialog(int id){
switch(id){
case Date_Dialog_Id:
return showdatepicker();
//return new DatePickerDialog(this,mDateSetListener,mYear,mMonth,mDay);
case Time_Dialog_Id:
return showtimepicker();
}
return super.onCreateDialog(id);
}		
	
protected void populatefields() 
{
	if(mRowID!=null)
	{
		Cursor reminder=mdbhelper.fetchReminder(mRowID);
		startManagingCursor(reminder);
		mtitle.setText(reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_TITLE)));
		mbody.setText(reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_BODY)));
		SimpleDateFormat datetimeformat=new SimpleDateFormat(Date_Time_Format); 
		Date date=null;
		try{
			String datestring=reminder.getString(reminder.getColumnIndexOrThrow(ReminderDB.KEY_DATE_TIME));
			date=datetimeformat.parse(datestring);
			c.setTime(date);
	}catch(ParseException e){
		Log.e("ReminderEditActivity",e.getMessage(),e);
		}
}
	updateDisplay();
	updatetime();
}
@Override
protected void onSaveInstanceState(Bundle outState)
{
super.onSaveInstanceState(outState);
outState.putLong(ReminderDB.KEY_ROWID, mRowID);
}
private void saveState()
{
	String title=mtitle.getText().toString();
	String body=mbody.getText().toString();
	SimpleDateFormat datetimeformat=new SimpleDateFormat(Date_Time_Format);
	String reminderdatetime=datetimeformat.format(c.getTime());
	if(mRowID==null){
	long id=mdbhelper.createReminder(title,body,reminderdatetime);
	if(id>0)
	{mRowID=id;
	}
	   }
	else{
	mdbhelper.updatereminder(mRowID,title,body,reminderdatetime);
		}
    new ReminderManager(this).setReminder(mRowID,c);
}

}
