package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View.OnTouchListener;

public interface AdjustmentProcessor {
	public abstract void adjust(Canvas canvas, Bitmap bmp);
	public abstract Bitmap process (Bitmap src, float ratio);
	public abstract OnTouchListener getOnTouchListner();
}
