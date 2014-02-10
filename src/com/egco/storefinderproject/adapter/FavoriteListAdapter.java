package com.egco.storefinderproject.adapter;

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

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.activity.ResultPageDetailActivity;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FavoriteListAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	private Context mContext;
	private ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
	
	public FavoriteListAdapter(Context mContext,
			ArrayList<ProductModel> productList) {
		super();
		this.mContext = mContext;
		this.layoutInflater = LayoutInflater.from(mContext);
		this.productList = productList;
	}
	
	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView productImg;
		TextView productName;
		final ProductModel productItem;
		
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.productlistview_cell_fav, null);
		}
		
		productImg = (ImageView) convertView.findViewById(R.id.cell_fav_product_img);
		productName = (TextView) convertView.findViewById(R.id.cell_fav_product_name);
		
		productItem = productList.get(position);
		
		Picasso.with(mContext).load(productItem.getProductImgURLFullPath()).placeholder(R.drawable.confused_75).error(R.drawable.cry_75).into(productImg);
		productName.setText(productItem.getProductName());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new getProductData().execute(Integer.toString(productItem.getProductID()));
				
			}
		});
		
		return convertView;
	}
	
	class getProductData extends AsyncTask<String, String, ProductModel> {

		final String PARAM_pid = "pid";
		
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
		protected ProductModel doInBackground(String... params) {
			ArrayList<ProductModel> resultList = new ArrayList<ProductModel>();
			InputStream inputStream = null;
			String jsonDataLine = null;
			JSONObject jsonDataObject = null;
			
			try {

				StringBuilder sb = new StringBuilder();
				sb.append(ApplicationConstant.SERVICE_URL);
				sb.append(ApplicationConstant.SERVICE_GET_PRODUCT_DATA);
				String url = sb.toString();

				Log.v("url=", url);

				List<NameValuePair> entity = new ArrayList<NameValuePair>();
				entity.add(new BasicNameValuePair(PARAM_pid,
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

			return resultList.get(0);
		}

		@Override
		protected void onPostExecute(ProductModel result) {
			super.onPostExecute(result);
			Intent intent = new Intent(mContext, ResultPageDetailActivity.class);
			intent.putExtra(ApplicationConstant.INTENT_PRODUCT_MODEL, result);
			mContext.startActivity(intent);
		}
		
	}
	
}
