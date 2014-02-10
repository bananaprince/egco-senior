package com.egco.storefinderproject.fragment;

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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.HistoryListAdapter;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.egco.storefinderproject.fragment.MainPageFavoriteFragment.GetFavoriteList;

public class MainPageHistoryFragment extends Fragment {

	private ListView historyListView;
	private ProgressBar historyProgressBar;
	private HistoryListAdapter historyAdapter;

	private Context mContext;
	private String gPlusAccount;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.mainpage_fragment_history,
				container, false);

		historyListView = (ListView) rootView
				.findViewById(R.id.mainpage_history_listview);

		historyProgressBar = (ProgressBar) rootView
				.findViewById(R.id.mainpage_history_progressbar);

		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {
			historyProgressBar.setVisibility(View.VISIBLE);
			historyProgressBar.bringToFront();

			new GetHistoryList().execute();
		}
	}

	public void initHistory(Context mContext, String gPlusAccount) {
		this.mContext = mContext;
		this.gPlusAccount = gPlusAccount;

	}

	class GetHistoryList extends
			AsyncTask<String, String, ArrayList<ProductModel>> {

		final String PARAM_customeremail = "customeremail";

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
				sb.append(ApplicationConstant.SERVICE_GET_TRANSACTION);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(PARAM_customeremail,
						gPlusAccount));

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

			Log.v("HistoryList", "size =" + result.size());

			historyAdapter = new HistoryListAdapter(mContext, result);
			historyListView.setAdapter(historyAdapter);
			historyProgressBar.setVisibility(View.GONE);
		}
	}

}
