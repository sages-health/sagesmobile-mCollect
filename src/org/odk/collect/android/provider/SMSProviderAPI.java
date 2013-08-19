package org.odk.collect.android.provider;

import android.provider.BaseColumns;

public final class SMSProviderAPI {

	public static final String AUTHORITY = "org.odk.collect.android.provider.odk.sms";
	public static final String SMS_TABLE_NAME = "sms";
	public static final String DATABASE_NAME = "sms.db";
	
	private SMSProviderAPI() {}
	
	public static final class SMSColumns implements BaseColumns {
		private SMSColumns() {}
		
		public static final String DISPLAY_NAME = "displayName";
		public static final String FORM_RECEIVED = "formReceived";
		public static final String FORM_PARSED = "formParsed";
		public static final String INSTANCE_KEY_ID = "instanceId";
		public static final String TX_ID = "txId";
		public static final String TIMESTAMP = "timestamp";
	}
}
