package com.android.reminder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ReminderListActivity extends ListActivity {
private static final int ACTIVITY_CREATE=0;
private static final int ACTIVITY_EDIT=1;
private static final int MESSAGE_ACTIVITY=0;
private ReminderDB mdbhelper;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reminder_list);
    mdbhelper=new ReminderDB(this);
    mdbhelper.open();
    filldata();
    registerForContextMenu(getListView());    
	}
	private void filldata() {
	Cursor remindercursor=mdbhelper.fetchallreminders();
	startManagingCursor(remindercursor);
	String[] from=new String[]{ReminderDB.KEY_TITLE};
	int [] to=new int[]{R.id.text1};
	SimpleCursorAdapter reminder=new SimpleCursorAdapter(this,R.layout.reminder_row,
	remindercursor,from,to);
	setListAdapter(reminder);
	setTitle("List of Tasks");
	}
//getting the item clicked          
@Override    
protected void onListItemClick(ListView l, View v, int position, long id){
    super.onListItemClick(l, v, position, id);
    Intent i=new Intent(this,ViewTask.class);
	i.putExtra(ReminderDB.KEY_ROWID, id);
	startActivityForResult(i, ACTIVITY_EDIT);
}
//Completing the activity once it is handled 
@Override
protected void onActivityResult(int requestcode,int resultcode, Intent intent)
{
	super.onActivityResult(requestcode, resultcode, intent);
    filldata();
}
//Creating the options menu
@Override
public boolean onCreateOptionsMenu(Menu menu)
{
	super.onCreateOptionsMenu(menu);
	MenuInflater mi= getMenuInflater();
	mi.inflate(R.menu.menu_list,menu);
	return true;
}
//handling the menu item selected by the user
@Override
public boolean onMenuItemSelected(int featureId, MenuItem item)
{
	switch(item.getItemId())
	{
	case R.id.menu_insert:create_Reminder();
	break;
	case R.id.SMS:create_message();
	return true;
	}
	return super.onMenuItemSelected(featureId,item);
}
//calling for the reminder edit class when the add reminder is clicked by user
public void create_Reminder()
{
	Intent i=new Intent(this,ReminderEditActivity.class);
	startActivityForResult(i,ACTIVITY_CREATE);
}
public void create_message()
{
	Intent m=new Intent(this,Message.class);
	startActivityForResult(m,MESSAGE_ACTIVITY);
}
//Creating a context menu for long presses here to delete reminder.
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuinfo)
{
	super.onCreateContextMenu(menu, v, menuinfo);
	MenuInflater mi=getMenuInflater();
	mi.inflate(R.menu.item_long_press, menu);
	menu.setHeaderTitle("Task Options");
}
//handling the selected context menu
@Override
public boolean onContextItemSelected(final MenuItem item)
{
	switch(item.getItemId())
	{
	case R.id.menu_delete:
		AlertDialog.Builder builder=new AlertDialog.Builder(ReminderListActivity.this);
		builder.setMessage("Are you sure you want to delete the task?").setTitle("Are you sure?")
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//saving the database
				AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
				mdbhelper.deleteReminder(info.id);
				filldata();	
			}
		})
		.setNegativeButton("No",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
	builder.create().show();
		
	
	break;
	case R.id.edit_task:
	AdapterContextMenuInfo infoid=(AdapterContextMenuInfo)item.getMenuInfo();
	edit(infoid.id);
	filldata();
	return true;
	}
 return  super.onContextItemSelected(item);
}
public void edit(Long id){
	Intent i=new Intent(this,ReminderEditActivity.class);
	i.putExtra(ReminderDB.KEY_ROWID, id);
	startActivityForResult(i, ACTIVITY_EDIT);
}
}



