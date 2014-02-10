package com.egco.storefinderproject.adapter;

import java.util.ArrayList;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.activity.ResultPageDetailActivity;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	private Context mContext;
	private ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
	
	public HistoryListAdapter(Context mContext,
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
		TextView productPrice;
		final ProductModel productItem;
		
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.productlistview_cell_his, null);
		}
		
		productImg = (ImageView) convertView.findViewById(R.id.cell_his_product_img);
		productName = (TextView) convertView.findViewById(R.id.cell_his_product_name);
		productPrice = (TextView) convertView.findViewById(R.id.cell_his_product_price);
		
		productItem = productList.get(position);
		
		Picasso.with(mContext).load(productItem.getProductImgURLFullPath()).placeholder(R.drawable.confused_75).error(R.drawable.cry_75).into(productImg);
		productName.setText(productItem.getProductName());
		productPrice.setText(Double.toString(productItem.getPrice()));
		
		return convertView;
	}
}
