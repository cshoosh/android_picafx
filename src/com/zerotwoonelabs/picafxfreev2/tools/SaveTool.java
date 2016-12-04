package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerotwoonelabs.picafxfreev2.R;

public class SaveTool extends BottomFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		v.findViewById(R.id.laySaveCancel).setVisibility(View.GONE);

		inflater.inflate(R.layout.layout_savetool,
				(ViewGroup) v.findViewById(R.id.laySimpleMain), true);
		
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.laySaveTool);

		for (int i = 0; i < layout.getChildCount(); i++) {
			layout.getChildAt(i).setOnClickListener(this);
			layout.getChildAt(i).setOnFocusChangeListener(this);
		}

		return v;
	}

	@Override
	public void adjust(Canvas canvas, Bitmap bmp) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtSaveBmp:
			getBMPHandler().askToSave(0, getActivity());
			break;
		case R.id.txtShareBmp:
			getBMPHandler().askToSave(1, getActivity());
			break;
		default:
			break;
		}
	}

	@Override
	public void onSaveClick() {

	}

	@Override
	public void onCancelClick() {

	}

	@Override
	public boolean onSingleTapUpImplement(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onScrollImplement(MotionEvent ev1, MotionEvent ev2,
			float distanceX, float distanceY) {
		return false;
	}

}
