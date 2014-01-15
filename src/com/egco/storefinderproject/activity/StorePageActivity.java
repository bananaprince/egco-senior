package com.egco.storefinderproject.activity;

import android.app.Activity;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.egco.storefinderproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class StorePageActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private PlusClient gPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		gPlusClient = new PlusClient.Builder(this, this, this).build();
	}

	@Override
	protected void onStop() {
		super.onStop();
		gPlusClient.disconnect();
	}

	@Override
	protected void onStart() {
		super.onStart();
		gPlusClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				gPlusClient.disconnect();
				gPlusClient.connect();
			}
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
