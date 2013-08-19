package org.odk.collect.android.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferencesSmsActivity;
import org.odk.collect.android.provider.SMSProviderAPI;
import org.odk.collect.android.provider.SMSProvider.DatabaseHelper;

import edu.jhuapl.sages.mobile.lib.receiver.OdkSagesSmsReceiver;

import org.odk.collect.android.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BroadcastReceiveTask extends OdkSagesSmsReceiver {
	
	@Override
	protected void handleUnknownMessage(Context context, Intent intent, String sender, String body) {
		//Open up cursor to sms.db
    	DatabaseHelper dbHelper = new DatabaseHelper(SMSProviderAPI.DATABASE_NAME);
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	
    	String[] brokenBody;
    	
    	if(body != null && (brokenBody = body.split("\\|")).length > 2) {
    		String txId = brokenBody[0];
    		String responseTime = brokenBody[1];
    		String message = brokenBody[2];
    		
    		if(!context.getString(R.string.date_format).equals(context.getString(R.string.receiver_date_format))) {
    			try {
    				Date date = new SimpleDateFormat(context.getString(R.string.receiver_date_format)).parse(responseTime);
					responseTime = (new SimpleDateFormat(context.getString(R.string.date_format))).format(date);
    			} catch (ParseException e) {
    				//I certainly hope this never happens
    			}
    		}
    		
    		
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Collect.getInstance());
        	String smsNumber = settings.getString(PreferencesSmsActivity.KEY_GSMSERVER_NUM, null);
        	
        	if(smsNumber == null || !(sender.equals(smsNumber) ^ sender.equals("+"+smsNumber))) {
        		//Sages Mobile does not have a receiver or message not from receiver
        		db.close();
                dbHelper.close();
                return;
        	}
    		
        	Cursor query = db.query(SMSProviderAPI.SMS_TABLE_NAME, new String[] {SMSProviderAPI.SMSColumns.FORM_PARSED, SMSProviderAPI.SMSColumns.DISPLAY_NAME}, SMSProviderAPI.SMSColumns.TX_ID+" = '"+txId+"'", null, null, null, null);
        	if(!query.moveToFirst()) {
        		//Ill formatted message
        		//probably wrong transaction id
        		return;
        	}
        	String currentFormParsed = query.getString(0);
        	String displayName = query.getString(1);
        	
        	String success = context.getString(R.string.status_on_success);
        	String unknown = context.getString(R.string.status_on_unknown);
        	String inProgress = context.getString(R.string.status_on_in_progress);
        	String failed = context.getString(R.string.status_on_failed);
        	String failedToInsert = context.getString(R.string.status_on_failed_to_insert);
        	
        	String timestampSuccess = context.getString(R.string.timestamp_success);
        	String timestampInProgress = context.getString(R.string.timestamp_in_progress);
        	String timestampFailed = context.getString(R.string.timestamp_failed);
        	String timestampFailedToInsert = context.getString(R.string.timestamp_failed_to_insert);
        	
    		String timestamp;
    		if(message.equals(context.getString(R.string.reply_on_success)) && (currentFormParsed.equals(unknown) || currentFormParsed.equals(inProgress))) {
    			timestamp = timestampSuccess+" "+responseTime;
    			db.execSQL("UPDATE "+SMSProviderAPI.SMS_TABLE_NAME+" SET "+SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+success+"', "+SMSProviderAPI.SMSColumns.TIMESTAMP+" = '"+timestamp+"', "+SMSProviderAPI.SMSColumns.FORM_RECEIVED+" = 'true' WHERE "+SMSProviderAPI.SMSColumns.TX_ID+" = "+txId);
    			Toast.makeText(context, displayName+": "+success, Toast.LENGTH_SHORT).show();
    		}
    		else if(message.equals(context.getString(R.string.reply_on_in_progress)) && currentFormParsed.equals(unknown)) {
    			timestamp= timestampInProgress+" "+responseTime;
    			db.execSQL("UPDATE "+SMSProviderAPI.SMS_TABLE_NAME+" SET "+SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+inProgress+"', "+SMSProviderAPI.SMSColumns.TIMESTAMP+" = '"+timestamp+"', "+SMSProviderAPI.SMSColumns.FORM_RECEIVED+" = 'true' WHERE "+SMSProviderAPI.SMSColumns.TX_ID+" = "+txId);
    			Toast.makeText(context, displayName+": "+inProgress, Toast.LENGTH_SHORT).show();
    		}
    		else if((message.length() >= (context.getString(R.string.reply_on_failed).length()) && (message.substring(0, context.getString(R.string.reply_on_failed).length())).equals(context.getString(R.string.reply_on_failed))) && currentFormParsed.equals(unknown)) {
    			timestamp = timestampFailed+" "+responseTime;
    			db.execSQL("UPDATE "+SMSProviderAPI.SMS_TABLE_NAME+" SET "+SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+failed+"', "+SMSProviderAPI.SMSColumns.TIMESTAMP+" = '"+timestamp+"', "+SMSProviderAPI.SMSColumns.FORM_RECEIVED+" = 'true' WHERE "+SMSProviderAPI.SMSColumns.TX_ID+" = "+txId);
    			Toast.makeText(context, displayName+": "+failed, Toast.LENGTH_SHORT).show();
    		}
    		else if(message.equals(context.getString(R.string.reply_on_failed_to_insert)) && (currentFormParsed.equals(unknown) || currentFormParsed.equals(inProgress) || currentFormParsed.equals(success))) {
    			timestamp = timestampFailedToInsert+" "+responseTime;
    			db.execSQL("UPDATE "+SMSProviderAPI.SMS_TABLE_NAME+" SET "+SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+failedToInsert+"', "+SMSProviderAPI.SMSColumns.TIMESTAMP+" = '"+timestamp+"', "+SMSProviderAPI.SMSColumns.FORM_RECEIVED+" = 'true' WHERE "+SMSProviderAPI.SMSColumns.TX_ID+" = "+txId);
    			Toast.makeText(context, displayName+": "+failedToInsert, Toast.LENGTH_SHORT).show();
    		}
    		else if(!currentFormParsed.equals(unknown)) {
    			System.out.println(context.getString(R.string.bad_message));
    		}
    		else {
    			System.out.println(context.getString(R.string.unknown_response)+message);
    		}
    	}
    	db.close();
        dbHelper.close();
		super.handleDataMessage(context, intent, sender, body);
	}

}
