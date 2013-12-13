package com.hanna.mysns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent callingIntent) {
		
		((MySNSApplication) context.getApplicationContext()).setAutoUpdate();

		Log.d("BootReceiver", "onReceived");
	}

}
