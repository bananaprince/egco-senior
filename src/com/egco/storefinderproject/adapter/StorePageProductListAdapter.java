package com.egco.storefinderproject.adapter;

import java.util.ArrayList;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.activity.ProductDetailPageActivity;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StorePageProductListAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private Context mContext;
	private ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
	
	public StorePageProductListAdapter(Context mContext,
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
	public View getView(int position, View convertView, ViewGroup parentView) {
		
		ImageView productImg;
		TextView productName;
		TextView productPrice;
		ImageButton editButton;
		final ProductModel productItem;
		
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.productlistview_cell, null);
		}
		
		productImg = (ImageView) convertView.findViewById(R.id.cell_product_img);
		productName = (TextView) convertView.findViewById(R.id.cell_product_name);
		productPrice = (TextView) convertView.findViewById(R.id.cell_product_price);
		editButton = (ImageButton) convertView.findViewById(R.id.cell_editbutton);
		
		productItem = productList.get(position);
		
		Picasso.with(mContext).load(productItem.getProductImgURLFullPath()).placeholder(R.drawable.confused_75).error(R.drawable.cry_75).into(productImg);
		productName.setText(productItem.getProductName());
		productPrice.setText(Double.toString(productItem.getPrice()));
		
		editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, ProductDetailPageActivity.class);
				intent.putExtra(ApplicationConstant.INTENT_ACTION, ApplicationConstant.ACTION_PRODUCT_DETAIL_EDIT);
				intent.putExtra(ApplicationConstant.INTENT_PRODUCT_MODEL, productItem);
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}
}
