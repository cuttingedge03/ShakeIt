package com.nadeem.shakeit;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActivityPreference extends PreferenceActivity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 
	        addPreferencesFromResource(R.xml.settings);
	 
	    }
	 @Override
		public void onBackPressed() {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			this.finish();
		}
	}
