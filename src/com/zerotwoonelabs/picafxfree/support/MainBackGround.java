package com.zerotwoonelabs.picafxfree.support;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zerotwoonelabs.picafxfreev2.R;

public class MainBackGround extends RelativeLayout {

	private Path mString;
	private static final Point mDisplaySize = new Point();
	private static final Paint mStringPaint = new Paint(),mImagePaint = new Paint();
	private PathMeasure mStringPoints;
	private ArrayList<Bitmap> mImages;
	private ArrayList<Integer> mShufflePositions;
	private float[] mPoints1,mPoints2;
	private Matrix mImageTransform;
	
	public MainBackGround(Context context) {
		super(context);
		Initialize(context);
	}

	public MainBackGround(Context context, AttributeSet attrs) {
		super(context, attrs);
		Initialize(context);
	}

	public MainBackGround(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Initialize(context);
	}
	
	private void Initialize(Context context){
		setWillNotDraw(false);
			
		mString = new Path();
		
		mStringPaint.setAntiAlias(true);
		mStringPaint.setStrokeWidth(3f);
		mStringPaint.setColor(0xFFb8bec4);
		mStringPaint.setStyle(Style.STROKE);
		
		mImagePaint.setFilterBitmap(true);
		mImagePaint.setAntiAlias(true);
		
		mStringPoints = new PathMeasure();
		mImages = new ArrayList<Bitmap>();
		mImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.img01));
		mImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.img02));
		mImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.img03));
		mImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.img04));
		mImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.img05));
		
		mPoints1 = new float[2];
		mPoints2 = new float[2];
		
		mShufflePositions = new ArrayList<Integer>();
		for (int i = 0;i < mImages.size();i++)
			mShufflePositions.add(i);
		
		Collections.shuffle(mShufflePositions);
		mImageTransform = new Matrix();
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mDisplaySize.x = getWidth();
		mDisplaySize.y = getHeight();
		
		mString.reset();
		mString.moveTo(0, mDisplaySize.y/6);
		mString.quadTo(mDisplaySize.x/2, mDisplaySize.y/4, mDisplaySize.x, mDisplaySize.y/6);
		mString.quadTo(mDisplaySize.x/2, mDisplaySize.y/4, 0, mDisplaySize.y/6);
		
		mStringPoints.setPath(mString, true);
		canvas.drawPath(mString, mStringPaint);

		for (int i = 0;i < mImages.size();i++){
			mStringPoints.getPosTan(mStringPoints.getLength() * ((mShufflePositions.get(i)/(float)mImages.size()) * 0.5f), mPoints1, null);
			mStringPoints.getPosTan(mStringPoints.getLength() * (((mShufflePositions.get(i)/(float)mImages.size()) * 0.5f) + 0.05f), mPoints2, null);
		
			float degrees = (float) Math.atan2(-1 * (mPoints2[1] - mPoints1[1]), mPoints2[0] - mPoints1[0]);
			degrees = (float) (degrees * (180 / Math.PI));
			if (degrees < 0)
				degrees = 360 + degrees;
		
			mImageTransform.reset();
			mImageTransform.preTranslate(mPoints1[0], mPoints1[1] - 20f);
			mImageTransform.preRotate(-degrees);
		
			canvas.drawBitmap(mImages.get(i), mImageTransform, mImagePaint);
		}
	};

}
