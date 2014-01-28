package com.egco.storefinderproject.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.ProductAdapter;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.squareup.picasso.Picasso;

public class ResultPageActivity extends Activity {

	private ProductAdapter productAdapter;
	private GridLayout gridLayout;

	private EditText queryEditText;
	private EditText queryTextHighlight;
	private ImageView querySubmit;

	private ProgressBar progressBar;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultpage);

		mContext = this;

		Picasso.with(this).setDebugging(true);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		getActionBar().hide();

		InputMethodManager im = (InputMethodManager) this
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		gridLayout = (GridLayout) findViewById(R.id.result_gridLayout);
		queryEditText = (EditText) findViewById(R.id.resultpage_search_query);
		queryTextHighlight = (EditText) findViewById(R.id.resultpage_search_query_highlight);
		querySubmit = (ImageView) findViewById(R.id.resultpage_search_submit_query_img);
		progressBar = (ProgressBar) findViewById(R.id.resultpage_progressbar);

		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		queryEditText.addTextChangedListener(queryWatcher);
		querySubmit.setOnClickListener(onQuerySubmitClick);

		initializeGridLayout();

		Log.v("resultpage", "oncreate");

		if (!getIntent().getStringExtra(ApplicationConstant.INTENT_QUERY)
				.equalsIgnoreCase("")) {
			new queryProductAsyncTask().execute(getIntent().getStringExtra(
					ApplicationConstant.INTENT_QUERY));
		} else {
			progressBar.setVisibility(View.GONE);
		}

	}

	private void initializeGridLayout() {
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels
				- (gridLayout.getPaddingLeft() + gridLayout.getPaddingRight());
		int column;

		if (width < 500) {
			column = 2;
		} else if (width < 801) {
			column = 3;
		} else if (width < 1201) {
			column = 4;
		} else if (width < 1601) {
			column = 5;
		} else {
			column = 6;
		}

		gridLayout.setColumnCount(column);
		productAdapter = new ProductAdapter(mContext, width / column);

		Log.v("TAGG", "basesize = " + (width / column));
	}

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

	OnClickListener onQuerySubmitClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!"".equalsIgnoreCase(queryEditText.getText().toString())) {

				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();

				new queryProductAsyncTask().execute(queryEditText.getText()
						.toString());
			} else {

			}
		}
	};

	class queryProductAsyncTask extends
			AsyncTask<String, Integer, ArrayList<ProductModel>> {

		private final String PARAM_Product_Hash = "producthash";
		private final String PARAM_Product_Word = "productword";

		private final String RESULT_SUCCESS = "success";
		private final String RESULT_PRODUCTLIST = "productlist";

		private final String RESULT_PID = "pid";
		private final String RESULT_PRODUCT_NAME = "productname";
		private final String RESULT_MERCHANT_EMAIL = "merchantemail";
		private final String RESULT_PRODUCT_PRICE = "productprice";
		private final String RESULT_PRODUCT_IMG = "productimg";
		private final String RESULT_PRODUCT_DISCOUNT = "productdiscount";
		private final String RESULT_PRODUCT_SHIPPING = "productshipping";
		private final String RESULT_PRODUCT_AVAILABLE = "productavailable";
		private final String RESULT_PRODUCT_DESCRIPTION = "productdescription";
		private final String RESULT_PRODUCT_TYPE = "producttype";

		private String[] shortStopWord = { "a", "about", "an", "are", "as",
				"at", "be", "by", "com", "for", "from", "how", "in", "is",
				"it", "of", "on", "or", "that", "the", "this", "to", "was",
				"what", "when", "where", "who", "will", "with", "www" };

		@Override
		protected ArrayList<ProductModel> doInBackground(String... params) {

			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;

			ArrayList<ProductModel> resultList = new ArrayList<ProductModel>();

			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_SEARCH);
				String url = sb.toString();

				Log.v("url=", url);
				List<NameValuePair> entity = new ArrayList<NameValuePair>();

				entity.add(new BasicNameValuePair(PARAM_Product_Hash,
						getHashTagList(params[0]).toString()));
				entity.add(new BasicNameValuePair(PARAM_Product_Word,
						getWordList(params[0]).toString()));

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

					// TODO: unmocking
					Random ran = new Random();

					for (int i = 0; i < jsonDataArray.length(); i++) {
						JSONObject product = jsonDataArray.getJSONObject(i);
						ProductModel tempModel = new ProductModel();
						tempModel.setProductID(product.getInt(RESULT_PID));
						tempModel.setMerchantEmail(product
								.getString(RESULT_MERCHANT_EMAIL));
						tempModel.setProductName(product
								.getString(RESULT_PRODUCT_NAME));
						tempModel.setPrice(product
								.getDouble(RESULT_PRODUCT_PRICE));
						tempModel.setProductImgURL(product
								.getString(RESULT_PRODUCT_IMG));
						tempModel.setDiscount(product
								.getDouble(RESULT_PRODUCT_DISCOUNT));
						tempModel.setShippingCost(product
								.getDouble(RESULT_PRODUCT_SHIPPING));
						tempModel.setAvailable(product
								.getInt(RESULT_PRODUCT_AVAILABLE) == 1 ? true
								: false);
						tempModel.setDescription(product
								.getString(RESULT_PRODUCT_DESCRIPTION));
						tempModel.setType(product.getInt(RESULT_PRODUCT_TYPE));
						tempModel.setPopularity(ran.nextInt(1001));
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

			gridLayout.removeAllViews();
			productAdapter.setProductList(result);
			ArrayList<View> viewList = productAdapter.getViewList();
			Log.v("TAGG", "size == " + viewList.size());
			for (int i = 0; i < viewList.size(); i++) {
				gridLayout.addView(viewList.get(i));
			}

			progressBar.setVisibility(View.GONE);

		}

		private JSONArray getHashTagList(String s) {
			ArrayList<String> hashList = new ArrayList<String>();
			HashSet<String> set = new HashSet<String>();

			String[] arr = s.split(" ");
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].charAt(0) == '#') {
					arr[i] = arr[i].replaceAll("[_-]", "").toLowerCase();
					set.add(arr[i]);
				}
			}
			hashList.addAll(set);

			JSONArray jArr = new JSONArray();
			for (int i = 0; i < hashList.size(); i++) {
				jArr.put(hashList.get(i));
			}
			return jArr;
		}

		private JSONArray getWordList(String s) {
			ArrayList<String> wordList = new ArrayList<String>();
			HashSet<String> set = new HashSet<String>();

			s = s.replaceAll("[!@#$%^&*()-_+={}/?><]", "").toLowerCase();
			String[] arr = s.split(" ");
			for (int i = 0; i < arr.length; i++) {
				int index = Arrays.binarySearch(shortStopWord, arr[i]);
				if (index < 0 || index >= shortStopWord.length) {
					set.add(arr[i]);
				}
			}
			wordList.addAll(set);
			JSONArray jArr = new JSONArray();
			for (int i = 0; i < wordList.size(); i++) {
				jArr.put(wordList.get(i));
			}
			return jArr;
		}

	}

}
