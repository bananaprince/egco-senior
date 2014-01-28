package com.egco.storefinderproject.activity;

import java.text.DecimalFormat;

import com.egco.storefinder.model.ProductModel;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultPageDetailActivity extends Activity{
	
	private Context mContext;
	
	private ProgressBar progressBar;
	private ImageView productImage;
	private TextView productName;
	private TextView productPrice;
	private TextView productShipping;
	private TextView productDescription;
	
	private ProductModel selectedItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultdetailpage);
		
		mContext = this;

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		getActionBar().hide();
		
		selectedItem = (ProductModel) getIntent().getSerializableExtra(ApplicationConstant.INTENT_PRODUCT_MODEL);
		
		progressBar = (ProgressBar) findViewById(R.id.resultdetailpage_progressbar);
		productImage = (ImageView) findViewById(R.id.resultdetailpage_productimage);
		productName = (TextView) findViewById(R.id.resultdetailpage_productname);
		productPrice = (TextView) findViewById(R.id.resultdetailpage_productprice);
		productShipping = (TextView) findViewById(R.id.resultdetailpage_productshipping);
		productDescription = (TextView) findViewById(R.id.resultdetailpage_productdescription);
		
		progressBar.setVisibility(View.GONE);
		
		initDetail();
	}
	
	private void initDetail() {
		DecimalFormat df = new DecimalFormat("#.##");
		
		double productPriceVal = selectedItem.getPrice();
		double discountPriceVal = selectedItem.getDiscount();
		double shippingPriceVal = selectedItem.getShippingCost();
		
		Picasso.with(mContext).load(selectedItem.getProductImgURLFullPath()).into(productImage);
		productName.setText(selectedItem.getProductName());
		if(Math.abs(productPriceVal-discountPriceVal) > 0.01f) {
			Spannable spanText = Spannable.Factory.getInstance().newSpannable(df.format(discountPriceVal)+" ("+df.format(productPriceVal)+") B");
			spanText.setSpan(new ForegroundColorSpan(Color.RED), 0, df.format(discountPriceVal).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanText.setSpan(new StrikethroughSpan(), df.format(discountPriceVal).length()+2, spanText.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			productPrice.setText(spanText);
		} else {
			productPrice.setText(df.format(productPriceVal));
		}
		if(shippingPriceVal < 0.01f) {
			productShipping.setText("Free Shipping");
			productShipping.setTextColor(Color.GREEN);
		} else {
			productShipping.setText(df.format(selectedItem.getShippingCost()));
		}
		productDescription.setText(selectedItem.getDescription());
	}

}
