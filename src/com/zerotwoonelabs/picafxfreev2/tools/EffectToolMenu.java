package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Region.Op;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfree.support.OpenCV;
import com.zerotwoonelabs.picafxfreev2.R;

public class EffectToolMenu extends BottomFragment implements OnClickListener {

	private Effects mCurrentEffectSelected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup ret = (ViewGroup) super.onCreateView(inflater, container,
				savedInstanceState);

		// Additional Tools
		inflater.inflate(R.layout.layout_effecttool,
				(ViewGroup) ret.findViewById(R.id.laySimpleMain), true);

		// setSeekBarVisible(true);

		LinearLayout effects = (LinearLayout) ret.findViewById(R.id.layEffects);
		for (int i = 0; i < effects.getChildCount(); i++) {
			effects.getChildAt(i).setOnClickListener(this);
			effects.getChildAt(i).setOnFocusChangeListener(this);
		}

		return ret;
	}

	@Override
	public void onClick(View v) {
		setSeekBarVisible(true);
		switch (v.getId()) {
		case R.id.txtEffectPosterize:
			mCurrentEffectSelected = Effects.Poster;
			break;
		case R.id.txtEffectBokeh:
			mCurrentEffectSelected = Effects.Bokeh;
			break;
		case R.id.txtEffectBnW:
			mCurrentEffectSelected = Effects.BlackNWhite;
			break;
		case R.id.txtEffectEmboss:
			mCurrentEffectSelected = Effects.Emboss;
			break;
		case R.id.txtEffectGBlur:
			mCurrentEffectSelected = Effects.GaussianBlur;
			break;
		case R.id.txtEffectGlass:
			mCurrentEffectSelected = Effects.Glass;
			break;
		case R.id.txtEffectLBlur:
			mCurrentEffectSelected = Effects.LinearBlur;
			break;
		case R.id.txtEffectPixellate:
			mCurrentEffectSelected = Effects.Pixellatte;
			break;
		case R.id.txtEffectSBlur:
			mCurrentEffectSelected = Effects.SmartBlur;
			break;
		case R.id.txtEffectSharp:
			mCurrentEffectSelected = Effects.Sharp;
			break;
		case R.id.txtEffectStone:
			mCurrentEffectSelected = Effects.Stone;
			break;
		default:
			break;
		}
		updatesurface(true);
	}

	@Override
	public void adjust(Canvas canvas, Bitmap bmp) {

		Bitmap bitmap = process(bmp.copy(Config.ARGB_8888, true), 1);

		if (mCurrentPath != null && !mCurrentPath.isEmpty()) {
			canvas.drawPath(mCurrentPath, PathHistoryToolMenu.mPathPaint);

			if (isInversePath())
				canvas.clipPath(mCurrentPath, Op.DIFFERENCE);
			else
				canvas.clipPath(mCurrentPath);
		}

		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, null);
			bitmap.recycle();
		}
	}

	@Override
	public Bitmap process(Bitmap src, float ratio) {
		if (mCurrentEffectSelected != null)
			switch (mCurrentEffectSelected) {
			case BlackNWhite:
				return OpenCV.MakeBlacknWhite(src, mSeekValue);
			case Bokeh:
				return OpenCV.Dilate(src, mSeekValue, ratio);
			case Emboss:
				return OpenCV.Emboss(src, mSeekValue, false);
			case GaussianBlur:
				return OpenCV.Gaussian(src, mSeekValue, ratio);
			case Glass:
				return OpenCV.GlassEffect(src, mSeekValue, ratio);
			case LinearBlur:
				return OpenCV.BoxBlur(src, mSeekValue, ratio);
			case Pixellatte:
				return OpenCV.Pixelate(src, mSeekValue, ratio);
			case Poster:
				return OpenCV.Erode(src, mSeekValue, ratio);
			case Sharp:
				return OpenCV.Sharpen(src, mSeekValue, ratio);
			case SmartBlur:
				return OpenCV.LinearBlur(src, mSeekValue, ratio);
			case Stone:
				return OpenCV.Emboss(src, mSeekValue, true);
			default:
				break;
			}
		return super.process(src, ratio);
	}

	@Override
	public void onSaveClick() {
		BMPHandler handler = getBMPHandler();
		Bitmap bmp = Bitmap.createBitmap(handler.getWidth(),
				handler.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(handler.getBitmap(), 0, 0, null);
		System.gc();

		if (mCurrentPath != null && !mCurrentPath.isEmpty()) {
			Matrix m = new Matrix();
			// RectF bounds = new RectF();
			// mCurrentPath.computeBounds(bounds, true);

			m.setScale((float) handler.getWidth() / getBitmap().getWidth(),
					(float) handler.getHeight() / getBitmap().getHeight());

			Path copy = new Path(mCurrentPath);
			copy.transform(m);

			if (isInversePath())
				canvas.clipPath(copy, Op.DIFFERENCE);
			else
				canvas.clipPath(copy);
		}

		canvas.drawBitmap(
				process(handler.getBitmap(), handler.getWidth()
						/ getBitmap().getWidth()), 0, 0, null);
		mCurrentEffectSelected = null;
		handler.writeBitmapOnSameThread(bmp);

		System.gc();
	}

	@Override
	public void onCancelClick() {
		mCurrentEffectSelected = null;
		mSeekValue = 0;
		updatesurface(true);
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

	public enum Effects {
		Poster, Bokeh, BlackNWhite, Sharp, GaussianBlur, LinearBlur, SmartBlur, Stone, Emboss, Pixellatte, Glass
	}

}
