package com.egco.storefinderproject.adapter;

import com.egco.storefinderproject.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreDetailPageSpinnerAdapter extends BaseAdapter{
	
	private LayoutInflater layoutInflater;
	Context mContext;
	final String spinnerList[] = {"Accessory","Shirt","Pants","Skirt","Shoes"};
	final int spinnerPic[] = {R.drawable.tie_32,R.drawable.shirt_32,R.drawable.trousers_32,R.drawable.skirt_32,R.drawable.shoe_man_32};
	
	public StoreDetailPageSpinnerAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.layoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return spinnerList.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.productstype_spinner_cell, null);
		}
		ImageView spinnerImg = (ImageView) convertView.findViewById(R.id.type_spinner_image);
		TextView spinnerText = (TextView) convertView.findViewById(R.id.type_spinner_text);
		
		spinnerImg.setImageResource(spinnerPic[position]);
		spinnerText.setText(spinnerList[position]);
		
		return convertView;
	}

}
