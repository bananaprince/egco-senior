package com.egco.storefinder.widget;

import java.text.DecimalFormat;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.R.color;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
	private DecimalFormat df;

	public ProductView(Context context) {
		super(context);
		mContext = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.result_product_layout, this);
		df = new DecimalFormat("#.##");
		
		loadViews();
	}

	public ProductView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.result_product_layout, this);
		df = new DecimalFormat("#.##");

		loadViews();
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
	
	public void setProductImageByURL(String url) {
		Picasso.with(mContext).load(url).into(productImage);
	}
	
	public void setProductImageByURL(String url,int width,int height) {
		Picasso.with(mContext).load(url).resize(width, height).into(productImage);
	}
	
	public void setBiggerFont() {
		productDiscount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		productShipping.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	}
	
	public void setPriceAndDiscount(double price,double discount) {
		if(Math.abs(price-discount) < 0.01f) {
			// no discount
			productDiscount.setVisibility(View.GONE);
			productPrice.setText(df.format(price)+" B");
		} else {
			// some discount
			long percentage = Math.round((1 - (discount/price))*100);
			productDiscount.setText("-"+percentage+"%");
			Spannable spanText = Spannable.Factory.getInstance().newSpannable(df.format(discount)+" ("+df.format(price)+") B");
			spanText.setSpan(new ForegroundColorSpan(Color.RED), 0, df.format(discount).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanText.setSpan(new StrikethroughSpan(), df.format(discount).length()+2, spanText.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			productPrice.setText(spanText);
		}
	}
	
	public void setShipping(double shipping) {
		if(Math.abs(shipping) < 0.01f) {
			productShipping.setText("Free Shipping");
			productShipping.setTextColor(Color.GREEN);
		} else {
			productShipping.setText("+"+df.format(shipping)+" B Shipping");
		}
	}
}
