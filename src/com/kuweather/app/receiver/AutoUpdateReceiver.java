package com.kuweather.app.receiver;



import com.kuweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AutoUpdateReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent i=new Intent(context,AutoUpdateService.class);
		context.startService(i);
		
	}
	
	

}
