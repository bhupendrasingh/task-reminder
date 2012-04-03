
package com.android.reminder;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Message extends Activity{
 Button btnSendSMS;
 EditText txtPhoneNo;
 EditText txtMessage;
 Button addcontact;
 private static final int CONTACT_PICKER_RESULT = 1001;
 private static final String DEBUG_TAG = "";
 String phoneNo=""; 
 @Override
 public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sms_scheduler);
    btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
    txtPhoneNo = (EditText) findViewById(R.id.phoneno);
    txtMessage = (EditText) findViewById(R.id.txtMessage);
    addcontact =(Button) findViewById(R.id.addcontact);
    addcontact.setOnClickListener(new View.OnClickListener()
    {
        public void onClick(View V)
        {
          Intent ContactPickerIntent = new Intent(Intent.ACTION_PICK,Contacts.CONTENT_URI);
          startActivityForResult(ContactPickerIntent,CONTACT_PICKER_RESULT);             
        }
    }
    );

    btnSendSMS.setOnClickListener(new View.OnClickListener() 
    {

        public void onClick(View v) 
        {                
            String message = txtMessage.getText().toString();
            phoneNo = txtPhoneNo.getText().toString();
            StringTokenizer st=new StringTokenizer(phoneNo,",");
            while (st.hasMoreElements())
            {
                String tempMobileNumber = (String)st.nextElement();
                if(tempMobileNumber.length()>0 && message.trim().length()>0) {
                    sendSMS(tempMobileNumber, message);
                }
                else 
                {
                    Toast.makeText(getBaseContext(), 
                            "Please enter both phone number and message.", 
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
           }
    });  
   
            }
protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	if (resultCode == RESULT_OK)
	{  
		   switch (requestCode)
		   {  
	       		case CONTACT_PICKER_RESULT:  
	       		Cursor cursor = null;  
	       		String phone = "";  
	       		try {  
	            Uri result = data.getData();  
	            Log.v(DEBUG_TAG, "Got a contact result: "+ result.toString());  
		        // get the contact id from the URI  
	            String id = result.getLastPathSegment();  
		        // query for everything email  
	            cursor = getContentResolver().query(Phone.CONTENT_URI,null, Phone.CONTACT_ID + "=?", new String[] { id },null);  
	            int phoneid = cursor.getColumnIndex(Phone.DATA);  
		        // let's just get the number  
		        if (cursor.moveToFirst())
		        {  
	            phone = cursor.getString(phoneid);  
		        Log.v(DEBUG_TAG, "Got Phone: " + phone);  
		        } else
		        {  
		        Log.w(DEBUG_TAG, "No results");  
	            }  
		        } catch (Exception e)
		        {  
	            Log.e(DEBUG_TAG, "Failed to get Phone data", e);  
	            } finally
	            {  
		        if (cursor != null)
		        {  
	             cursor.close();  
	            }  }
		        txtPhoneNo = (EditText)findViewById(R.id.phoneno);  
	            txtPhoneNo.setText(phone);  
                break;  
	       }  
		 } else {  
		      Log.w(DEBUG_TAG, "Warning: activity result not ok");  
	   }  
	} 

private void sendSMS(String phoneNumber, String message)
{
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
        new Intent(SENT), 0);

    PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
        new Intent(DELIVERED), 0);

  //---when the SMS has been sent---
    registerReceiver(new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off", 
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    },new IntentFilter(SENT));

    //---when the SMS has been delivered---
    registerReceiver(new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS delivered", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS not delivered", 
                            Toast.LENGTH_SHORT).show();
                    break;                        
            }
        }
    }, new IntentFilter(DELIVERED));        

    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);       
}
}
 