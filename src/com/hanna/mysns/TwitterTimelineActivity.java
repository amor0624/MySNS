package com.hanna.mysns;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ActivityManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TwitterTimelineActivity extends TimelineBaseActivity implements
        LoaderCallbacks<Cursor> {
	static final String TAG = "TwitterTimelineActivity";
	ListView listTimeline;
	TimelineAdapter adapter;
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.hanna.mysns.SEND_TIMELINE_NOTIFICATIONS";
	TimelineReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);

		// create new status receiver
		receiver = new TimelineReceiver();
		filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);
		
		adapter = new TimelineAdapter(this, null,
		        ((MySNSApplication) getApplication()).getBitmapCache());
		listTimeline.setAdapter(adapter);

		// initialize (or reload) cursor for this activity
		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register the receiver
		super.registerReceiver(receiver, filter, SEND_TIMELINE_NOTIFICATIONS,
		        null);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// UNregister the receiver
		unregisterReceiver(receiver);
	}

	// Responsible for reloading the cursor and the list;
	private void resetList() {
		// restart the cursor for this activity to get recently retrieved status
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, StatusProvider.CONTENT_URI, null, null,
		        null, StatusData.GET_ALL_ORDER_BY_STRING);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		// swap new cursor in
		adapter.swapCursor(newCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// called when the last cursor is provided to onLoadFinished
		// above is about to be closed. we need to make sure we are no longer
		// using it
		adapter.swapCursor(null);
	}

	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			resetList();
			Log.d("TimelineReceiver", "onReceived");
		}
	}

}