package com.zerotwoonelabs.picafxfreev2.tools;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zerotwoonelabs.picafxfree.support.OpenCV;
import com.zerotwoonelabs.picafxfreev2.PathArrayHandler;
import com.zerotwoonelabs.picafxfreev2.R;
import com.zerotwoonelabs.picafxfreev2.Static;
import com.zerotwoonelabs.picafxfreev2.UpdateMainActivity;
import com.zerotwoonelabs.picafxfreev2.dialogviews.TipDialog;

public class PenToolMenu extends BottomFragment implements OnClickListener {
	private static final int TOOL_LINEAR = 0;
	private static final int TOOL_MAGIC = 1;
	//private static final int TOOL_CURVE = 2;

	/*private static final Paint LINEAR_PAINT = new Paint();

	static {
		LINEAR_PAINT.setColor(0xFF000000);
		LINEAR_PAINT.setStyle(Style.STROKE);
		LINEAR_PAINT.setStrokeWidth(4f);
		LINEAR_PAINT.setStrokeJoin(Join.ROUND);
		LINEAR_PAINT.setStrokeCap(Cap.ROUND);
	}*/
	
	private ArrayList<Float> mXArray = new ArrayList<Float>();
	private ArrayList<Float> mYArray = new ArrayList<Float>();
	private ArrayList<Integer> mAction = new ArrayList<Integer>();

	private int mCurrentToolSelected = TOOL_LINEAR;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View ret = super.onCreateView(inflater, container, savedInstanceState);
		inflater.inflate(R.layout.layout_pentool,
				(ViewGroup) ret.findViewById(R.id.laySimpleMain), true);
		LinearLayout laypen = (LinearLayout) ret.findViewById(R.id.layPenTool);
		for (int i = 0;i < laypen.getChildCount();i++){
			laypen.getChildAt(i).setOnClickListener(this);
			laypen.getChildAt(i).setOnFocusChangeListener(this);
		}
		return ret;
	}


	@Override
	public void adjust(Canvas canvas, Bitmap bmp) {
		
			Path pathmagic = new Path();
			if (mXArray.size() > 1) {

				pathmagic.moveTo(mXArray.get(0), mYArray.get(0));
				for (int i = 1; i < mXArray.size(); i++) {
					if (mAction.get(i) == 1)
						pathmagic.lineTo(mXArray.get(i), mYArray.get(i));
					else if (mAction.get(i) == 0)
						pathmagic.moveTo(mXArray.get(i), mYArray.get(i));
				}
				// path.close();
			}

			canvas.drawPath(pathmagic, PathHistoryToolMenu.mPathPaint);
			
	}	

	@Override
	public void onClick(View v) {
		setSeekBarVisible(false);
		switch (v.getId()) {
		case R.id.txtPenLinear:
			mCurrentToolSelected = TOOL_LINEAR;
			break;
		case R.id.txtPenMagic:
			mCurrentToolSelected = TOOL_MAGIC;
			setSeekBarVisible(true);
			break;
		default:
			break;
		}
		onCancelClick();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (!Static.isPenTipShown) {
			Bundle args = new Bundle();
			args.putString(TipDialog.KEY_MESSAGE, getActivity().getResources()
					.getString(R.string.tip_pentool));
			
			TipDialog diag = new TipDialog();
			diag.setArguments(args);
			diag.show(getFragmentManager(), "PENTIP");
			
			Static.isPenTipShown = true;
			PreferenceManager.getDefaultSharedPreferences(getActivity())
				.edit().putBoolean(Static.KEY_PENTIP, true).commit();
		}
	}
	@Override
	public void onSaveClick() {
		if (!mXArray.isEmpty()) {
			
			PathArrayHandler handler = new PathArrayHandler(mXArray, mYArray, mAction);
			((UpdateMainActivity)getActivity()).getPathHandler().add(handler);
			((UpdateMainActivity)getActivity()).setCurrentPath(handler.getNewPath());
			updatesurface(false);
		}
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getActivity(), "Selection Saved in Pen History", Toast.LENGTH_SHORT)
				.show();
			}
		});
		onCancelClick();
	}

	@Override
	public void onCancelClick() {
		mXArray.clear();
		mYArray.clear();
		mAction.clear();
		
		updatesurface(false);
	}

	@Override
	public boolean onSingleTapUpImplement(MotionEvent event) {
		//event.offsetLocation(-left, -top);
		
		float x = event.getX();
		float y = event.getY();

		switch (mCurrentToolSelected) {
		case TOOL_LINEAR:
			if (mXArray.isEmpty())
				mAction.add(0);
			else
				mAction.add(1);
			
			mXArray.add(x);
			mYArray.add(y);
			
			updatesurface(false);
			break;
		case TOOL_MAGIC:
			mXArray.clear();
			mYArray.clear();
			mAction.clear();

			OpenCV.mapMagicTool(getBitmap(), x, y, mSeekValue, mSeekValue, 
					mXArray, mYArray, mAction);
			updatesurface(false);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onScrollImplement(MotionEvent ev1, MotionEvent ev2,
			float distanceX, float distanceY) {
		
		
		float x = ev2.getX();
		float y = ev2.getY();
		if (mCurrentToolSelected == TOOL_LINEAR && (ev2.getAction() == MotionEvent.ACTION_DOWN || 
				ev2.getAction() == MotionEvent.ACTION_MOVE)) {
			if (mXArray.isEmpty())
				mAction.add(0);
			else
				mAction.add(1);
			
			mXArray.add(x);
			mYArray.add(y);
			updatesurface(false);
		}			
		return true;
	}
}
