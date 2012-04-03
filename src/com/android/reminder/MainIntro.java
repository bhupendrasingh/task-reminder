package com.android.reminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainIntro extends Activity {
		
	private static final int startcode=0;
	Button startbutton;
	@Override
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(R.layout.intro);
	startbutton=(Button)findViewById(R.id.start);
	startbutton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		startactivity();
		}
	});
}		
	private void startactivity() {
		Intent i=new Intent(this,ReminderListActivity.class);
		startActivityForResult(i,startcode);
	}

}