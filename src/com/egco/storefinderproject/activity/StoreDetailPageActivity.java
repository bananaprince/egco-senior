package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.constant.ApplicationConstant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class StoreDetailPageActivity extends Activity {

	private final String FIELD_IMEI = "imei";
	private final String FIELD_VERIFIED = "verified";
	private final String FIELD_STORENAME = "storename";
	private final String FIELD_STOREDETAIL = "storedetail";
	private final String FIELD_STORECONTRACT = "storecontract";
	private final String FIELD_STORETEL = "storetel";
	private final String FIELD_STOREEMAIL = "storeemail";
	private final String FIELD_SUCCESS = "success";

	private Button storeButton;
	private Context mContext;

	private EditText storenameValue;
	private EditText storedetailValue;
	private EditText storecontractValue;
	private EditText storetelValue;
	private EditText storeemailValue;

	private String tStorenameValue;
	private String tStoredetailValue;
	private String tStorecontractValue;
	private String tStoretelValue;
	private String tStoreemailValue;

	private ProgressBar progressBar;

	private TelephonyManager telMng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storedetailpage);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		getActionBar().hide();

		mContext = this;

		telMng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		storeButton = (Button) findViewById(R.id.storedetailpage_storepage_button);
		storeButton.setOnClickListener(onStoreButtonClick);

		storenameValue = (EditText) findViewById(R.id.storedetailpage_storename_value);
		storedetailValue = (EditText) findViewById(R.id.storedetailpage_storedetail_value);
		storecontractValue = (EditText) findViewById(R.id.storedetailpage_storecontract_value);
		storetelValue = (EditText) findViewById(R.id.storedetailpage_storetel_value);
		storeemailValue = (EditText) findViewById(R.id.storedetailpage_storemail_value);

		progressBar = (ProgressBar) findViewById(R.id.storedetailpage_progressbar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		new getStoreDetail().execute();

	}

	OnClickListener onStoreButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (tStorenameValue.equals(storenameValue.getText().toString())
					&& tStoredetailValue.equals(storedetailValue.getText()
							.toString())
					&& tStorecontractValue.equals(storecontractValue.getText()
							.toString())
					&& tStoretelValue
							.equals(storetelValue.getText().toString())
					&& tStoreemailValue.equals(storeemailValue.getText()
							.toString())) {
				Intent intent = new Intent(mContext, StorePageActivity.class);
				startActivity(intent);
			} else {

				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				new updateStoreDetail().execute();
			}
		}
	};

	class getStoreDetail extends
			AsyncTask<String, String, HashMap<String, String>> {

		// param : nothing;

		@Override
		protected HashMap<String, String> doInBackground(String... params) {
			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;
			HashMap<String, String> resultMap = new HashMap<String, String>();

			try {
				String imei = telMng.getDeviceId();

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_GET_STORE_DETAIL);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(FIELD_IMEI, imei));

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

				if (!jsonDataObject.getString(FIELD_STORENAME)
						.equalsIgnoreCase("null")) {
					resultMap.put(FIELD_STORENAME,
							jsonDataObject.getString(FIELD_STORENAME));
				} else {
					resultMap.put(FIELD_STORENAME, "");
				}

				if (!jsonDataObject.getString(FIELD_STOREDETAIL)
						.equalsIgnoreCase("null")) {
					resultMap.put(FIELD_STOREDETAIL,
							jsonDataObject.getString(FIELD_STOREDETAIL));
				} else {
					resultMap.put(FIELD_STOREDETAIL, "");
				}

				if (!jsonDataObject.getString(FIELD_STORECONTRACT)
						.equalsIgnoreCase("null")) {
					resultMap.put(FIELD_STORECONTRACT,
							jsonDataObject.getString(FIELD_STORECONTRACT));
				} else {
					resultMap.put(FIELD_STORECONTRACT, "");
				}

				if (!jsonDataObject.getString(FIELD_STORETEL).equalsIgnoreCase(
						"null")) {
					resultMap.put(FIELD_STORETEL,
							jsonDataObject.getString(FIELD_STORETEL));
				} else {
					resultMap.put(FIELD_STORETEL, "");
				}

				if (!jsonDataObject.getString(FIELD_STOREEMAIL)
						.equalsIgnoreCase("null")) {
					resultMap.put(FIELD_STOREEMAIL,
							jsonDataObject.getString(FIELD_STOREEMAIL));
				} else {
					resultMap.put(FIELD_STOREEMAIL, "");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return resultMap;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);

			storenameValue.setText(result.get(FIELD_STORENAME));
			storedetailValue.setText(result.get(FIELD_STOREDETAIL));
			storecontractValue.setText(result.get(FIELD_STORECONTRACT));
			storetelValue.setText(result.get(FIELD_STORETEL));
			storeemailValue.setText(result.get(FIELD_STOREEMAIL));

			tStorenameValue = result.get(FIELD_STORENAME);
			tStoredetailValue = result.get(FIELD_STOREDETAIL);
			tStorecontractValue = result.get(FIELD_STORECONTRACT);
			tStoretelValue = result.get(FIELD_STORETEL);
			tStoreemailValue = result.get(FIELD_STOREEMAIL);

			progressBar.setVisibility(View.GONE);
		}

	}

	class updateStoreDetail extends AsyncTask<String, String, Integer> {

		// param : nothing

		@Override
		protected Integer doInBackground(String... params) {
			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;
			Integer result = 0;

			try {
				String imei = telMng.getDeviceId();

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_UPDATE_STORE_DETAIL);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(FIELD_IMEI, imei));
				entity.add(new BasicNameValuePair(FIELD_STORENAME,
						storenameValue.getText().toString()));
				entity.add(new BasicNameValuePair(FIELD_STOREDETAIL,
						storedetailValue.getText().toString()));
				entity.add(new BasicNameValuePair(FIELD_STORECONTRACT,
						storecontractValue.getText().toString()));
				entity.add(new BasicNameValuePair(FIELD_STORETEL, storetelValue
						.getText().toString()));
				entity.add(new BasicNameValuePair(FIELD_STOREEMAIL,
						storeemailValue.getText().toString()));

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

				result = jsonDataObject.getInt(FIELD_SUCCESS);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (result == 1) {
				Intent intent = new Intent(mContext, StorePageActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "Cannot update data",
						Toast.LENGTH_SHORT).show();
			}
			progressBar.setVisibility(View.GONE);
		}

	}

}
