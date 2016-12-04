package com.zerotwoonelabs.picafxfreev2.dialogviews;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zerotwoonelabs.picafxfreev2.R;

public class ProgressFrag extends DialogFragment{

	private AnimationDrawable mRotation;
	private ImageView mSpinner;
	private boolean dismiss;
	private Runnable mTask;
	
	public ProgressFrag(Runnable task) {
		mTask = task;
		setRetainInstance(true);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setStyle(STYLE_NO_FRAME, getTheme());
		this.setStyle(STYLE_NO_TITLE, getTheme());
		//setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.progressview, container);
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSpinner = (ImageView) view.findViewById(R.id.imgProgress);
		
		mRotation = new AnimationDrawable();
		mRotation.addFrame(getResources().getDrawable(R.drawable.prog01), 100);
		mRotation.addFrame(getResources().getDrawable(R.drawable.prog02), 100);
		mRotation.addFrame(getResources().getDrawable(R.drawable.prog03), 100);
		mRotation.addFrame(getResources().getDrawable(R.drawable.prog04), 100);
		mRotation.addFrame(getResources().getDrawable(R.drawable.prog05), 100);
		
		mRotation.setOneShot(false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			mSpinner.setBackground(mRotation);
		else
			mSpinner.setBackgroundDrawable(mRotation);
		
	}
	
	public void setDismiss(){
		dismiss = true;
		if (getDialog() != null)
			getDialog().dismiss();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (dismiss)
			dismiss();
		
		mRotation.start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mTask.run();
				dismiss();
			}
		}).start();
	}
	
	@Override
	public void onDestroyView() {
	 mRotation.stop();
	 super.onDestroyView();
	}
	
}