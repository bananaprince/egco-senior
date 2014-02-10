package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.activity.ProductDetailPageActivity.productDeleteAsyncTask;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.egco.storefinderproject.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultPageDetailActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private Context mContext;

	private PlusClient gPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressBar progressBar;
	private ImageView productImage;
	private TextView productName;
	private TextView productPrice;
	private TextView productShipping;
	private TextView productDescription;

	private Button reportButton;
	private Button buyButton;
	private Button favoriteButton;
	
	private boolean isFavorite;

	private ProductModel selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultdetailpage);

		mContext = this;

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		getActionBar().hide();

		gPlusClient = new PlusClient.Builder(this, this, this).build();

		selectedItem = (ProductModel) getIntent().getSerializableExtra(
				ApplicationConstant.INTENT_PRODUCT_MODEL);

		progressBar = (ProgressBar) findViewById(R.id.resultdetailpage_progressbar);
		productImage = (ImageView) findViewById(R.id.resultdetailpage_productimage);
		productName = (TextView) findViewById(R.id.resultdetailpage_productname);
		productPrice = (TextView) findViewById(R.id.resultdetailpage_productprice);
		productShipping = (TextView) findViewById(R.id.resultdetailpage_productshipping);
		productDescription = (TextView) findViewById(R.id.resultdetailpage_productdescription);

		reportButton = (Button) findViewById(R.id.resultdetailpage_rep_button);
		buyButton = (Button) findViewById(R.id.resultdetailpage_buy_button);
		favoriteButton = (Button) findViewById(R.id.resultdetailpage_fav_button);

		progressBar.setVisibility(View.VISIBLE);

	}

	private void initDetail() {
		DecimalFormat df = new DecimalFormat("#.##");

		double productPriceVal = selectedItem.getPrice();
		double discountPriceVal = selectedItem.getDiscount();
		double shippingPriceVal = selectedItem.getShippingCost();

		Picasso.with(mContext).load(selectedItem.getProductImgURLFullPath())
				.into(productImage);
		productName.setText(selectedItem.getProductName());
		if (Math.abs(productPriceVal - discountPriceVal) > 0.01f) {
			Spannable spanText = Spannable.Factory.getInstance().newSpannable(
					df.format(discountPriceVal) + " ("
							+ df.format(productPriceVal) + ") B");
			spanText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					df.format(discountPriceVal).length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanText.setSpan(new StrikethroughSpan(),
					df.format(discountPriceVal).length() + 2,
					spanText.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			productPrice.setText(spanText);
		} else {
			productPrice.setText(df.format(productPriceVal));
		}
		if (shippingPriceVal < 0.01f) {
			productShipping.setText("Free Shipping");
			productShipping.setTextColor(Color.GREEN);
		} else {
			productShipping.setText(df.format(selectedItem.getShippingCost()));
		}
		productDescription.setText(selectedItem.getDescription());

		Log.v("result detail", "checkfavorite");

		new getFavoriteStatus().execute();
	}

	class getFavoriteStatus extends AsyncTask<String, String, Integer> {

		private final String PARAM_customeremail = "customeremail";
		private final String PARAM_pid = "pid";

		private final String RESULT_success = "success";

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_GET_FAVORITE_STATUS);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_customeremail,
						gPlusClient.getAccountName()));
				entity.add(new BasicNameValuePair(PARAM_pid, Integer
						.toString(selectedItem.getProductID())));

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

				if (jsonDataObject.getInt(RESULT_success) == 1) {
					return 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			progressBar.setVisibility(View.GONE);

			if (result == 1) {
				// current product is favorite
				favoriteButton.setText("Dislike");
				isFavorite = true;
			} else {
				isFavorite = false;
			}

			reportButton.setOnClickListener(onReportButtonClick);
			buyButton.setOnClickListener(onBuyButtonClick);
			favoriteButton.setOnClickListener(onFavoriteButtonClick);
		}
	}

	OnClickListener onReportButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			View promptsView = layoutInflater.inflate(R.layout.prompts, null);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			alertDialogBuilder.setView(promptsView);
			final EditText userInput = (EditText) promptsView.findViewById(R.id.promtsEditText);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.bringToFront();
					new addNewReport().execute(userInput.getText().toString());
				}
			});
			alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}
	};

	OnClickListener onBuyButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext);
			adBuilder.setTitle("Confirmation");
			adBuilder.setMessage("Are you sure?");
			adBuilder.setCancelable(false);
			adBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							progressBar.setVisibility(View.VISIBLE);
							progressBar.bringToFront();
							new addNewOrder().execute();

						}
					});
			adBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

						}
					});
			AlertDialog alertDialog = adBuilder.create();
			alertDialog.show();

		}
	};

	OnClickListener onFavoriteButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();
			if (isFavorite) {
				new deleteFavorite().execute();
			} else {
				new addFavorite().execute();
			}
		}
	};
	
	class  addNewReport extends AsyncTask<String, String, Integer> {
		private final String PARAM_reporteremail = "reporteremail";
		private final String PARAM_targetemail = "targetemail";
		private final String PARAM_pid = "pid";
		private final String PARAM_extra = "extra";
		
		private final String RESULT_success = "success";
		
		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_REPORT);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_reporteremail,
						gPlusClient.getAccountName()));
				entity.add(new BasicNameValuePair(PARAM_targetemail,
						selectedItem.getMerchantEmail()));
				entity.add(new BasicNameValuePair(PARAM_pid, Integer
						.toString(selectedItem.getProductID())));
				entity.add(new BasicNameValuePair(PARAM_extra,
						params[0]));

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

				if (jsonDataObject.getInt(RESULT_success) == 1) {
					return 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);

			if (result == 1) {
				ToastUtils.getToast(mContext, "Report success!",
						ToastUtils.STYLE_INFO_BLUE, ToastUtils.DURATION_SHORT)
						.show();
			} else {
				ToastUtils.getToast(mContext, "Cannot report",
						ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT)
						.show();
			}
		}
	}

	class addNewOrder extends AsyncTask<String, String, Integer> {
		private final String PARAM_customeremail = "customeremail";
		private final String PARAM_pid = "pid";
		private final String PARAM_productlastprice = "productlastprice";

		private final String RESULT_success = "success";

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_ADD_TRANSACTION);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_customeremail,
						gPlusClient.getAccountName()));
				entity.add(new BasicNameValuePair(PARAM_pid, Integer
						.toString(selectedItem.getProductID())));
				entity.add(new BasicNameValuePair(PARAM_productlastprice,
						Double.toString(selectedItem.getDiscount())));

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

				if (jsonDataObject.getInt(RESULT_success) == 1) {
					return 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);

			if (result == 1) {
				ToastUtils.getToast(mContext, "Order product success!",
						ToastUtils.STYLE_INFO_BLUE, ToastUtils.DURATION_SHORT)
						.show();
			} else {
				ToastUtils.getToast(mContext, "Cannot order product",
						ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT)
						.show();
			}
		}
	}

	class deleteFavorite extends AsyncTask<String, String, Integer> {

		private final String PARAM_customeremail = "customeremail";
		private final String PARAM_pid = "pid";

		private final String RESULT_success = "success";

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_DELETE_FAVORITE);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_customeremail,
						gPlusClient.getAccountName()));
				entity.add(new BasicNameValuePair(PARAM_pid, Integer
						.toString(selectedItem.getProductID())));

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

				if (jsonDataObject.getInt(RESULT_success) == 1) {
					return 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);

			if (result == 1) {
				ToastUtils.getToast(mContext, "Delete Favorite Success",
						ToastUtils.STYLE_INFO_BLUE, ToastUtils.DURATION_SHORT)
						.show();
				favoriteButton.setText("Favorite");
				isFavorite = false;
			} else {
				ToastUtils.getToast(mContext, "Cannot delete new Favorite",
						ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT)
						.show();
			}
		}
	}

	class addFavorite extends AsyncTask<String, String, Integer> {

		private final String PARAM_customeremail = "customeremail";
		private final String PARAM_pid = "pid";

		private final String RESULT_success = "success";

		@Override
		protected Integer doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_ADD_FAVORITE);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_customeremail,
						gPlusClient.getAccountName()));
				entity.add(new BasicNameValuePair(PARAM_pid, Integer
						.toString(selectedItem.getProductID())));

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

				if (jsonDataObject.getInt(RESULT_success) == 1) {
					return 1;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);

			if (result == 1) {
				ToastUtils.getToast(mContext, "Add New Favorite Success",
						ToastUtils.STYLE_INFO_BLUE, ToastUtils.DURATION_SHORT)
						.show();
				favoriteButton.setText("Dislike");
				isFavorite = true;
			} else {
				ToastUtils.getToast(mContext, "Cannot add new Favorite",
						ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT)
						.show();
			}
		}
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
		Log.v("result detail", "connect g+");
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
		initDetail();
	}

	@Override
	public void onDisconnected() {

	}

}
