package com.nadeem.shakeit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class ActivityDirection extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direction);
		WebView wv=(WebView)findViewById(R.id.directions);
		wv.loadDataWithBaseURL(null, getString(R.string.directions_description), "text/html", "utf-8", null);
	}

}
