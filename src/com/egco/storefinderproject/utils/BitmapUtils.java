package com.egco.storefinderproject.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class BitmapUtils {
	public static Bitmap addWatermark(Bitmap src, String watermark, int padding) {
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(src, 0, 0, null);

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setTextSize(14.0f);
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.SANS_SERIF);

		Rect textBounds = new Rect();
		paint.getTextBounds(watermark, 0, watermark.length(), textBounds);

		canvas.drawText(watermark, width - textBounds.width() - padding,
				2*padding, paint);

		return result;
	}
}
