package com.egco.storefinderproject.utils;

import com.egco.storefinderproject.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

	public static final int STYLE_INFO_BLUE = 0;
	public static final int STYLE_WARNING_YELLOW = -1;
	public static final int STYLE_SUCCESS_GREEN = 1;
	public static final int STYLE_ERROR_RED = -2;

	private static final int ICON_QUESTION = R.drawable.question_75;
	private static final int ICON_CONFUSE = R.drawable.confused_75;
	private static final int ICON_HAPPY = R.drawable.happy_75;
	private static final int ICON_CRY = R.drawable.cry_75;

	private static final int BG_BLUE = R.drawable.round_toast_blue;
	private static final int BG_YELLOW = R.drawable.round_toast_yellow;
	private static final int BG_GREEN = R.drawable.round_toast_green;
	private static final int BG_RED = R.drawable.round_toast_red;

	public static final int DURATION_SHORT = Toast.LENGTH_SHORT;
	public static final int DURATION_LONG = Toast.LENGTH_LONG;

	public static Toast getToast(Context context, String text, int style,
			int duration) {
		View toastRoot = LayoutInflater.from(context).inflate(
				R.layout.custom_toast, null);
		TextView toastText = (TextView) toastRoot.findViewById(R.id.toast_text);
		ImageView toastImg = (ImageView) toastRoot
				.findViewById(R.id.toast_image);
		LinearLayout toastLayout = (LinearLayout) toastRoot
				.findViewById(R.id.toast_layout);

		toastText.setText(text);

		switch (style) {
		case STYLE_INFO_BLUE: {
			toastImg.setImageResource(ICON_QUESTION);
			toastLayout.setBackgroundResource(BG_BLUE);
			break;
		}
		case STYLE_WARNING_YELLOW: {
			toastImg.setImageResource(ICON_CONFUSE);
			toastLayout.setBackgroundResource(BG_YELLOW);
			break;
		}
		case STYLE_SUCCESS_GREEN: {
			toastImg.setImageResource(ICON_HAPPY);
			toastLayout.setBackgroundResource(BG_GREEN);
			break;
		}
		case STYLE_ERROR_RED: {
			toastImg.setImageResource(ICON_CRY);
			toastLayout.setBackgroundResource(BG_RED);
			break;
		}
		}

		Toast toast = new Toast(context);
		toast.setView(toastRoot);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		return toast;
	}
}
