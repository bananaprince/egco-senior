package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.egco.storefinderproject.utils.BitmapUtils;
import com.egco.storefinderproject.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ProductDetailPageActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private PlusClient gPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private Context mContext;
	String action;

	private ImageView productImg;
	private EditText productNameValue;
	private EditText productPriceValue;
	private EditText productDescriptionValue;
	private EditText productShippingValue;
	private Button submitButton;
	private Button cancleButton;
	
	private ProductModel editItemModel;
	
	private Bitmap productImgBitmap;

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productdetailpage);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		mContext = this;
		getActionBar().hide();

		gPlusClient = new PlusClient.Builder(this, this, this).build();

		action = getIntent().getStringExtra(ApplicationConstant.INTENT_ACTION);

		progressBar = (ProgressBar) findViewById(R.id.productdetailpage_progressbar);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		productImg = (ImageView) findViewById(R.id.productdetailpage_product_image);
		productNameValue = (EditText) findViewById(R.id.productdetailpage_product_name_value);
		productPriceValue = (EditText) findViewById(R.id.productdetailpage_product_price_value);
		productDescriptionValue = (EditText) findViewById(R.id.productdetailpage_product_description_value);
		productShippingValue = (EditText) findViewById(R.id.productdetailpage_product_shipping_value);
		submitButton = (Button) findViewById(R.id.productdetailpage_submit_button);
		cancleButton = (Button) findViewById(R.id.productdetailpage_cancle_button);
		
		productImg.setOnClickListener(onAddProductImgClick);
		productPriceValue.setOnFocusChangeListener(onMoneyFieldFocusChange);
		productShippingValue.setOnFocusChangeListener(onMoneyFieldFocusChange);

		if (ApplicationConstant.ACTION_PRODUCT_DETAIL_ADD
				.equalsIgnoreCase(action)) {
			// add new item case
			initAdd();
		} else if (ApplicationConstant.ACTION_PRODUCT_DETAIL_EDIT
				.equalsIgnoreCase(action)) {
			// edit item case
			initEdit();
		}
	}

	void initAdd() {
		submitButton.setOnClickListener(onAddSubmitButtonClick);

	}
	
	void initEdit() {
		editItemModel = (ProductModel)getIntent().getSerializableExtra(ApplicationConstant.INTENT_PRODUCT_MODEL);

		Picasso.with(mContext).load(editItemModel.getProductImgURLFullPath()).placeholder(R.drawable.add_image_512).into(productImg);
		productNameValue.setText(editItemModel.getProductName());
		productPriceValue.setText(""+editItemModel.getPrice());
		productDescriptionValue.setText(editItemModel.getDescription());
		productShippingValue.setText(""+editItemModel.getShippingCost());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ApplicationConstant.REQUEST_CODE_PHOTO_PICKER)
			if (resultCode == RESULT_OK) {
				
				Bitmap productImageBitmap = (Bitmap) data
						.getParcelableExtra(ApplicationConstant.INTENT_CROPPED_IMAGE);
				productImageBitmap = BitmapUtils.addWatermark(
						productImageBitmap, "#egcoStorefinder", 5);
				productImg.setImageBitmap(productImageBitmap);
				productImgBitmap = productImageBitmap;
			} else {
				Log.v("product detauk", "didn't get image");
				ToastUtils.getToast(mContext, "Something wrong", ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT).show();
			}

	}

	OnClickListener onAddProductImgClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();

			Intent intent = new Intent(mContext, ImageCropperPageActivity.class);
			startActivityForResult(intent,
					ApplicationConstant.REQUEST_CODE_PHOTO_PICKER);
			progressBar.setVisibility(View.GONE);
		}
	};
	
	OnFocusChangeListener onMoneyFieldFocusChange = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus == false) {
				String text = ((EditText)v).getText().toString();
				DecimalFormat df = new DecimalFormat("#.##");
				((EditText)v).setText(df.format(Double.parseDouble(text)));
			}
			
		}
	};

	OnClickListener onAddSubmitButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();

			new addNewProductAsyncTask().execute();
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
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();
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
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	class addNewProductAsyncTask extends AsyncTask<String, String, Integer> {

		private final String PARAM_merchantemail = "merchantemail";
		private final String PARAM_productname = "productname";
		private final String PARAM_productprice = "productprice";
		private final String PARAM_productimg = "productimg";
		private final String PARAM_productdiscount = "productdiscount";
		private final String PARAM_productshipping = "productshipping";
		private final String PARAM_productavailable = "productavailable";
		private final String PARAM_productdescription = "productdescription";
		private final String PARAM_producttype = "producttype";
		private final String PARAM_productimgsrc = "productimgsrc";
		
		private final String RESULT_success = "success";

		@Override
		protected Integer doInBackground(String... params) {
			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			try {
				
				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_ADD_NEW_PRODUCT);
				String url = sb.toString();

				Log.v("url=", url);
				
				DecimalFormat df = new DecimalFormat("#.##");
				
				String merchantemail = gPlusClient.getAccountName();
				String productname = productNameValue.getText().toString();
				String productprice = df.format(Double.parseDouble(productPriceValue.getText().toString()));
				String productimg = merchantemail + Calendar.getInstance().getTime().toString()+".png"; // product image name = email + time
				String productdiscount = "0";
				String productshipping = df.format(Double.parseDouble(productShippingValue.getText().toString()));
				String productavailable = "1"; // TODO: this is just mock value
				String productdescription = productDescriptionValue.getText().toString();
				String producttype = "0"; // TODO: this is just mock value
				
				Bitmap tempProductImgBitmap = productImgBitmap;
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				tempProductImgBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
				byte [] byteArray = stream.toByteArray();
				String imageString = Base64.encodeBytes(byteArray);
				
				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(PARAM_merchantemail, merchantemail));
				entity.add(new BasicNameValuePair(PARAM_productname, productname));
				entity.add(new BasicNameValuePair(PARAM_productprice, productprice));
				entity.add(new BasicNameValuePair(PARAM_productimg, productimg));
				entity.add(new BasicNameValuePair(PARAM_productdiscount, productdiscount));
				entity.add(new BasicNameValuePair(PARAM_productshipping, productshipping));
				entity.add(new BasicNameValuePair(PARAM_productavailable, productavailable));
				entity.add(new BasicNameValuePair(PARAM_productdescription, productdescription));
				entity.add(new BasicNameValuePair(PARAM_producttype, producttype));
				entity.add(new BasicNameValuePair(PARAM_productimgsrc, imageString));
				
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
			
			int success = -1;

			try {
				Log.v("result", jsonDataLine);
				
				jsonDataObject = new JSONObject(jsonDataLine);

				success = jsonDataObject.getInt(RESULT_success);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return success;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			Log.v("addproduct", ""+result);
			
			if(result == 1) {
				finish();
			} else {
				// TODO: insert product is not success
			}
		}

	}

}