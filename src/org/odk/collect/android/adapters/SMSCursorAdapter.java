package org.odk.collect.android.adapters;

import org.odk.collect.android.views.SMSCursorView;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class SMSCursorAdapter extends CursorAdapter {

	private SMSCursorView view;
	
	public SMSCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		SMSCursorView cursorView = (SMSCursorView) view;
		cursorView.setData(cursor);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		this.view = new SMSCursorView(context, cursor);
		return this.view;
	}
	
	
	public void refresh(Cursor cursor) {
		if(cursor.getCount() != 0) {
			cursor.moveToFirst();
		}
		this.changeCursor(cursor);
		this.view.setData(cursor);
		notifyDataSetChanged();
	}

}
