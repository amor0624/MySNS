package com.hanna.mysns;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusUpdateActivity extends Activity implements OnClickListener,
        TextWatcher {
	private static final String TAG = "StatusUpdate";
	EditText statusEditText;
	TextView textCount;
	Button statusUpdateButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statusupdate);

		statusEditText = (EditText) findViewById(R.id.statusEditText);
		statusEditText.addTextChangedListener(this);

		textCount = (TextView) findViewById(R.id.statusTextCount);

		statusUpdateButton = (Button) findViewById(R.id.statusUpdateButton);
		statusUpdateButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String status = statusEditText.getText().toString();
		new PostStatus().execute(status);
	}

	class PostStatus extends AsyncTask<String, Integer, String> {
		Twitter twitter;

		@Override
		protected String doInBackground(String... statuses) {
			twitter = ((MySNSApplication) getApplication()).getTwitter();
			try {
				twitter4j.Status status = twitter.updateStatus(statuses[0]);
				return status.getText();
			} catch (TwitterException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(StatusUpdateActivity.this, result, Toast.LENGTH_LONG)
			        .show();

			if (findViewById(R.id.statusUpdateButton) != null)
				finish();
		}

	}

	@Override
	public void afterTextChanged(Editable statusText) {
		int count = 140 - statusText.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		if (count < 0)
			textCount.setTextColor(Color.RED);
		else if (count < 10)
			textCount.setTextColor(Color.YELLOW);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	        int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

}
