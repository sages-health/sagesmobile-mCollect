package org.odk.collect.android.activities;

import org.odk.collect.android.R;
import org.odk.collect.android.adapters.SMSCursorAdapter;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.AdminPreferencesActivity;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.provider.SMSProvider.DatabaseHelper;
import org.odk.collect.android.provider.SMSProviderAPI;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SMSViewer extends ListActivity {

	private static final int FILTERING = Menu.FIRST;
	private static final int SORTING = Menu.FIRST+1;
	
	private AlertDialog mAlertDialog;
	
	private static boolean EXIT = true;
	
	private String where;
	private String orderBy;
	
	private MenuItem currentItem;
	
	private SMSCursorAdapter adapter;
	
	private Handler updater;
	private Runnable r = new Runnable() {
		public void run() {
			DatabaseHelper dbHelper = new DatabaseHelper(SMSProviderAPI.DATABASE_NAME);
	        SQLiteDatabase db = dbHelper.getReadableDatabase();
			
			Cursor c = db.query(SMSProviderAPI.SMS_TABLE_NAME,
					new String[] {SMSProviderAPI.SMSColumns._ID, SMSProviderAPI.SMSColumns.DISPLAY_NAME, SMSProviderAPI.SMSColumns.FORM_PARSED, SMSProviderAPI.SMSColumns.FORM_RECEIVED, SMSProviderAPI.SMSColumns.TIMESTAMP},
					where, null, null, null, orderBy);
	        
			//setListAdapter(new SimpleCursorAdapter(this, R.layout.sms_viewer, c,
					//new String[] {SMSProviderAPI.SMSColumns.DISPLAY_NAME, SMSProviderAPI.SMSColumns.FORM_PARSED, SMSProviderAPI.SMSColumns.TIMESTAMP},
					//new int[] {R.id.sms_txt_view,R.id.sms_time_txt_view, R.id.sms_status_txt_view}));
			
			((SMSCursorAdapter) SMSViewer.this.getListView().getAdapter()).refresh(c);
			
			db.close();
			dbHelper.close();
			updater.postDelayed(r, 2500);
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // must be at the beginning of any activity that can be called from an
        // external intent
        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), EXIT);
            return;
        }
        
        try {
        where = savedInstanceState.getString("where");
        } catch(NullPointerException e){} //TODO SAGES/filipdt1: Why is this throwing NullPointerException? it should just return null
        try {
        orderBy = savedInstanceState.getString("orderBy");
        } catch(NullPointerException e) {} //TODO SAGES/filipdt1: Why is this throwing NullPointerException? it should just return null



        this.updater = new Handler();
        this.updater.postDelayed(r, 2500);
        
        DatabaseHelper dbHelper = new DatabaseHelper(SMSProviderAPI.DATABASE_NAME);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query(SMSProviderAPI.SMS_TABLE_NAME,
				new String[] {SMSProviderAPI.SMSColumns._ID, SMSProviderAPI.SMSColumns.DISPLAY_NAME, SMSProviderAPI.SMSColumns.FORM_PARSED, SMSProviderAPI.SMSColumns.FORM_RECEIVED, SMSProviderAPI.SMSColumns.TIMESTAMP},
				where, null, null, null, orderBy);
        
		//setListAdapter(new SimpleCursorAdapter(this, R.layout.sms_viewer, c,
				//new String[] {SMSProviderAPI.SMSColumns.DISPLAY_NAME, SMSProviderAPI.SMSColumns.FORM_PARSED, SMSProviderAPI.SMSColumns.TIMESTAMP},
				//new int[] {R.id.sms_txt_view,R.id.sms_time_txt_view, R.id.sms_status_txt_view}));
		
		this.adapter = new SMSCursorAdapter(getApplicationContext(), c);
		
		setListAdapter(this.adapter);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		db.close();
		dbHelper.close();
        
	}
	
	private void createErrorDialog(String errorMsg, final boolean shouldExit) {
        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON1:
                        Collect.getInstance()
                                .getActivityLogger()
                                .logAction(this, "createErrorDialog",
                                        shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        Collect.getInstance().getActivityLogger().logAction(this, "onCreateOptionsMenu", "show");
	        super.onCreateOptionsMenu(menu);
	        menu.add(Menu.NONE, FILTERING, Menu.NONE, getString(R.string.sms_filtering));
	        menu.add(Menu.NONE, SORTING, Menu.NONE, getString(R.string.sms_sorting));
	        return true;
	    }
	 
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		 
		 this.currentItem = item;
		 
		 AlertDialog.Builder b;
		 String[] types;
	        switch (item.getItemId()) {
	            case FILTERING:
	                Collect.getInstance()
	                        .getActivityLogger()
	                        .logAction(this, "onOptionsItemSelected",
	                                "FILTERING");
	                
	                b = new Builder(this);
	                b.setTitle("Filter By:");
	                types = new String[] {"Success", "In Progress", "Failed", "Unknown", "Nothing"};
	                b.setItems(types, new OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {

	                        dialog.dismiss();
	                        switch(which){
	                        case 0:
	                            SMSViewer.this.where = SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+SMSViewer.this.getResources().getString(R.string.status_on_success)+"'";
	                            currentItem.setTitle(SMSViewer.this.getString(R.string.sms_filtering) + "   (+Successful)");
	                            break;
	                        case 1:
	                        	SMSViewer.this.where = SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+SMSViewer.this.getResources().getString(R.string.status_on_in_progress)+"'";
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_filtering) + "   (+In Progress)");
	                        	break;
	                        case 2:
	                        	SMSViewer.this.where = SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+SMSViewer.this.getResources().getString(R.string.status_on_failed)+"'";
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_filtering) + "   (+Failed)");
	                        	break;
	                        case 3:
	                        	SMSViewer.this.where = SMSProviderAPI.SMSColumns.FORM_PARSED+" = '"+SMSViewer.this.getResources().getString(R.string.status_on_unknown)+"'";
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_filtering) + "   (+Unknown)");
	                        	break;
	                        case 4:
	                        	SMSViewer.this.where = null;
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_filtering));
	                        	break;
	                        }
	                        SMSViewer.this.updater.postDelayed(r, 0);
	                    }

	                });

	                b.show();
	                
	                return true;
	            case SORTING:
	                Collect.getInstance().getActivityLogger()
	                        .logAction(this, "onOptionsItemSelected", "SORTING");
	                
	                b = new Builder(this);
	                b.setTitle("Sort By:");
	                types = new String[] {"Name", "Time", "Status", "Nothing"};
	                b.setItems(types, new OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {

	                        dialog.dismiss();
	                        switch(which){
	                        case 0:
	                            SMSViewer.this.orderBy = SMSProviderAPI.SMSColumns.DISPLAY_NAME;
	                            currentItem.setTitle(SMSViewer.this.getString(R.string.sms_sorting) + "   (+Name)");
	                            break;
	                        case 1:
	                        	SMSViewer.this.orderBy = SMSProviderAPI.SMSColumns.TIMESTAMP;
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_sorting) + "   (+Time)");
	                            break;
	                        case 2:
	                        	SMSViewer.this.orderBy = SMSProviderAPI.SMSColumns.FORM_PARSED;
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_sorting) + "   (+Status)");
	                        	break;
	                        case 3:
	                        	SMSViewer.this.orderBy = null;
	                        	currentItem.setTitle(SMSViewer.this.getString(R.string.sms_sorting));
	                        	break;
	                        }
	                        SMSViewer.this.updater.postDelayed(r, 0);
	                    }

	                });

	                b.show();
	                
	                return true;
	        }
	        return super.onOptionsItemSelected(item);
	 }

	 @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("where", where);
		outState.putString("orderBy", orderBy);
	}
	 
}
