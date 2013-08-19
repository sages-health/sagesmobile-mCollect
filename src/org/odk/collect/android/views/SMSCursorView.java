package org.odk.collect.android.views;

import org.odk.collect.android.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SMSCursorView extends TableLayout {

	private Context context;
	
	TextView mainView;
	TextView timeView;
	TextView statusView;
	
	private int cId;
	private int cDisplayName;
	private int cFormReceived;
	private int cFormParsed;
	private int cTimestamp;
	
	public SMSCursorView(Context context, Cursor smsCursor) {
		super(context);
		
		this.context = context;
		
		//Set up the variables
		float row1TextSize = getResources().getDimension(R.dimen.sms_first_row_text_size);
		float row2TextSize = getResources().getDimension(R.dimen.sms_second_row_text_size);
		
		cTimestamp = smsCursor.getColumnCount()-1;
		cFormReceived = smsCursor.getColumnCount()-2;
		cFormParsed = smsCursor.getColumnCount()-3;
		cDisplayName = smsCursor.getColumnCount()-4;
		cId = smsCursor.getColumnCount()-5;
		
		
		//Set up the table's first row
		TableRow row = new TableRow(getContext());
		mainView = new TextView(getContext());
		mainView.setTextColor(Color.BLACK);
		mainView.setPadding(3, 3, 3, 3);
		mainView.setTextSize(row1TextSize);
		row.addView(mainView);
		addView(row);
		
		//Set up the table's second row
		TableRow secondRow = new TableRow(getContext());
		statusView = new TextView(getContext());
		statusView.setPadding(3, 3, 3, 3);
		statusView.setTextSize(row2TextSize);
		secondRow.addView(statusView);
		
		timeView = new TextView(getContext());
		timeView.setTextColor(Color.BLACK);
		timeView.setPadding(3, 3, 3, 3);
		timeView.setTextSize(row2TextSize);
		secondRow.addView(timeView);
		addView(secondRow);
		
		this.setColumnShrinkable(0, true);
		this.setColumnShrinkable(1, true);
		
		//Add the cursor data to the views
		setData(smsCursor);
	}
	
	public void setData(Cursor cursor) {
		if(cursor.getCount() == 0) {
			mainView.setText("");
			timeView.setText("");
			statusView.setText("");
			return;
		}
		mainView.setText(cursor.getString(cDisplayName));
		timeView.setText(cursor.getString(cTimestamp));
		
		String text = cursor.getString(cFormParsed);
		if(text.equals(getResources().getString(R.string.status_on_success))) {
			statusView.setTextColor(Color.GREEN);
		}
		else if (text.equals(getResources().getString(R.string.status_on_unknown))) {
			statusView.setTextColor(Color.rgb(255, 140, 0));
		}
		else if(text.equals(getResources().getString(R.string.status_on_in_progress))) {
			statusView.setTextColor(Color.BLUE);
		}
		else if(text.equals(getResources().getString(R.string.status_on_failed)) 
				|| text.equals(getResources().getString(R.string.status_on_failed_to_insert))
				|| text.equals(getResources().getString(R.string.status_on_failed_to_send))) {
			statusView.setTextColor(Color.RED);
		}
		statusView.setText(text);
	}

}
