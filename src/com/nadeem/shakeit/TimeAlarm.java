package com.nadeem.shakeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeAlarm extends BroadcastReceiver {
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDSTOP = "stop";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("abc", "intent received");
		Intent i = new Intent(SERVICECMD);
		Log.d("abc", "Service command send");
		i.putExtra(CMDNAME, CMDSTOP);
		context.sendBroadcast(i);
	}

}
