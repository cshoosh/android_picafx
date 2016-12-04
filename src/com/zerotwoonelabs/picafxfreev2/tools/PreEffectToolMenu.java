package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Region.Op;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
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

public class PreEffectToolMenu extends BottomFragment implements
		OnClickListener {

	private PreEffect mCurrentEffect;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup ret = (ViewGroup) super.onCreateView(inflater, container,
				savedInstanceState);

		// Additional Tools
		inflater.inflate(R.layout.layout_preeffecttool,
				(ViewGroup) ret.findViewById(R.id.laySimpleMain), true);

		LinearLayout preeffects = (LinearLayout) ret
				.findViewById(R.id.layPreEffects);

		for (int i = 0; i < preeffects.getChildCount(); i++) {
			preeffects.getChildAt(i).setOnClickListener(this);
			preeffects.getChildAt(i).setOnFocusChangeListener(this);
		}

		return ret;
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

		float progvalue = 0.8f * (mSeekValue / 100f) + 0.1f;
		if (mCurrentEffect != null)
			switch (mCurrentEffect) {
			case BlueToRed:
				float[] brfilter = { 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0,
						0, 0, 0, 0, 1, 0 };

				return OpenCV.RGBAdjust(src, brfilter);
			case BlueTone:
				float[] btone = { progvalue, 0, 0, 0, 0, 0, progvalue, 0, 0, 0,
						0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
				return OpenCV.RGBAdjust(src, btone);
			case GreenToBlue:
				float[] bgfilter = { 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0,
						0, 0, 0, 0, 1, 0 };

				return OpenCV.RGBAdjust(src, bgfilter);
			case GreenTone:
				float[] gtone = { progvalue, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
						progvalue, 0, 0, 0, 0, 0, 1, 0 };
				return OpenCV.RGBAdjust(src, gtone);

			case GreyScale:
				return OpenCV.GreyScale(src);
			case RedToGreen:
				float[] rgfilter = { 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0,
						0, 0, 0, 0, 1, 0 };

				return OpenCV.RGBAdjust(src, rgfilter);
			case RedTone:
				float[] rtone = { 1f, 0, 0, 0, 0, 0, progvalue, 0, 0, 0, 0, 0,
						progvalue, 0, 0, 0, 0, 0, 1, 0 };
				return OpenCV.RGBAdjust(src, rtone);
			case Sepia:
				float[] sepia = { 0.393f, 0.769f, 0.189f, 0, 0, 0.349f, 0.686f,
						0.168f, 0, 0, 0.272f, 0.534f, 0.131f, 0, 0, 0, 0, 0, 1,
						0 };

				return OpenCV.RGBAdjust(src, sepia);
			case YellowTone:
				float[] ytone = { 1f, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
						progvalue, 0, 0, 0, 0, 0, 1, 0 };
				return OpenCV.RGBAdjust(src, ytone);
			case Invert:
				float[] invert = { -1, 0, 0, 0, 255, 0, -1, 0, 0, 255, 0, 0,
						-1, 0, 255, 0, 0, 0, 1, 0 };
				return OpenCV.RGBAdjust(src, invert);
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
			RectF bounds = new RectF();

			mCurrentPath.computeBounds(bounds, true);
			m.setScale((float) handler.getWidth() / getBitmap().getWidth(),
					(float) handler.getHeight() / getBitmap().getHeight());

			Path copy = new Path(mCurrentPath);
			copy.transform(m);

			if (isInversePath())
				canvas.clipPath(copy, Op.DIFFERENCE);
			else
				canvas.clipPath(copy);
		}

		canvas.drawBitmap(process(handler.getBitmap()
				, handler.getWidth() / getBitmap().getWidth()), 0, 0, null);
		mCurrentEffect = null;
		handler.writeBitmapOnSameThread(bmp);

		System.gc();
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

	public enum PreEffect {
		Sepia, RedTone, GreenTone, BlueTone, YellowTone, GreyScale, RedToGreen, GreenToBlue, BlueToRed, Invert
	}

	@Override
	public void onClick(View v) {
		setSeekBarVisible(false);

		switch (v.getId()) {
		case R.id.txtPreEffectSepia:
			mCurrentEffect = PreEffect.Sepia;
			break;
		case R.id.txtPreEffectRedToBlue:
			mCurrentEffect = PreEffect.BlueToRed;
			break;
		case R.id.txtPreEffectRedToGreen:
			mCurrentEffect = PreEffect.RedToGreen;
			break;
		case R.id.txtPreEffectBlueTone:
			mCurrentEffect = PreEffect.BlueTone;
			setSeekBarVisible(true);
			break;
		case R.id.txtPreEffectGreenTone:
			mCurrentEffect = PreEffect.GreenTone;
			setSeekBarVisible(true);
			break;
		case R.id.txtPreEffectGreyscale:
			mCurrentEffect = PreEffect.GreyScale;
			break;
		case R.id.txtPreEffectInvert:
			mCurrentEffect = PreEffect.Invert;
			break;
		case R.id.txtPreEffectRedTone:
			mCurrentEffect = PreEffect.RedTone;
			setSeekBarVisible(true);
			break;
		case R.id.txtPreEffectYellowTone:
			mCurrentEffect = PreEffect.YellowTone;
			setSeekBarVisible(true);
			break;
		case R.id.txtPreEffectBlueToGreen:
			mCurrentEffect = PreEffect.GreenToBlue;
			break;
		default:
			break;
		}
		updatesurface(true);
	}

}