package com.egco.storefinderproject.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.MainPageViewPagerAdapter;
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
	private Context mContext = this;
	private EditText queryEditText;
	private EditText queryTextHighlight;
	private ImageView querySubmit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainpage);

		gPlusClient = new PlusClient.Builder(this, this, this).build();

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
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1) {
			viewPager.setCurrentItem(arg0.getPosition());

		}

		@Override
		public void onTabUnselected(Tab arg0,
				android.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub

		}
	};

	private OnPageChangeListener viewPagerOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			actionBar.setSelectedNavigationItem(arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	private void initMainFragmentLayout() {
		// Load user data from g+ account
		Picasso.with(this).setDebugging(true);
		String imgURL = gPlusClient.getCurrentPerson().getImage().getUrl();
		imgURL = imgURL.replace("?sz=50", "?sz=175");
		Picasso.with(this).load(imgURL)
				.into((ImageView) findViewById(R.id.mainpage_user_img));
		((TextView) findViewById(R.id.mainpage_username)).setText(gPlusClient
				.getCurrentPerson().getDisplayName());

		queryEditText = (EditText) findViewById(R.id.mainpage_search_query);
		queryTextHighlight = (EditText) findViewById(R.id.mainpage_search_query_highlight);
		queryEditText.addTextChangedListener(queryWatcher);
		
		querySubmit = (ImageView) findViewById(R.id.mainpage_search_submit_query_img);
		querySubmit.setOnClickListener(onSubmitQuery);
	}
	
	private OnClickListener onSubmitQuery = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO: send data to result page
			Toast.makeText(mContext, "go to result page", Toast.LENGTH_SHORT).show();
			
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

}
