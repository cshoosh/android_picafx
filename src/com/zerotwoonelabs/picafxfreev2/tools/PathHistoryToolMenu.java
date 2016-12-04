package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zerotwoonelabs.picafxfreev2.PathArrayHandler;
import com.zerotwoonelabs.picafxfreev2.R;

public class PathHistoryToolMenu extends BottomFragment implements
		OnClickListener {

	public static final Paint mPathPaint = new Paint();
	private static final Paint mRectPaint = new Paint();
	static {
		mPathPaint.setColor(0xffffffff);
		mPathPaint.setStyle(Style.STROKE);
		mPathPaint.setStrokeWidth(3f);
		mPathPaint.setStrokeCap(Cap.ROUND);
		mPathPaint.setStrokeJoin(Join.BEVEL);
		
		mRectPaint.setColor(0xFFFFFFFF);
		mRectPaint.setStyle(Style.STROKE);
		mRectPaint.setStrokeWidth(4);
	}

	private Path mInitialPath;
	private LinearLayout mView;
	private int mPosition;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		v.findViewById(R.id.txtSave).setVisibility(View.GONE);
		v.findViewById(R.id.txtDelete).setVisibility(View.VISIBLE);

		v.findViewById(R.id.txtDelete).setOnClickListener(this);
		v.findViewById(R.id.txtDelete).setOnFocusChangeListener(this);

		mInitialPath = mCurrentPath;

		ViewGroup main = (ViewGroup) v.findViewById(R.id.laySimpleMain);
		inflater.inflate(R.layout.layout_pathhistory, main, true);

		mView = (LinearLayout) main.findViewById(R.id.gridview);

		for (int i = 0; i < getPathList().size(); i++) {
			mView.addView(getView(inflater, container, i));
		}

		return v;
	}


	@Override
	public void adjust(Canvas canvas, Bitmap bmp) {
		if (mCurrentPath != null && !mCurrentPath.isEmpty())
			canvas.drawPath(mCurrentPath, PathHistoryToolMenu.mPathPaint);
	}

	@Override
	public void onSaveClick() {

	}

	@Override
	public void onCancelClick() {
		setCurrentPath(mInitialPath);
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

	public View getView(LayoutInflater inflater, ViewGroup parent, int position) {
		TextView inflated = (TextView) inflater.inflate(R.layout.text_view,
				parent, false);

		int stdWidth = (int) getResources().getDimension(R.dimen.icon_size);

		Path path = PathArrayHandler.getMiniPath(getPathList().get(position)
				.getPath(), getActivity());

		Bitmap bmp = Bitmap.createBitmap(stdWidth, stdWidth, Config.ARGB_8888);

		Canvas c = new Canvas(bmp);

		c.drawColor(Color.TRANSPARENT);
		c.drawPath(path, PathHistoryToolMenu.mPathPaint);
		c.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), mRectPaint);

		Drawable d = new BitmapDrawable(getResources(), bmp);

		inflated.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
		inflated.setText("" + position);

		inflated.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = Integer.valueOf((String) ((TextView) v)
						.getText());
				PathArrayHandler handler = getPathList().get(position);

				setCurrentPath(handler.getPath());
				mPosition = position;

				updatesurface(false);
			}
		});
		
		inflated.setOnFocusChangeListener(this);
		return inflated;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtDelete:
			// TODO
			if (mView != null) {
				if (mPosition > 0
						&& mPosition < getPathList().size()) {
					getPathList().remove(mPosition);
					mView.removeViewAt(mPosition);
					setCurrentPath(getPathList().get(0).getPath());
					updatesurface(false);
					mPosition = -1;
				}
			}
			break;

		default:
			break;
		}
		updatesurface(false);
	}
}
