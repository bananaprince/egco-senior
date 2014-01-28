package com.egco.storefinderproject.activity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.edmodo.cropper.CropImageView;
import com.egco.storefinderproject.R;
import com.egco.storefinderproject.constant.ApplicationConstant;
import com.egco.storefinderproject.utils.ToastUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageCropperPageActivity extends Activity {

	private Context mContext;
	private CropImageView cropImageView;
	private Button submitButton;
	private ProgressBar progressBar;
	private TextView instruction;

	private final int minimumImageSize = 256;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagecropperpage);
		getActionBar().hide();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		mContext = this;

		cropImageView = (CropImageView) findViewById(R.id.imagecropperpage_image);
		submitButton = (Button) findViewById(R.id.imagecropperpage_submit_button);
		instruction = (TextView) findViewById(R.id.imagecropperpage_instruction);

		progressBar = (ProgressBar) findViewById(R.id.imagecropperpage_progressbar);
		progressBar.setVisibility(View.GONE);
		
		cropImageView.setFixedAspectRatio(true);
		cropImageView.setAspectRatio(1, 1);
		cropImageView.setGuidelines(2);
		
		instruction.setOnClickListener(onCropImageViewClick);
	}
	
	OnClickListener onCropImageViewClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			photoPicker();
			
		}
	};

	private void photoPicker() {
		// Intent to get image resource

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent,
				ApplicationConstant.REQUEST_CODE_PHOTO_PICKER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ApplicationConstant.REQUEST_CODE_PHOTO_PICKER) {

			if (resultCode == RESULT_OK) {
				
				instruction.setOnClickListener(null);
				instruction.setText(R.string.imagecropperpage_instruction2);

				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();

				Uri selectedImage = data.getData();

				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Bitmap selectImageBitmap = BitmapFactory
						.decodeStream(imageStream);

				Log.v("ImageCropperPage", "original image file size (w x h) = "
						+ selectImageBitmap.getWidth() + " x "
						+ selectImageBitmap.getHeight());

				// scaled image
				if (selectImageBitmap.getWidth() > 512
						|| selectImageBitmap.getHeight() > 512) {
					int largerDim = Math.max(selectImageBitmap.getWidth(),
							selectImageBitmap.getHeight());
					double scale = 512.0f / (double) largerDim;

					selectImageBitmap = Bitmap.createScaledBitmap(
							selectImageBitmap,
							(int) (selectImageBitmap.getWidth() * scale),
							(int) (selectImageBitmap.getHeight() * scale),
							false);
				} else if (selectImageBitmap.getWidth() < 256
						|| selectImageBitmap.getHeight() < 256) {
					// do something
				}

				Log.v("ImageCropperPage", "resize image file size (w x h) = "
						+ selectImageBitmap.getWidth() + " x "
						+ selectImageBitmap.getHeight());

				cropImageView.setImageBitmap(selectImageBitmap);
				submitButton.setOnClickListener(onSubmitButtonClick);

				progressBar.setVisibility(View.GONE);
			} else {
				
				Log.v("TAG", "get image failed");
				ToastUtils.getToast(mContext, "Cannot get image", ToastUtils.STYLE_ERROR_RED, ToastUtils.DURATION_SHORT).show();
			}
		}

	}

	OnClickListener onSubmitButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();

			Bitmap croppedImage = cropImageView.getCroppedImage();

			Intent intent = new Intent();
			intent.putExtra(ApplicationConstant.INTENT_CROPPED_IMAGE,
					croppedImage);
			setResult(RESULT_OK, intent);
			progressBar.setVisibility(View.GONE);
			finish();
		}
	};

}
