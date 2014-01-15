package com.egco.storefinderproject.activity;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.constant.ApplicationConstant;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BanPageActivity extends Activity {

	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.banpage);

		TextView banReasonTextView = (TextView) findViewById(R.id.banpage_reason);
		banReasonTextView.setText(this.getIntent().getStringExtra(
				ApplicationConstant.DENIED_SERVICE_MESSAGE));

		// Setup ActionBar
		actionBar = getActionBar();
		actionBar.hide();
	}

}
