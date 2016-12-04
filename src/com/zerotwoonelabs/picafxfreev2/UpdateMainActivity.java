package com.zerotwoonelabs.picafxfreev2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zerotwoonelabs.picafxfree.HomeFragment;
import com.zerotwoonelabs.picafxfree.MainActivityNavigation;
import com.zerotwoonelabs.picafxfree.RecentFragment;
import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfree.support.BMPHandler.BMPSaveInterface;
import com.zerotwoonelabs.picafxfree.support.RecentStruct;
import com.zerotwoonelabs.picafxfreev2.dialogviews.ProgressFrag;
import com.zerotwoonelabs.picafxfreev2.tools.AdjustmentProcessor;
import com.zerotwoonelabs.picafxfreev2.tools.ToolsMenu;

public class UpdateMainActivity extends Activity implements OnClickListener,
		BMPSaveInterface, Callback {
	
	private AdView mAdsView;

	private SurfaceView mMainSurface;
	private BMPHandler mBMPHandler;
	private CanvasUpdateLooper mCanvasLooper;
	private ArrayList<PathArrayHandler> mPathHandler = new ArrayList<PathArrayHandler>();

	private int mScreenWidth, mScreenHeight;
	private int mLeft, mTop;

	private Path mCurrentSelectedPath;

	private boolean isPathInverse;

	private AdjustmentProcessor mAdjustmentLayer;

	private volatile Bitmap mMainBmpChunk;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Static.initializeStatic(this);

		setContentView(R.layout.activity_update_main);
		
		mAdsView = (AdView) findViewById(R.id.adUpdateView);
		if (!MainActivityNavigation.isPurchased() && mAdsView != null) 			
			mAdsView.loadAd(new AdRequest.Builder().addTestDevice(
					"883921C29483F7D030D5A5E27455B425")
					.addTestDevice("A0CB025723CDC39E7B4B431B82661E2E").build());
		else
			mAdsView.setVisibility(View.GONE);

		mMainSurface = (SurfaceView) findViewById(R.id.mainSurface);
		mMainSurface.setOnClickListener(this);
		mMainSurface.getHolder().addCallback(this);

		mCanvasLooper = new CanvasUpdateLooper();
		mCanvasLooper.start();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Static.getTransaction(UpdateMainActivity.this)
				.replace(R.id.layoutFrameTools, new ToolsMenu(), "Main")
				.commit();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				findViewById(R.id.txtEffectsTool).requestFocus();
			}
		}, 500);
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if (mBMPHandler != null && isFinishing()) {

			// new Thread(new Runnable() {

			// @Override
			// public void run() {
			try {
				FileOutputStream stream = openFileOutput(
						String.valueOf(mBMPHandler.getId()) + ".png",
						MODE_PRIVATE);
				FileOutputStream streamThumb = openFileOutput(
						"TH" + String.valueOf(mBMPHandler.getId()) + ".png",
						MODE_PRIVATE);
				Bitmap bmp = mBMPHandler.getBitmap();
				bmp.compress(CompressFormat.PNG, 100, stream);
				stream.close();
				bmp.recycle();

				mBMPHandler.getBitmapScaled(100, 100).compress(
						CompressFormat.PNG, 100, streamThumb);
				streamThumb.close();

				Document doc = RecentFragment
						.getDocument(getApplicationContext());
				NodeList list = doc
						.getElementsByTagName(RecentFragment.TAG_RECENT);

				RecentStruct str = new RecentStruct();

				str.config = mBMPHandler.getConfig();
				str.height = mBMPHandler.getHeight();
				str.width = mBMPHandler.getWidth();
				str.lastActive = System.currentTimeMillis();
				str.uri = mBMPHandler.getBMPUri();
				str.id = mBMPHandler.getId();

				str.list = mPathHandler;

				boolean ifFound = false;
				if (list.getLength() > 0) {
					for (int i = 0; i < list.getLength(); i++) {
						Element id = (Element) ((Element) list.item(i))
								.getElementsByTagName(RecentFragment.TAG_ID)
								.item(0);
						long idlong = Long.valueOf(id.getTextContent());
						if (idlong == mBMPHandler.getId()) {
							ifFound = true;
							RecentFragment.appendProperties(
									(Element) list.item(i), str,
									UpdateMainActivity.this);
						}
					}

				}

				if (!ifFound)
					RecentFragment.addProperties(doc, str,
							UpdateMainActivity.this);

				mBMPHandler.destroy();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// }
			// }).start();
		}
	}

	public Bitmap getBitmap() {
		return mMainBmpChunk;
	}

	public BMPHandler getBMPHandler() {
		return mBMPHandler;
	}

	public ArrayList<PathArrayHandler> getPathHandler() {
		return mPathHandler;
	}

	public Path getCurrentPath() {
		return mCurrentSelectedPath;
	}

	public void setCurrentPath(Path p) {
		mCurrentSelectedPath = p;
	}

	public int getLeft() {
		return mLeft;
	}

	public int getTop() {
		return mTop;

	}

	public synchronized void updatesurface(boolean withdialog) {

		if (getFragmentManager().findFragmentByTag("update") == null) {
			if (withdialog)
				new ProgressFrag(mPostSimple).show(getFragmentManager(),
						"update");
			else {
				mCanvasLooper.getHandler().removeCallbacks(mPostSimple);
				mCanvasLooper.getHandler().post(mPostSimple);
			}
		} else {
			mCanvasLooper.getHandler().removeCallbacks(mPostSimple);
			mCanvasLooper.getHandler().post(mPostSimple);
		}
	}

	private Runnable mPostSimple = new Runnable() {

		@Override
		public void run() {
			Canvas c = null;
			try {

				c = mMainSurface.getHolder().lockCanvas();
				if (c != null) {
					c.drawColor(0xFF000000);
					c.save();
					c.translate(getLeft(), getTop());
					c.drawBitmap(mMainBmpChunk, 0, 0, null);
					if (mAdjustmentLayer != null)
						mAdjustmentLayer.adjust(c, mMainBmpChunk);

					c.restore();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();

			} finally {
				if (c != null)
					mMainSurface.getHolder().unlockCanvasAndPost(c);
			}
		}
	};

	public void registerAdjustmentLayer(AdjustmentProcessor processor) {
		this.mAdjustmentLayer = processor;
		this.mMainSurface.setOnTouchListener(mAdjustmentLayer
				.getOnTouchListner());
	}

	public void unregisterAdjustmentLayer(AdjustmentProcessor processor) {
		this.mAdjustmentLayer = null;
		this.mMainSurface.setOnTouchListener(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.mainSurface:
			isPathInverse = !isPathInverse;
			updatesurface(false);
			break;

		default:
			break;
		}
	}

	@Override
	public void saveImage(Bitmap bmp) {
		mMainBmpChunk = bmp;
		mLeft = (int) (mScreenWidth / 2f - mMainBmpChunk.getWidth() / 2f);
		mTop = (int) (mScreenHeight / 2f - mMainBmpChunk.getHeight() / 2f);
		updatesurface(false);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mScreenWidth = width;
		mScreenHeight = height;
		OpenCVLoader.initDebug();

		new ProgressFrag(new Runnable() {
			@Override
			public void run() {

				if (mBMPHandler == null) {
					Bundle bundle = getIntent().getBundleExtra(
							MainActivityNavigation.BUNDLE_KEY);

					if (bundle != null) {
						mBMPHandler = bundle
								.getParcelable(MainActivityNavigation.BMP_HANDLER_KEY);
						mPathHandler = bundle
								.getParcelableArrayList(MainActivityNavigation.PATH_ARRAY_KEY);
					}

					if (getIntent().getBooleanExtra(
							MainActivityNavigation.BMP_HANDLER_BOOLEAN_KEY,
							true))
						try {
							if (mBMPHandler == null)
								mBMPHandler = new BMPHandler((Uri) getIntent()
										.getParcelableExtra("URI"),
										UpdateMainActivity.this, System
												.currentTimeMillis());

						} catch (IOException e) {
							e.printStackTrace();
							finish();
						}

				}

				if (mPathHandler.isEmpty())
					mPathHandler.add(new PathArrayHandler());

				saveImage(mBMPHandler.getBitmapScaled(mScreenWidth,
						mScreenHeight));
				mBMPHandler.setInterface(UpdateMainActivity.this);
				updatesurface(false);
			}
		}).show(getFragmentManager(), null);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable("bmphandler", mBMPHandler);
		outState.putParcelableArrayList("pathhandler", mPathHandler);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mBMPHandler = savedInstanceState.getParcelable("bmphandler");
		mPathHandler = savedInstanceState.getParcelableArrayList("pathhandler");
	}

	@Override
	public int getScreenWidth() {
		return mScreenWidth;
	}

	@Override
	public int getScreenHeight() {
		return mScreenHeight;
	}

	public boolean isInversePath() {
		return isPathInverse;
	}

	public void setInverse() {
		isPathInverse = !isPathInverse;
		updatesurface(false);
	}

	public void setFullscreen() {
		Fragment frag = getFragmentManager().findFragmentById(
				R.id.layoutFrameTools);
		Fragment bfrag = getFragmentManager().findFragmentById(R.id.layoutFrameBottom);

		if (frag != null) {
			if (frag.isHidden())
				Static.getTransaction(this).show(frag).show(bfrag).commit();
			else
				Static.getTransaction(this).hide(frag).hide(bfrag).commit();
		}
	}
}
