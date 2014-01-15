package com.nadeem.shakeit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	// private SensorManager sensorManager;
	// private long lastUpdate;
	// AudioManager am;
	Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// lastUpdate = System.currentTimeMillis();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		float sensitivity;
		int timeout;
		boolean stop_music;
		sensitivity = Float.parseFloat(prefs.getString("list", "5"));
		stop_music = prefs.getBoolean("stop_music", false);
		timeout = Integer.parseInt(prefs.getString("timeout", "30"));
		Intent intent = new Intent(this, service.class);
		intent.putExtra("sensitivity", sensitivity);
		intent.putExtra("stop_music", stop_music);
		intent.putExtra("timeout", timeout);
		startService(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(this, ActivityPreference.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	public void click(View v){
		startActivity(new Intent(this,ActivityDirection.class));
	}
	/*
	 * @Override protected void onResume() { super.onResume(); // register this
	 * class as a listener for the orientation and // accelerometer sensors
	 * sensorManager.registerListener(this,
	 * sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	 * SensorManager.SENSOR_DELAY_NORMAL); }
	 * 
	 * @Override protected void onPause() { // unregister listener
	 * super.onPause(); sensorManager.unregisterListener(this); }
	 */
}
