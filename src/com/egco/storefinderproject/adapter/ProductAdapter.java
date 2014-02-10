package com.egco.storefinderproject.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinder.widget.ProductView;
import com.egco.storefinderproject.activity.ResultPageDetailActivity;
import com.egco.storefinderproject.constant.ApplicationConstant;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.Toast;

public class ProductAdapter extends BaseAdapter {

	private ArrayList<ProductModel> productList;
	private Context mContext;
	private int baseSize;
	private long percentile80;

	public ProductAdapter(Context context,int tBaseSize) {
		productList = new ArrayList<ProductModel>();
		this.mContext = context;
		this.baseSize = tBaseSize;
	}
	
	public void setProductList(ArrayList<ProductModel> productList) {
		this.productList = productList;
		
		long poularity[] = new long[productList.size()];
		for(int i=0;i<poularity.length;i++)
			poularity[i] = productList.get(i).getPopularity();
		Arrays.sort(poularity);
		percentile80 = poularity[(int)(Math.floor(0.8*poularity.length))];
		Log.v("TAGGGGGGGG", "popularity ======= "+percentile80);
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public ProductModel getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		return null;
	}

	public ProductModel getProductModelFromID(int Id) {
		for (int i = 0; i < productList.size(); i++) {
			if (productList.get(i).getProductID() == Id) {
				return productList.get(i);
			}
		}
		return null;
	}
	
	public ArrayList<View> getViewList() {
		ArrayList<View> viewList = new ArrayList<View>();
		for(int i=0;i<productList.size();i++) {
			ProductModel item = productList.get(i);
			ProductView productView = new ProductView(mContext);
			productView.setId(item.getProductID());
			
			// TODO: unmocking
			if(item.getPopularity() > percentile80) {
				productView.setProductImageByURL(item.getProductImgURLFullPath(), baseSize*2, baseSize*2);
				
				GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
				layoutParams.width = baseSize * 2;
				layoutParams.height = baseSize * 2;
				layoutParams.rowSpec = GridLayout.spec(Integer.MIN_VALUE, 2);
				layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 2);
				
				productView.setLayoutParams(layoutParams);
				productView.setBiggerFont();
			} else {
				productView.setProductImageByURL(item.getProductImgURLFullPath(), baseSize, baseSize);
				
				GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
				layoutParams.width = baseSize * 1;
				layoutParams.height = baseSize * 1;
				layoutParams.rowSpec = GridLayout.spec(Integer.MIN_VALUE, 1);
				layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, 1);
				
				productView.setLayoutParams(layoutParams);
			}
			productView.setPriceAndDiscount(item.getPrice(), item.getDiscount());
			productView.setShipping(item.getShippingCost());
			
			productView.setOnClickListener(onProductClickListener);
			viewList.add(productView);
		}
		return viewList;
	}
	
	OnClickListener onProductClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, ResultPageDetailActivity.class);
			intent.putExtra(ApplicationConstant.INTENT_PRODUCT_MODEL, getProductModelFromID(v.getId()));
			mContext.startActivity(intent);

		}
	};

}
