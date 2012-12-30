package com.wiley.aoa.the_sampler;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;

import com.wiley.wroxaccessories.UsbConnection12;
import com.wiley.wroxaccessories.WroxAccessory;

public class MainActivity extends Activity {

	RingtoneManager mRingtoneManager;

	/** The Wrox Accessory class, handles communication for us */
	private WroxAccessory mAccessory;

	/**
	 * The USB Manager, change this to com.android.hardware.UsbManager if you
	 * want the SDK 12 version of the Accessory
	 */
	private UsbManager mUsbManager;

	/**
	 * The Connection object, need to change this too if you want to use another
	 * type of accessory.
	 */
	private UsbConnection12 connection;

	private String subscription;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 1. Get a reference to the UsbManager (there's only one, so you don't
		// instantiate it)
		mUsbManager = (UsbManager) getSystemService(USB_SERVICE);

		// 2. Create the Connection object
		connection = new UsbConnection12(this, mUsbManager);

		// 3. Instantiate the WroxAccessory
		mAccessory = new WroxAccessory(this);

		mRingtoneManager = new RingtoneManager(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			mAccessory.connect(WroxAccessory.USB_ACCESSORY_12, connection);
			subscription = mAccessory.subscribe(mReceiver, "ts", 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mAccessory.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mRingtoneManager.stopPreviousRingtone();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(subscription)) {
				byte[] payload = intent.getByteArrayExtra(subscription + ".payload");
				mRingtoneManager.stopPreviousRingtone();
				mRingtoneManager.getRingtone(payload[0]).play();
			}
		}
	};
}
