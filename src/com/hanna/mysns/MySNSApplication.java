package com.hanna.mysns;

import java.util.List;
import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class MySNSApplication extends Application implements
        OnSharedPreferenceChangeListener {
	private static final String TAG = MySNSApplication.class.getSimpleName();
	public static final long INTERVAL_NEVER = 0;
	private SharedPreferences prefs;
	private PendingIntent pendingIntentForUpdate;
	Twitter twitter;
	boolean isServiceRunning;
	private StatusData statusData;
	private BitmapCache mBitmapCache;

	@Override
	public void onCreate() {
		super.onCreate();
		statusData = new StatusData(this);
		mBitmapCache = new BitmapCache(this);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		mBitmapCache = null;
	}

	public synchronized Twitter getTwitter() {
		if (this.twitter == null) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			        .setOAuthConsumerKey("*****")
			        .setOAuthConsumerSecret(
			                "*****")
			        .setOAuthAccessToken(
			                "*****")
			        .setOAuthAccessTokenSecret(
			                "*****");

			twitter = (new TwitterFactory(cb.build())).getInstance();
		}

		return twitter;
	}

	// Connects to the online service and puts the latest statuses into DB.
	// Returns the count of new statuses
	public synchronized int fetchStatusUpdates() {
		Log.d(TAG, "Fetching Status Updates");
		Twitter twitter = this.getTwitter();
		if (twitter == null) {
			Log.d(TAG, "Twitter connection info not initialized");
			return 0;
		}
		try {
			List<Status> statusUpdates = twitter.getHomeTimeline();
			long latestStatusCreatedAtTime = this.getStatusData()
			        .getLatestStatusCreatedAtTime();
			int count = 0;

			// loop over the timeline and print it out
			ContentValues values = new ContentValues();
			for (Status status : statusUpdates) {
				// insert into database
				// values.clear();
				values.put(StatusData.C_ID, status.getId());
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createdAt);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_USER_ID, status.getUser().getId());
				values.put(StatusData.C_USER_NAME, status.getUser().getName());
				values.put(StatusData.C_USER_IMAGEURL, status.getUser()
				        .getProfileImageURL());
				Log.d(TAG, "Got UpdaterService with id " + status.getId()
				        + ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if (latestStatusCreatedAtTime < createdAt) {
					count++;
				}
			}
			Log.d(TAG, count > 0 ? "Got " + count + " status upates"
			        : "No new status updates");
			return count;
		} catch (TwitterException e) {
			Log.e(TAG, "Failed to fetch status updates", e);
			return 0;
		}
	}

	public boolean isServiceRunning() {
		return this.isServiceRunning;
	}

	public void setServiceRunnin(boolean serviceRunnintg) {
		this.isServiceRunning = serviceRunnintg;
	}

	public StatusData getStatusData() {
		return statusData;
	}

	public BitmapCache getBitmapCache() {
		return mBitmapCache;
	}
	
	public long getInterval(){
		return Long.parseLong(prefs.getString("interval", "0"));
	}
	
	public void setAutoUpdate(){
		//Cancel the previous alarm set to update
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntentForUpdate);
		Log.d(TAG, "cancelled auto update");
		
		// Check if we should do anything at boot at all
		long interval = this.getInterval();
		if (interval == MySNSApplication.INTERVAL_NEVER){
			return;
		}

		Log.d(TAG, "setting new update interval");
		// Create the pending intent
		Intent intent = new Intent( this, UpdaterService.class);
		pendingIntentForUpdate = PendingIntent.getService(this, -1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Setup alarm service to wake up and start service periodically
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				System.currentTimeMillis(), interval, pendingIntentForUpdate);
	}

	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
		if (key.equals(getResources().getString(R.string.keyInterval))){
			this.setAutoUpdate();
		}
    }
}
