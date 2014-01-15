package com.egco.storefinder.widget;

import java.text.DecimalFormat;

import com.egco.storefinderproject.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProductView extends RelativeLayout {

	private ImageView productImage;
	private TextView productDiscount;
	private TextView productPrice;
	private TextView productShipping;
	private Context mContext;
	private long productPopularity;

	public ProductView(Context context) {
		super(context);
		mContext = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.result_product_layout, this);
		
		loadViews();
		productPopularity = 0;
	}

	public ProductView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.result_product_layout, this);

		loadViews();
		productPopularity = 0;
	}
	
	private void init() {
		productImage = new ImageView(mContext);
		productDiscount = new TextView(mContext);
		productPrice = new TextView(mContext);
		productShipping = new TextView(mContext);
	}

	private void loadViews() {
		productImage = (ImageView) findViewById(R.id.product_image);
		productDiscount = (TextView) findViewById(R.id.product_discount);
		productPrice = (TextView) findViewById(R.id.product_price);
		productShipping = (TextView) findViewById(R.id.product_shipping);
	}
	
	public void setProductPropularity(long popularity) {
		productPopularity = popularity;
	}
	
	public void setProductImageByURL(String url) {
		Picasso.with(mContext).load(url).into(productImage);
	}
	
	public void setProductImageByURL(String url,int width,int height) {
		Picasso.with(mContext).load(url).resize(width, height).into(productImage);
	}
	
	public void setProductPriceAndDiscount(double price,double discount){
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuilder discountSB = new StringBuilder();
		StringBuilder priceSB = new StringBuilder();
		
		discountSB.append("-");
		discountSB.append(Math.round(discount));
		discountSB.append(" %");
		this.setProductDiscountText(discountSB.toString());
		
		price *= (1-(discount/100));
		priceSB.append(df.format(price));
		priceSB.append(" B");
		this.setProductPriceText(priceSB.toString());
	}
	
	private void setProductDiscountText(String text) {
		productDiscount.setText(text);
	}
	
	private void setProductPriceText(String text) {
		productPrice.setText(text);
	}
	
	private void setProductShippingText(String text) {
		productShipping.setText(text);
	}
	
	public void setProductShippingValue(double shipping) {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuilder sb = new StringBuilder();
		sb.append("+ ");
		sb.append(df.format(shipping));
		sb.append(" B Shipping");
		this.setProductShippingText(sb.toString());
	}



}
