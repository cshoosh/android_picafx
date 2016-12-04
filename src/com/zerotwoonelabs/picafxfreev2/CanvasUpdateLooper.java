package com.zerotwoonelabs.picafxfreev2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class CanvasUpdateLooper extends Thread{

	private Handler mHandler;
	
	public CanvasUpdateLooper() {
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	public void run() {
		Looper.prepare();
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);				
			}
		};
		Looper.loop();
	}
	
	public Handler getHandler(){
		return mHandler;
	}
}
