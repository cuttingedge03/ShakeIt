package com.nadeem.shakeit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

public class service extends Service implements SensorEventListener {
	private SensorManager sensorManager;
	private long lastUpdate;
	public int ringerMode;
	// DevicePolicyManager deviceManger;
	public float accelationSquareRoot;
	AudioManager am;
	TelephonyManager telephonyManager;
	PhoneStateListener phoneStateListener;
	AlarmManager alarm;
	Intent intent;
//	PowerManager powermanager;
//	PowerManager.WakeLock wakeLock;
	public boolean screenOn = false;
	View view;
	public float sensitivity;
	public int timeout;
	public boolean stop_music;
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";
	public static final String SERVICECMD = "com.android.music.musicservicecommand";
	public static final String CMDNAME = "command";
	public static final String CMDSTOP = "stop";

	@Override
	public void onCreate() {
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		powermanager = ((PowerManager) this
//				.getSystemService(Context.POWER_SERVICE));
//		wakeLock = powermanager.newWakeLock(
//				PowerManager.SCREEN_BRIGHT_WAKE_LOCK
//						| PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
//		wakeLock.acquire();
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
				SensorManager.SENSOR_DELAY_NORMAL);
		// REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver mReceiver = new ScreenReceiver();
		registerReceiver(mReceiver, filter);

	}

	/*
	 * @Override public int onStartCommand(Intent intent, int flags, int
	 * startId) { if (intent == null) { sensorManager.registerListener(this,
	 * sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	 * SensorManager.SENSOR_DELAY_NORMAL); } return
	 * Service.START_REDELIVER_INTENT; }
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		screenOn = intent.getBooleanExtra("screen_state", false);
		sensitivity = intent.getFloatExtra("sensitivity", 5);
		timeout = intent.getIntExtra("timeout", 15);
		stop_music = intent.getBooleanExtra("stop_music", false);
		if (am.isMusicActive()) {
			Log.d("abc", "active music");
			stopmusic();
		}
		/*
		 * if(screenOn) Log.d("abc", "on"); else Log.d("abc", "false");
		 */
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}
		// else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
		// Log.d("tag", "msg");
		// getProximity(event);
		// }

	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];
		final float z1 = z;
		accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (actualTime - lastUpdate < 400) {
			return;
		}
		lastUpdate = actualTime;
		ringerMode = am.getRingerMode();
		phoneStateListener = new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				// String callStateStr = "Unknown";
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					if (am.isMusicActive()) {// if music is active
						if (z1 > -10 && z1 < -9) {
							Intent i = new Intent(SERVICECMD);
							// Log.d("abc", "Service command send");
							i.putExtra(CMDNAME, CMDPREVIOUS);// if music is
																// active
																// and phone
																// is turned
																// then
																// next
																// song
							service.this.sendBroadcast(i);
						} else if (accelationSquareRoot >= sensitivity) {// if
																			// shake
																			// then
																			// next
							Intent i = new Intent(SERVICECMD);
							// Log.d("abc", "Service command send");
							i.putExtra(CMDNAME, CMDNEXT);
							service.this.sendBroadcast(i);
						}
						// Log.d("abc", "broadcast send");
						// } else if (z1 > 9 && z1 < 10) {
						// Intent i = new Intent(SERVICECMD);
						// Log.d("abc", "Service command send");
						// i.putExtra(CMDNAME, CMDPREVIOUS);// if music is
						// // active
						// // and phone
						// // is turned
						// // other side
						// // then
						// // previous
						// // song
						// service.this.sendBroadcast(i);
						// }
					} else {// if music nt active change ringer modes
						if (!screenOn && accelationSquareRoot >= sensitivity) {// When
																				// screen
																				// is
																				// on
																				// and
																				// shake
																				// is
																				// performed
							// Log.d("abc", "Screen is on");
							if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
								am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
							} else if (ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
								am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							} else {
								am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							}
						}
//						 else if(!screenOn &&z1 > -10 && z1 < -9){
//							 wakeLock.release();
////						 deviceManger = (DevicePolicyManager)getSystemService(
////						 Context.DEVICE_POLICY_SERVICE);
////						 deviceManger.lockNow();
//						 }
						// } else if (screenOn) {// if screen is off
						// // Log.d("abc",
						// // "Screen is off");

					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					// callStateStr = "offhook";
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					// callStateStr = "ringing. Incoming number is: "
					// + incomingNumber;

					// if (z1 > -10 && z1 < -9) {
					//
					// am.setStreamVolume(AudioManager.STREAM_RING, 0,
					// AudioManager.FLAG_ALLOW_RINGER_MODES);
					// Log.d("abc", "turning silent");
					// Toast.makeText(getApplicationContext(),
					// "turning silent",Toast.LENGTH_SHORT).show();
					// }
					break;
				}
			}
		};
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	// public void getProximity(SensorEvent event) {
	// Log.d("tag", "msg");
	// if (event.values[0] == 0) {
	// Toast.makeText(getApplicationContext(), "aba", Toast.LENGTH_SHORT)
	// .show();
	// }
	// }

	public void stopmusic() {
		if (stop_music) {
			Log.d("abc", "stop is true");
			alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(this, TimeAlarm.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
					intent, PendingIntent.FLAG_ONE_SHOT);
			alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (timeout * 60 * 1000), pendingIntent);
			Log.d("abc", "ALARM IS SET");
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}
}
