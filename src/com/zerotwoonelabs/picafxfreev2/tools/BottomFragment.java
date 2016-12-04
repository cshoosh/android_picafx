package com.zerotwoonelabs.picafxfreev2.tools;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfreev2.PathArrayHandler;
import com.zerotwoonelabs.picafxfreev2.R;
import com.zerotwoonelabs.picafxfreev2.Static;
import com.zerotwoonelabs.picafxfreev2.UpdateMainActivity;
import com.zerotwoonelabs.picafxfreev2.dialogviews.NumberDialog;
import com.zerotwoonelabs.picafxfreev2.dialogviews.ProgressFrag;
import com.zerotwoonelabs.picafxfreev2.dialogviews.TipDialog;

public abstract class BottomFragment extends Fragment implements
		AdjustmentProcessor, OnGestureListener, OnTouchListener,
		OnFocusChangeListener, OnDoubleTapListener {

	public abstract void onSaveClick();

	public abstract void onCancelClick();

	public abstract boolean onSingleTapUpImplement(MotionEvent event);

	public abstract boolean onScrollImplement(MotionEvent ev1, MotionEvent ev2,
			float distanceX, float distanceY);

	protected GestureDetector mGesture;

	protected float left, top;
	protected Path mCurrentPath;
	protected int mSeekValue = 1;
	private boolean isSeekBarEnabled;

	protected void updatesurface(boolean withdialog) {
		((UpdateMainActivity) getActivity()).updatesurface(withdialog);

		left = ((UpdateMainActivity) getActivity()).getLeft();
		top = ((UpdateMainActivity) getActivity()).getTop();
		mCurrentPath = ((UpdateMainActivity) getActivity()).getCurrentPath();
	}

	protected void setSeekBarVisible(boolean show) {
		if (show && !Static.isScrollTipShown) {
			Bundle args = new Bundle();
			args.putString(TipDialog.KEY_MESSAGE, getActivity().getResources()
					.getString(R.string.tip_scroll));
			
			TipDialog diag = new TipDialog();
			diag.setArguments(args);
			diag.show(getFragmentManager(), "SCROLLTIP");
			
			Static.isScrollTipShown = true;
			PreferenceManager.getDefaultSharedPreferences(getActivity())
				.edit().putBoolean(Static.KEY_SCROLLTIP, true).commit();
		}
		
		isSeekBarEnabled = show;
	}

	protected ArrayList<PathArrayHandler> getPathList() {
		return ((UpdateMainActivity) getActivity()).getPathHandler();
	}

	protected boolean isInversePath() {
		return ((UpdateMainActivity) getActivity()).isInversePath();
	}

	protected void setCurrentPath(Path p) {
		((UpdateMainActivity) getActivity()).setCurrentPath(p);
	}

	protected BMPHandler getBMPHandler() {
		return ((UpdateMainActivity) getActivity()).getBMPHandler();
	}

	protected Bitmap getBitmap() {
		return ((UpdateMainActivity) getActivity()).getBitmap();
	}

	@Override
	public void onStart() {
		super.onStart();
		((UpdateMainActivity) getActivity()).registerAdjustmentLayer(this);
		updatesurface(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGesture = new GestureDetector(getActivity(), this);
		mGesture.setOnDoubleTapListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		MotionEvent ev = MotionEvent.obtain(event);
		ev.offsetLocation(-left, -top);
		boolean ret = mGesture.onTouchEvent(ev);
		ev.recycle();
		return ret;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		((UpdateMainActivity) getActivity()).setFullscreen();
		return true;
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		boolean result = onSingleTapUpImplement(e);
		if (!result)
			((UpdateMainActivity) getActivity()).setInverse();
		return result;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (isSeekBarEnabled) {
			NumberDialog diag = (NumberDialog) getActivity()
					.getFragmentManager().findFragmentByTag("NumberDialog");
			if (diag == null) {
				diag = new NumberDialog();
				diag.show(getFragmentManager(), "NumberDialog");
			}
			if (Math.abs(distanceY) > Math.abs(distanceX))
				mSeekValue += distanceY / 5;
			else
				mSeekValue += distanceX / 5;
			mSeekValue = mSeekValue > 100 ? 100 : mSeekValue <= 0 ? 1
					: mSeekValue;
			diag.setCount((int) mSeekValue);
			updatesurface(false);
		}
		return onScrollImplement(e1, e2, distanceX, distanceY);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public OnTouchListener getOnTouchListner() {
		return this;
	}

	@Override
	public Bitmap process(Bitmap src, float ratio) {
		return src;
	}

	private Runnable mProgressTask = new Runnable() {

		@Override
		public void run() {
			try {
				onSaveClick();
				updatesurface(false);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.frag_simple_bottom, container, false);

		View cancel = v.findViewById(R.id.txtCancel);
		View save = v.findViewById(R.id.txtSave);

		save.setOnClickListener(mClick);
		cancel.setOnClickListener(mClick);

		save.setOnFocusChangeListener(this);
		cancel.setOnFocusChangeListener(this);

		setSeekBarVisible(false);

		return v;
	}

	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.txtCancel:
				onCancelClick();

				/*
				 * Static.getTransaction(getActivity())
				 * .remove(BottomFragment.this).commit();
				 * 
				 * ((UpdateMainActivity) getActivity())
				 * .unregisterAdjustmentLayer(BottomFragment.this);
				 */
				v.setEnabled(false);
				new ProgressFrag(new Runnable() {
					@Override
					public void run() {
						try {
							getBMPHandler().undo();
							updatesurface(false);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
							return;
						}

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								v.setEnabled(true);
							}
						});
					}
				}).show(getFragmentManager(), null);

				break;
			case R.id.txtSave:
				new ProgressFrag(mProgressTask)
						.show(getFragmentManager(), null);

				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus && !getActivity().isChangingConfigurations()) {
			v.performClick();
		}
	}

	/*@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden)
			getActivity().findViewById(R.id.mainSurface).requestFocus();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().findViewById(R.id.mainSurface).requestFocus();
	}*/
}