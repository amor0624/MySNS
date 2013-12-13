package com.hanna.mysns;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	ListView mainMenuList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		IconMenu iconMenu_data[] = new IconMenu[] {
		        new IconMenu(R.drawable.icon_twitter, getResources().getString(
		                R.string.titleTwitter)),
		        new IconMenu(R.drawable.icon_settings, getResources()
		                .getString(R.string.titleSettings)) };

		IconMenuAdapter adapter = new IconMenuAdapter(this,
		        R.layout.iconmenu_row, iconMenu_data);

		mainMenuList = (ListView) findViewById(R.id.main_menu_list);
		mainMenuList.setAdapter(adapter);
		mainMenuList.setOnItemClickListener(mItemClickListener);
	}

	AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		        long id) {
			IconMenuAdapter iconMenuAdapter = (IconMenuAdapter) parent
			        .getAdapter();
			IconMenu iconMenu = (IconMenu) iconMenuAdapter.getItem(position);

			if (iconMenu.title.equals(getResources().getString(
			        R.string.titleTwitter)))
				startActivity(new Intent(MainActivity.this,
				        TwitterTimelineActivity.class)
				        .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
			else if (iconMenu.title.equals(getResources().getString(
			        R.string.titleSettings)))
				startActivity(new Intent(MainActivity.this,
				        SettingsActivity.class)
				        .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
				
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
