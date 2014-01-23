package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.egco.storefinderproject.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class LoginPageActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private PlusClient gPlusClient;
	private ActionBar actionBar;
	private Context mContext;

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loginpage);

		mContext = this;

		// Set Action Bar
		actionBar = getActionBar();
		actionBar.hide();

		// Set progress bar
		progressBar = (ProgressBar) findViewById(R.id.loginpage_progressbar);
		progressBar.setVisibility(View.GONE);

		// Init PlusClient Object
		gPlusClient = new PlusClient.Builder(this, this, this).setScopes(
				Scopes.PLUS_LOGIN).build();
		findViewById(R.id.loginpage_signin_button).setOnClickListener(
				signInButtonOnClickListener);
	}

	private OnClickListener signInButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!gPlusClient.isConnected()) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				gPlusClient.connect();
			}

		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		gPlusClient.disconnect();
	}

	@Override
	protected void onStart() {
		super.onStart();
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
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			gPlusClient.disconnect();
			gPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		TelephonyManager telMng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String userEmail = gPlusClient.getAccountName();
		String userIMEI = telMng.getDeviceId();
		new CheckUserStatus().execute(userEmail, userIMEI);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	class CheckUserStatus extends AsyncTask<String, String, Integer> {

		/*
		 * Note : Return of this task -2 : Application Error -1 : Banned 0 : New
		 * User 1 : Merchant User 2 : Verified User
		 */

		private final String PARAM_USER_EMAIL = "email";
		private final String PARAM_USER_IMEI = "imei";
		private final String FIELD_USER_STATUS = "status";
		private final String FIELD_USER_OPERATION = "operate";
		private final String FIELD_USER_MESSAGE = "message";

		private final String OPERATE_NEW = "new";
		private final String OPERATE_READ = "read";
		private final String OPERATE_NON = "non";

		private String message = null;
		private String operate = null;

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {
				String email = params[0];
				String imei = params[1];

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_ADD_CHECK_USER);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(PARAM_USER_EMAIL, email));
				entity.add(new BasicNameValuePair(PARAM_USER_IMEI, imei));

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(entity));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				inputStream = httpEntity.getContent();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line + "\n");
				}
				inputStream.close();
				jsonDataLine = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				jsonDataObject = new JSONObject(jsonDataLine);

				Log.v("result", jsonDataLine);

				operate = jsonDataObject.getString(FIELD_USER_OPERATION);
				if (OPERATE_NON.equalsIgnoreCase(operate)) {
					Log.v("service", "non");
					message = getString(R.string.banpage_case_db_failure);
					return -2;
				} else if (OPERATE_NEW.equalsIgnoreCase(operate)) {
					Log.v("service", "new");
					message = jsonDataObject.getString(FIELD_USER_MESSAGE);
					return 0;
				} else if (OPERATE_READ.equalsIgnoreCase(operate)) {
					Log.v("service", "read");
					int userStatus = jsonDataObject.getInt(FIELD_USER_STATUS);
					if (userStatus == -1) {
						message = getString(R.string.banpage_case_break_term);
					} else {
						message = jsonDataObject.getString(FIELD_USER_MESSAGE);
					}
					return userStatus;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return -2;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			Intent intent;
			if (result < 0) {
				intent = new Intent(mContext, BanPageActivity.class);
				intent.putExtra(ApplicationConstant.INTENT_DENIED_SERVICE_MESSAGE, message);
			} else {
				intent = new Intent(mContext, MainPageActivity.class);
			}
			startActivity(intent);
			progressBar.setVisibility(View.GONE);
		}

	}

}
