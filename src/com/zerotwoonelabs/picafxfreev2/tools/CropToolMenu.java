package com.zerotwoonelabs.picafxfreev2.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfreev2.UpdateMainActivity;

public class CropToolMenu extends BottomFragment {
	
	private static final Paint mRectPaint = new Paint();
	private static final Paint mCirclePaint = new Paint();
	
	private static final RectF mRect = new RectF();
	
	private static final int EVENT_PADDING = 18;
	
	private CropToolState mState = CropToolState.Inside;
		
	static{
		mRectPaint.setColor(0xFF000000);
		mRectPaint.setStyle(Style.STROKE);
		mRectPaint.setStrokeCap(Cap.SQUARE);
		mRectPaint.setStrokeJoin(Join.BEVEL);
		mRectPaint.setStrokeWidth(5f);
		
		mCirclePaint.setColor(0xFF7Ddd00);
		mCirclePaint.setStyle(Style.FILL);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setShadowLayer(5, 1, 1, 0xFF7D7D7D);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mRect.set(20, 20, getBitmap().getWidth() - 20,
				getBitmap().getHeight() - 20);
		} catch (NullPointerException e) {}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup ret = (ViewGroup) super.onCreateView(inflater, container,
				savedInstanceState);
		return ret;
	}
	
	@Override
	public void adjust(Canvas canvas, Bitmap bmp) {		
		canvas.drawRect(mRect, mRectPaint);
		
		canvas.drawCircle(mRect.left, mRect.top, EVENT_PADDING, mCirclePaint);
		canvas.drawCircle(mRect.left, mRect.bottom, EVENT_PADDING, mCirclePaint);
		canvas.drawCircle(mRect.right, mRect.top, EVENT_PADDING, mCirclePaint);
		canvas.drawCircle(mRect.right, mRect.bottom, EVENT_PADDING, mCirclePaint);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		MotionEvent ev = MotionEvent.obtain(event);
		ev.offsetLocation(-left,-top);
		
		if (ev.getAction() == MotionEvent.ACTION_DOWN){
			float x = ev.getX(), y = ev.getY();
			
			if (new RectF(mRect.left - EVENT_PADDING, mRect.top - EVENT_PADDING,
					mRect.left + EVENT_PADDING, mRect.top + EVENT_PADDING).contains(x, y))
				mState = CropToolState.TopLeft;
			else if (new RectF(mRect.right - EVENT_PADDING, mRect.top - EVENT_PADDING,
					mRect.right + EVENT_PADDING, mRect.top + EVENT_PADDING).contains(x, y))
				mState = CropToolState.TopRight;
			else if (new RectF(mRect.left - EVENT_PADDING, mRect.bottom - EVENT_PADDING,
					mRect.left + EVENT_PADDING, mRect.bottom + EVENT_PADDING).contains(x, y))
				mState = CropToolState.BottomLeft;
			else if (new RectF(mRect.right - EVENT_PADDING, mRect.bottom - EVENT_PADDING,
					mRect.right + EVENT_PADDING, mRect.bottom + EVENT_PADDING).contains(x, y))
				mState = CropToolState.BottomRight;
			else if (new RectF(mRect.left - EVENT_PADDING, mRect.top + EVENT_PADDING
					,mRect.left + EVENT_PADDING,mRect.bottom - EVENT_PADDING).contains(x,y))
				mState = CropToolState.Left;
			else if (new RectF(mRect.right - EVENT_PADDING, mRect.top + EVENT_PADDING
					,mRect.right + EVENT_PADDING,mRect.bottom - EVENT_PADDING).contains(x,y))
				mState = CropToolState.Right;
			else if (new RectF(mRect.left + EVENT_PADDING, mRect.top - EVENT_PADDING
					,mRect.right - EVENT_PADDING,mRect.top + EVENT_PADDING).contains(x,y))
				mState = CropToolState.Top;
			else if (new RectF(mRect.left + EVENT_PADDING, mRect.bottom - EVENT_PADDING
					,mRect.right - EVENT_PADDING,mRect.bottom + EVENT_PADDING).contains(x,y))
				mState = CropToolState.Bottom;
			else if (mRect.contains(x, y))
				mState = CropToolState.Inside;
			
			else
				mState = CropToolState.Outside;			
		}
		
		if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() 
				== MotionEvent.ACTION_UP)
			mState = CropToolState.Inside;
		
		ev.recycle();

		return super.onTouch(v, event);
	}

	@Override
	public void onSaveClick() {
		RectF surfacebounds = new RectF(0, 0, ((UpdateMainActivity)getActivity()).
				getBitmap().getWidth(),((UpdateMainActivity)getActivity()).getBitmap()
				.getHeight());
		
		RectF result = new RectF(
				mRect.left < 0 || mRect.left > surfacebounds.width() ? 0 : mRect.left,
				mRect.top < 0 || mRect.top > surfacebounds.height() ? 0 : mRect.top,
				mRect.right > surfacebounds.width() || mRect.right < 0 ? 
						surfacebounds.width() : mRect.right,
				mRect.bottom > surfacebounds.height() || mRect.bottom < 0 ? 
						surfacebounds.height() : mRect.bottom);
		
		BMPHandler handler = getBMPHandler();
				
		float xRatio = handler.getWidth() / surfacebounds.width();
		float yRatio = handler.getHeight() / surfacebounds.height();
		
		Bitmap bmpCopy = Bitmap.createBitmap(handler.getBitmap(), 
				(int) (xRatio * result.left), (int) (yRatio * result.top), 
				(int) (xRatio * result.width()), (int) (yRatio * result.height()));
		
		handler.writeBitmapOnSameThread(bmpCopy);
		mRect.offsetTo(20, 20);
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
		switch (mState) {
		case BottomLeft:
			mRect.bottom -= distanceY;
			mRect.left -= distanceX;
			break;
		case BottomRight:
			mRect.right -= distanceX;
			mRect.bottom -= distanceY;
			break;
		case Inside:
			mRect.offset(-distanceX, -distanceY);
			break;
		case Outside:
			break;
		case TopLeft:
			mRect.left -= distanceX;
			mRect.top -= distanceY;
			break;
		case TopRight:
			mRect.top -= distanceY;
			mRect.right -= distanceX;
			break;
		case Bottom:
			mRect.bottom -= distanceY;
			break;
		case Left:
			mRect.left -= distanceX;
			break;
		case Right:
			mRect.right -= distanceX;
			break;
		case Top:
			mRect.top -= distanceY;
			break;
		default:
			break;
		
		}
		updatesurface(false);
		return true;
	}
	
	private enum CropToolState{
		TopLeft, TopRight, BottomLeft, BottomRight, Inside, Outside, Left,
		Right, Top, Bottom
	}	
}
