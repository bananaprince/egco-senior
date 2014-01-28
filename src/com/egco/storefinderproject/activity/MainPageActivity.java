package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.MainPageViewPagerAdapter;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.squareup.picasso.Picasso;

public class MainPageActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private MainPageViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;
	private ActionBar actionBar;
	private PlusClient gPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private Context mContext;
	private EditText queryEditText;
	private EditText queryTextHighlight;
	private ImageView querySubmit;
	private Button storeButton;
	
	private Fragment mainFragment;
	private Fragment historyFragment;
	private Fragment favoriteFragment;

	private TelephonyManager telMng;

	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainpage);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		mContext = this;

		gPlusClient = new PlusClient.Builder(this, this, this).build();

		telMng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// Setup ViewPager
		viewPagerAdapter = new MainPageViewPagerAdapter(
				getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.mainpage_viewpager);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(viewPagerOnPageChangeListener);

		// Setup ActionBar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(viewPagerAdapter.getPageTitle(i))
					.setTabListener(actionBarTabListener));
		}
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		viewPager.setCurrentItem(1);
		
		historyFragment = viewPagerAdapter.getItem(0);
		mainFragment = viewPagerAdapter.getItem(1);
		favoriteFragment = viewPagerAdapter.getItem(2);

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

	private ActionBar.TabListener actionBarTabListener = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab arg0,
				android.app.FragmentTransaction arg1) {

		}

		@Override
		public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
			viewPager.setCurrentItem(arg0.getPosition());

		}

		@Override
		public void onTabUnselected(Tab arg0,
				android.app.FragmentTransaction arg1) {

		}
	};

	private OnPageChangeListener viewPagerOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			actionBar.setSelectedNavigationItem(arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

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
		initMainFragmentLayout();

	}

	@Override
	public void onDisconnected() {

	}

	private void initMainFragmentLayout() {

		progressBar = (ProgressBar) mainFragment.getView().findViewById(R.id.mainpage_progressbar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		// Load user data from g+ account
		Picasso.with(this).setDebugging(true);
		String imgURL = gPlusClient.getCurrentPerson().getImage().getUrl();
		imgURL = imgURL.replace("?sz=50", "?sz=175");
		Picasso.with(this).load(imgURL)
				.into((ImageView) mainFragment.getView().findViewById(R.id.mainpage_user_img));
		((TextView) mainFragment.getView().findViewById(R.id.mainpage_username)).setText(gPlusClient
				.getCurrentPerson().getDisplayName());

		queryEditText = (EditText) mainFragment.getView().findViewById(R.id.mainpage_search_query);
		queryTextHighlight = (EditText) mainFragment.getView().findViewById(R.id.mainpage_search_query_highlight);
		queryEditText.addTextChangedListener(queryWatcher);

		querySubmit = (ImageView) mainFragment.getView().findViewById(R.id.mainpage_search_submit_query_img);
		querySubmit.setOnClickListener(onSubmitQuery);

		storeButton = (Button) mainFragment.getView().findViewById(R.id.mainpage_storepage_button);
		storeButton.setOnClickListener(onStoreButtonClick);

		progressBar.setVisibility(View.GONE);
	}

	private OnClickListener onStoreButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();
			new CheckUserStatus().execute();
		}
	};

	private OnClickListener onSubmitQuery = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, ResultPageActivity.class);
			intent.putExtra(ApplicationConstant.INTENT_QUERY, queryEditText.getText().toString());
			startActivity(intent);

		}
	};

	private TextWatcher queryWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			SpannableString spannableQueryText = new SpannableString(s);
			Matcher matcher = Pattern.compile("[#]+[A-Za-z0-9]+\\b").matcher(
					spannableQueryText);
			while (matcher.find()) {
				spannableQueryText.setSpan(
						new BackgroundColorSpan(Color.argb(127, 87, 115, 171)),
						matcher.start(), matcher.end(), 0);
			}
			queryTextHighlight.setText(spannableQueryText);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	class CheckUserStatus extends AsyncTask<String, String, Integer> {

		/*
		 * Note : Return of this task -2 : Application Error -1 : Banned 0 : New
		 * User 1 : Merchant User 2 : Verified User
		 */

		private final String PARAM_USER_EMAIL = "email";
		private final String PARAM_USER_IMEI = "imei";
		private final String FIELD_USER_STATUS = "status";
		private final String FIELD_SUCCESS = "success";

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {
				String email = gPlusClient.getAccountName();
				String imei = telMng.getDeviceId();

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_GET_USER_STATUS);
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
				Log.v("result", jsonDataLine);
				jsonDataObject = new JSONObject(jsonDataLine);

				
				if(jsonDataObject.getInt(FIELD_SUCCESS) == 1) {
					return jsonDataObject.getInt(FIELD_USER_STATUS);
				} else {
					return -2;
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
			} else if (result == 0) {
				intent = new Intent(mContext, StoreDetailPageActivity.class);
			} else {
				intent = new Intent(mContext, StorePageActivity.class);
			}
			startActivity(intent);
			progressBar.setVisibility(View.GONE);
		}

	}

}
