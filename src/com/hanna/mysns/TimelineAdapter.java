package com.hanna.mysns;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {
	static final String[] FROM = { StatusData.C_CREATED_AT,
	        StatusData.C_USER_NAME, StatusData.C_USER_IMAGEURL,
	        StatusData.C_TEXT };
	static final int[] TO = { R.id.rowCreatedAt, R.id.rowUserName,
	        R.id.rowUserImage, R.id.rowText };
	BitmapCache bitmapcashe;

	// Constructor
	public TimelineAdapter(Context context, Cursor c, BitmapCache bitmapCache) {
		super(context, R.layout.twittertimeline_row, c, FROM, TO, 0);
		this.bitmapcashe = bitmapCache;
	}

	// This is where the actual binding of a cursor to view happens
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);

		// Manually bind created at timestamp to its view
		long timestamp = cursor.getLong(cursor
		        .getColumnIndex(StatusData.C_CREATED_AT));
		TextView textCreatedAt = (TextView) row
		        .findViewById(R.id.rowCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}

	@Override
    public void setViewImage(ImageView iv, String value) {
	    final String url = value;
	    iv.setImageBitmap(null);
	    new LoadProfileImageAsyncTask(bitmapcashe, iv, url).execute();
    }
	
	
	
}
