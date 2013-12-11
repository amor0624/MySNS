package com.hanna.mysns;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

public class LoadProfileImageAsyncTask extends
        AsyncTask<Void, Void, Pair<Bitmap, Exception>> {
	private ImageView mImageView;
	private String mUrl;
	private BitmapCache mCache;

	public LoadProfileImageAsyncTask(BitmapCache cache, ImageView imageView,
	        String url) {
		this.mImageView = imageView;
		this.mUrl = url;
		this.mCache = cache;

		mImageView.setTag(mUrl);
	}

	@Override
	protected void onPreExecute() {
		Bitmap bm = mCache.get(mUrl);

		if (bm != null) {
			cancel(false);
			mImageView.setImageBitmap(bm);
		}
	}

	@Override
	protected Pair<Bitmap, Exception> doInBackground(Void... arg0) {
		if (isCancelled())
			return null;
		URL url;
		InputStream inStream = null;

		try {
			url = new URL(mUrl);
			URLConnection conn = url.openConnection();

			inStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inStream);
			return new Pair<Bitmap, Exception>(bitmap, null);
		} catch (Exception e) {
			return new Pair<Bitmap, Exception>(null, e);
		} finally {

		}
	}

	@Override
	protected void onPostExecute(Pair<Bitmap, Exception> result) {
		if (result == null)
			return;

		if (result.first != null && mUrl.equals(mImageView.getTag())) {
			mCache.put(mUrl, result.first);
			mImageView.setImageBitmap(result.first);
		}
	}

	public void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				Log.d("LoadProfileImageAsyncTask", "Can't close input stream?");
			}
		}
	}

}