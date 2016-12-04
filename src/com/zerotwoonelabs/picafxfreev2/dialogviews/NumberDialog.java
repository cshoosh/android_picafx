package com.zerotwoonelabs.picafxfreev2.dialogviews;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zerotwoonelabs.picafxfreev2.R;

public class NumberDialog extends DialogFragment implements Runnable {

	private TextView mView;
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		setStyle(STYLE_NO_TITLE, 0);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = (TextView) inflater.inflate(R.layout.number_dialog, container,
				false);
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setCount(0);
	}

	@Override public void onStart() {
	    super.onStart();

	    Window window = getDialog().getWindow();
	    WindowManager.LayoutParams windowParams = window.getAttributes();
	    windowParams.dimAmount = 0;
	    windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
	    window.setAttributes(windowParams);
	}
	
	public void setCount(int count) {
		if (mHandler != null && mView != null) {
			mHandler.removeCallbacks(this);
			mView.setText("" + count);
			mHandler.postDelayed(this, 500);
		}
	}

	@Override
	public void run() {
		dismiss();
	}
}
