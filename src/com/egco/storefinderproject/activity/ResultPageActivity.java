package com.egco.storefinderproject.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.Toast;

import com.egco.storefinderproject.R;
import com.egco.storefinderproject.adapter.ProductAdapter;
import com.squareup.picasso.Picasso;

public class ResultPageActivity extends Activity {

	private ActionBar actionBar;
	private ProductAdapter productAdapter;
	private GridLayout gridLayout;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultpage);
		
		mContext = this;

		Picasso.with(this).setDebugging(true);

		gridLayout = (GridLayout) findViewById(R.id.result_gridLayout);
		
		initializeGridLayout();


//		for (int i = 0; i < viewList.size(); i++) {
//
//			View view = viewList.get(i);
//			GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//			
//			if(productAdapter.getProductModelFromID(view.getId()).getPopularity() > 800) {
//				ratio = 2;
//				Log.v("TAG", "Item " + (view.getId()) + " is big one");
//			} else {
//				ratio = 1;
//			}
//
//			layoutParams.width = baseSize * ratio;
//			layoutParams.height = baseSize * ratio;
//			layoutParams.rowSpec = GridLayout.spec(Integer.MIN_VALUE, ratio);
//			layoutParams.columnSpec = GridLayout.spec(Integer.MIN_VALUE, ratio);
//
//			view.setOnClickListener(onProductClickListener);
//			view.setLayoutParams(layoutParams);
//			
//			gridLayout.addView(view);
//		}

	}

	OnClickListener onProductClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(mContext, v.getId() + ": score=" + productAdapter.getProductModelFromID(v.getId()).getPopularity(), Toast.LENGTH_SHORT).show();

		}
	};
	
	private void initializeGridLayout(){
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels - (gridLayout.getPaddingLeft() + gridLayout.getPaddingRight());
		int column;
		
		if(width < 500) {
			column = 2;
		} else if (width < 801) {
			column = 3;
		} else if (width < 1201) {
			column = 4;
		} else if (width < 1601) {
			column = 5;
		} else {
			column = 6;
		}
			
		gridLayout.setColumnCount(column);
		productAdapter = new ProductAdapter(mContext, width/column);
	}

}
