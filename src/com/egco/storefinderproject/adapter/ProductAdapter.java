package com.egco.storefinderproject.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinder.widget.ProductView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ProductAdapter extends BaseAdapter {

	private ArrayList<ProductModel> productList;
	private Context mContext;
	private int baseSize;

	public ProductAdapter(Context context,int tBaseSize) {
		productList = new ArrayList<ProductModel>();
		this.mContext = context;
		this.baseSize = tBaseSize;
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

//	private ArrayList<View> convertModelToView() {
//
//		ArrayList<View> tempViewList = new ArrayList<View>();
//
//		for (int i = 0; i < 30; i++) {
//			ProductModel productModel = tempList.get(i);
//			ProductView productView = new ProductView(mContext);
//			productView.setProductPriceAndDiscount(productModel.getPrice(),
//					productModel.getDiscount());
//			productView.setProductShippingValue(productModel.getShippingCost());
//			productView.setProductImageByURL(productModel.getProductImgURL());
//			productView.setProductPropularity(productModel.getPopularity());
//			productView.setId(productModel.getProductID());
//			tempViewList.add(productView);
//		}
//
//		return tempViewList;
//	}

}
