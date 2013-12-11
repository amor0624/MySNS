package com.hanna.mysns;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TimelineBaseActivity extends Activity {
	MySNSApplication mySNS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mySNS = (MySNSApplication) getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timelinemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.itemStatus:
			startActivity(new Intent(this, StatusUpdateActivity.class)
			        .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
			break;
		case R.id.itemRefresh:
			startService(new Intent(this, UpdaterService.class));
			break;
		case R.id.itemPurge:
			((MySNSApplication)getApplication()).getStatusData().delete();
			Toast.makeText(this, R.string.descAllDataPurged, Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}

}
