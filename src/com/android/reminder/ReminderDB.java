package com.android.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReminderDB {
private static final String DATABASE_NAME="data";
private static final String DATABASE_TABLE="reminder";
private static final int DATABASE_VERSION=1;
public static final String KEY_TITLE="title";
public static final String KEY_BODY="body";
public static final String KEY_DATE_TIME="reminder_date_time";
public static final String KEY_ROWID="_id";
private SQLiteDatabase mdb;
private DatabaseHelper mdbhelper;


private static final String DATABASE_CREATE=
	"create table "+ DATABASE_TABLE + " ( "
	+KEY_ROWID + " integer primary key autoincrement, "
	+KEY_TITLE + " text not null, "
	+KEY_BODY + " text not null, "
	+KEY_DATE_TIME + " text not null);";
private final Context mctx;
public ReminderDB(Context ctx)
{
this.mctx=ctx;
}

public ReminderDB open()throws SQLException
{
	mdbhelper=new DatabaseHelper(mctx);
	mdb=mdbhelper.getWritableDatabase();
	return this;
}

public void close()
{
	mdbhelper.close();
}

public long createReminder(String title,String body, String reminderdatetime)
{
ContentValues initialvalues=new ContentValues();	
initialvalues.put(KEY_TITLE,title);
initialvalues.put(KEY_BODY,body);
initialvalues.put(KEY_DATE_TIME,reminderdatetime);
return mdb.insert(DATABASE_TABLE,null,initialvalues);
}
public boolean deleteReminder(long rowId)
{
	return mdb.delete(DATABASE_TABLE,KEY_ROWID+"="+rowId,null)>0;
}

public Cursor fetchallreminders()
{
	return mdb.query(DATABASE_TABLE,new String[] {KEY_ROWID, KEY_TITLE,
            KEY_BODY, KEY_DATE_TIME}, null, null, null, null, null);
}

public Cursor fetchReminder(long rowId) throws SQLException{
	Cursor mcursor=mdb.query(true,DATABASE_TABLE, new String[] {KEY_ROWID,
            KEY_TITLE, KEY_BODY, KEY_DATE_TIME},KEY_ROWID +"="+ 
            rowId,null,null, null, null, null);
if(mcursor!=null)
{
	mcursor.moveToFirst();
}
return mcursor;
}
	
public boolean updatereminder(long rowId,String title,String body,String reminderdatetime)
{
	ContentValues args=new ContentValues();	
	args.put(KEY_TITLE,title);
	args.put(KEY_BODY,body);
	args.put(KEY_DATE_TIME,reminderdatetime);
	return mdb.update(DATABASE_TABLE,args,KEY_ROWID+"="+rowId,null)>0;	
}

private static class DatabaseHelper extends SQLiteOpenHelper{
	DatabaseHelper(Context context){
	super (context,DATABASE_NAME,null,DATABASE_VERSION);	
}	
	@Override
public void onCreate(SQLiteDatabase db){
	db.execSQL(DATABASE_CREATE);
}
public void onUpgrade(SQLiteDatabase db, int oldversion,int newversion)
{
	//to upgrade database
}
}
}

