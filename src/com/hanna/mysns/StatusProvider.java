package com.hanna.mysns;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class StatusProvider extends ContentProvider {
	static final String TAG = "StatusProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://com.hanna.mysns.statusprovider");
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.hanna.mysns.status";
	public static final String MULTIPLE_RECORD_MIME_TYPE = "vnd.android.cursor.dir/vnd.hanna.mysns.mstatus";
	StatusData statusData;
	
	private long getId(Uri uri){
		String lastPathSegment = uri.getLastPathSegment();
		if (lastPathSegment != null){
			try {
	            return Long.parseLong(lastPathSegment);
            } catch (NumberFormatException e) {
	            //at least we tried
            }
		}
		
		return -1;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		int count;
		SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
		try {
			if (id<0){
				count =  db.delete(StatusData.TWITTERTABLE, selection, selectionArgs);
			} else {
				count =  db.delete(StatusData.TWITTERTABLE, StatusData.C_ID+"="+id, null);
			}
		} finally {
			db.close();
		}
		
		//Notify the Context's ContentResolver of the change
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return this.getId(uri) < 0 ? MULTIPLE_RECORD_MIME_TYPE : SINGLE_RECORD_MIME_TYPE;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
		try {
			long id = db.insertOrThrow(StatusData.TWITTERTABLE, null, values);
			if (id == -1){
				throw new RuntimeException(String.format("%s: Failed to insert [%s] to [%s] for unknown reasons.", TAG, values, uri));
			} else {
				Uri newUri = ContentUris.withAppendedId(uri, id);
				//Notify the context's ContentResolver of the change
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		}  finally {
			db.close();
		}
	}

	@Override
	public boolean onCreate() {
		statusData = new StatusData(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
	        String sortOrder) {
		long id = this.getId(uri);
		SQLiteDatabase db = statusData.dbHelper.getReadableDatabase();
		Log.d(TAG, "querying");
		
		Cursor c;
		
		if (id < 0){
			c =  db.query(StatusData.TWITTERTABLE, projection, selection, selectionArgs, null, null, sortOrder);
		} else {
			c=  db.query(StatusData.TWITTERTABLE, projection, StatusData.C_ID+"="+id, null, null, null, null);
		}
		
		//Notify the context's ContentResolver if the cursor result set changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		long id = this.getId(uri);
		int count;
		SQLiteDatabase db = statusData.dbHelper.getWritableDatabase();
		try {
			if (id<0){
				count =  db.update(StatusData.TWITTERTABLE, values, selection, selectionArgs);
			} else {
				count = db.update(StatusData.TWITTERTABLE, values, StatusData.C_ID+"="+id, null);
			}
		} finally {
			db.close();
		}
		
		//Notify the Context's ContentResolver of the change
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
