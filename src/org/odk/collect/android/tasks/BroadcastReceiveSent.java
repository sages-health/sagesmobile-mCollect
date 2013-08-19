package org.odk.collect.android.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.odk.collect.android.R;
import org.odk.collect.android.provider.SMSProviderAPI;
import org.odk.collect.android.provider.SMSProvider.DatabaseHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;

public class BroadcastReceiveSent extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int i = 1;
		if(this.getResultCode() != Activity.RESULT_OK) {
			DatabaseHelper dbHelper = new DatabaseHelper(SMSProviderAPI.DATABASE_NAME);
	    	SQLiteDatabase db = dbHelper.getWritableDatabase();
	    	String body = intent.getStringExtra("body");
	    	
	    	String[] brokenBody;
	    	
	    	if(body != null && (brokenBody = body.split("\\|")).length > 2) {
	    		String formNumber = brokenBody[0];
	    		String formsTotal = brokenBody[1];
	    		String txIdAndMessage = brokenBody[2];
	    		String txId = txIdAndMessage.split(":")[0];
	    	
	    		String timestamp = "Failed to send on "+(new SimpleDateFormat("EEE, MMMMMMM dd, yyyy HH:mm:ss")).format(new Date());
	    		String sendFail = context.getResources().getString(R.string.status_on_failed_to_send);
	    		
	    		db.execSQL("UPDATE "+SMSProviderAPI.SMS_TABLE_NAME+" SET "+SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+sendFail+"', "+SMSProviderAPI.SMSColumns.TIMESTAMP+" = '"+timestamp+"', "+SMSProviderAPI.SMSColumns.FORM_RECEIVED+" = 'false' WHERE "+SMSProviderAPI.SMSColumns.TX_ID+" = "+txId);
	    	}
	    	
	    	db.close();
	    	dbHelper.close();
		}
	}

}
