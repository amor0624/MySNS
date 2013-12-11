package com.hanna.mysns;

import java.util.List;
import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Application;
import android.content.ContentValues;
import android.util.Log;

public class MySNSApplication extends Application {
	private static final String TAG = MySNSApplication.class.getSimpleName();
	Twitter twitter;
	boolean isServiceRunning;
	private StatusData statusData;
	private BitmapCache mBitmapCache;
	
	@Override
    public void onCreate() {
	    super.onCreate();
	    statusData = new StatusData(this);
	    mBitmapCache = new BitmapCache(this);
    }
	
	

	@Override
    public void onTerminate() {
	    super.onTerminate();
	    mBitmapCache = null;
    }

	public synchronized Twitter getTwitter(){
		if (this.twitter == null){
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
					//values.clear();
					values.put(StatusData.C_ID, status.getId());
					long createdAt = status.getCreatedAt().getTime();
					values.put(StatusData.C_CREATED_AT, createdAt);
					values.put(StatusData.C_TEXT, status.getText());
					values.put(StatusData.C_USER_ID, status.getUser().getId());
					values.put(StatusData.C_USER_NAME, status.getUser().getName());
					values.put(StatusData.C_USER_IMAGEURL, status.getUser().getProfileImageURL());
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
	
	public boolean isServiceRunning(){
		return this.isServiceRunning;
	}
	
	public void setServiceRunnin(boolean serviceRunnintg){
		this.isServiceRunning = serviceRunnintg;
	}
	
	public StatusData getStatusData() {
	    return statusData;
    }
	
	public BitmapCache getBitmapCache(){
		return mBitmapCache;
	}
}
