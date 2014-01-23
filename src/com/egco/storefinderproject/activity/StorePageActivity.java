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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.StorePageProductListAdapter;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class StorePageActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private PlusClient gPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private Context mContext;
	private Button homeButton;
	private Button storeDetailButton;
	private Button addItemButton;
	private ListView productListView;

	private StorePageProductListAdapter productListAdapter;

	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.storepage);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		mContext = this;
		getActionBar().hide();

		gPlusClient = new PlusClient.Builder(this, this, this).build();

		progressBar = (ProgressBar) findViewById(R.id.storepage_progressbar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		homeButton = (Button) findViewById(R.id.storepage_mainpage_button);
		homeButton.setOnClickListener(onHomeButtonClick);

		storeDetailButton = (Button) findViewById(R.id.storepage_setting_button);
		storeDetailButton.setOnClickListener(onStoreDetailButtonClick);

		addItemButton = (Button) findViewById(R.id.storepage_additem);
		addItemButton.setOnClickListener(onAddItemButtonClick);

		productListView = (ListView) findViewById(R.id.storepage_listview);

	}

	private OnClickListener onAddItemButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext,
					ProductDetailPageActivity.class);
			intent.putExtra(ApplicationConstant.INTENT_ACTION,
					ApplicationConstant.ACTION_PRODUCT_DETAIL_ADD);
			startActivity(intent);

		}
	};

	private OnClickListener onHomeButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, MainPageActivity.class);
			startActivity(intent);

		}
	};

	private OnClickListener onStoreDetailButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, StoreDetailPageActivity.class);
			startActivity(intent);
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
		new getProductListAsyncTask().execute();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	class getProductListAsyncTask extends
			AsyncTask<String, String, ArrayList<ProductModel>> {

		private final String PARAM_merchantemail = "email";

		private final String RESULT_SUCCESS = "success";
		private final String RESULT_PRODUCTLIST = "productlist";
		
		private final String RESULT_PID = "pid";
		private final String RESULT_PRODUCT_NAME = "productname";
		private final String RESULT_PRODUCT_PRICE = "productprice";
		private final String RESULT_PRODUCT_IMG = "productimg";
		private final String RESULT_PRODUCT_DISCOUNT = "productdiscount";
		private final String RESULT_PRODUCT_SHIPPING = "productshipping";
		private final String RESULT_PRODUCT_AVAILABLE = "productavailable";
		private final String RESULT_PRODUCT_DESCRIPTION = "productdescription";
		private final String RESULT_PRODUCT_TYPE = "producttype";

		@Override
		protected ArrayList<ProductModel> doInBackground(String... params) {
			ArrayList<ProductModel> resultList = new ArrayList<ProductModel>();
			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_GET_ALL_PRODUCT_LIST);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(PARAM_merchantemail,
						gPlusClient.getAccountName()));

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
				int success = jsonDataObject.getInt(RESULT_SUCCESS);
				if (success == 1) {
					JSONArray jsonDataArray = jsonDataObject
							.getJSONArray(RESULT_PRODUCTLIST);
					for (int i = 0; i < jsonDataArray.length(); i++) {
						JSONObject product = jsonDataArray.getJSONObject(i);
						ProductModel tempModel = new ProductModel();
						tempModel.setProductID(product.getInt(RESULT_PID));
						tempModel.setProductName(product.getString(RESULT_PRODUCT_NAME));
						tempModel.setPrice(product.getDouble(RESULT_PRODUCT_PRICE));
						tempModel.setProductImgURL(product.getString(RESULT_PRODUCT_IMG));
						tempModel.setDiscount(product.getDouble(RESULT_PRODUCT_DISCOUNT));
						tempModel.setShippingCost(product.getDouble(RESULT_PRODUCT_SHIPPING));
						tempModel.setAvailable(product.getInt(RESULT_PRODUCT_AVAILABLE) == 1 ? true : false);
						tempModel.setDescription(product.getString(RESULT_PRODUCT_DESCRIPTION));
						tempModel.setType(product.getInt(RESULT_PRODUCT_TYPE));
						resultList.add(tempModel);
					}
				} else {
					// TODO: handle error
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return resultList;
		}

		@Override
		protected void onPostExecute(ArrayList<ProductModel> result) {
			super.onPostExecute(result);
			
			productListAdapter = new StorePageProductListAdapter(mContext, result);

			productListView.setAdapter(productListAdapter);

			progressBar.setVisibility(View.GONE);
		}

	}
}
