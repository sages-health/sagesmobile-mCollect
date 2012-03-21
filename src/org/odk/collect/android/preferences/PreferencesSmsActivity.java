/******************************************************************************
*                              COPYRIGHT NOTICE                              *
* Copyright (c) 2011 The Johns Hopkins University/Applied Physics Laboratory *
*                            All rights reserved.                            *
*                                                                            *
* This material was developed with U.S. Government support.                  *
* The U.S. Government has certain rights in this material.                   *
******************************************************************************/


package org.odk.collect.android.preferences;

import org.odk.collect.android.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.provider.MediaStore.Images;

/**
 * @author pokuam1
 */
public class PreferencesSmsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    protected static final int IMAGE_CHOOSER = 0;

//    public static String KEY_LAST_VERSION = "lastVersion";
//    public static String KEY_FIRST_RUN = "firstRun";
//    public static String KEY_SHOW_SPLASH = "showSplash";
//    public static String KEY_SPLASH_PATH = "splashPath";
//    public static String KEY_FONT_SIZE = "font_size";

//    public static String KEY_SERVER_URL = "server_url";
//    public static String KEY_USERNAME = "username";
//    public static String KEY_PASSWORD = "password";

//    public static String KEY_PROTOCOL = "protocol";
//    public static String KEY_FORMLIST_URL = "formlist_url";
//    public static String KEY_SUBMISSION_URL = "submission_url";
    
    public static String KEY_PREFSCREEN_XFORM_OVERRIDE = "xformsettings_disabled";
    public static String KEY_GSMSERVER_NUM = "gsmserverNum";
    public static String KEY_DELIMITER = "delimiter";
    public static String KEY_TICKSYMBOL = "tickSymbol";
    public static String KEY_PRESERVE_FORMAT = "preserveFormat";
    public static String KEY_INCLUDE_TAGS = "includeTags";
    public static String KEY_FILL_BLANKS = "fillBlanks";
    public static String KEY_USE_TICKS = "useTicks";
    

    private EditTextPreference mGsmServerNumPreference;
    private EditTextPreference mDelimiterPreference;
    private EditTextPreference mTickSymbolPreference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_sms);
        /**
         * FIX FOR: nested preference screen had black background that "hid" widgets
         * http://stackoverflow.com/questions/3164862/black-screen-in-inner-preferencescreen
         * BROKE ONCE I ROTATED PHONE.
         **/

        /**
         * http://code.google.com/p/android/issues/detail?id=4611
         * THE TRUE FIX. UGH.
         * 
         * nice general example 
         * http://www.kaloer.com/android-preferences
         */
        
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.general_preferences));
        updateGsmServerNum();
        updateDelimiter();
        updateTickSymbol();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
            this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED) {
            // request was canceled, so do nothing
            return;
        }

        switch (requestCode) {

            case IMAGE_CHOOSER:

                // get gp of chosen file
                Uri uri = intent.getData();
                String[] projection = {
                    Images.Media.DATA
                };

                Cursor c = managedQuery(uri, projection, null, null, null);
                startManagingCursor(c);
                int i = c.getColumnIndexOrThrow(Images.Media.DATA);
                c.moveToFirst();

                break;

        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	/* if (key.equals(KEY_PROTOCOL)) {
            updateProtocol();
            updateServerUrl();
            updateUsername();
            updatePassword();
            updateFormListUrl();
            updateSubmissionUrl();
        } else if (key.equals(KEY_SERVER_URL)) {
            updateServerUrl();
        } else if (key.equals(KEY_FORMLIST_URL)) {
            updateFormListUrl();
        } else if (key.equals(KEY_SUBMISSION_URL)) {
            updateSubmissionUrl();
        } else if (key.equals(KEY_USERNAME)) {
            updateUsername();
        } else if (key.equals(KEY_PASSWORD)) {
            updatePassword();
        } else if (key.equals(KEY_SPLASH_PATH)) {
            updateSplashPath();
       } else if (key.equals(KEY_FONT_SIZE)) {
            updateFontSize();
        }  else */
    	if (key.equals(KEY_GSMSERVER_NUM)) {
        	updateGsmServerNum();
        } else if (key.equals(KEY_DELIMITER)) {
        	updateDelimiter();
        } else if (key.equals(KEY_TICKSYMBOL)) {
        	updateTickSymbol();
        }

    }

//    private void updateFontSize() {
//        ListPreference lp = (ListPreference) findPreference(KEY_FONT_SIZE);
//        lp.setSummary(lp.getEntry());
//    }

    private void updateGsmServerNum() {
    	mGsmServerNumPreference = (EditTextPreference) findPreference(KEY_GSMSERVER_NUM);
    	mGsmServerNumPreference.setSummary(mGsmServerNumPreference.getText());
    }

    private void updateDelimiter() {
    	mDelimiterPreference = (EditTextPreference) findPreference(KEY_DELIMITER);
    	mDelimiterPreference.setSummary(mDelimiterPreference.getText());
    }

    private void updateTickSymbol() {
    	mTickSymbolPreference = (EditTextPreference) findPreference(KEY_TICKSYMBOL);
    	mTickSymbolPreference.setSummary(mTickSymbolPreference.getText());
    }
}

