package com.zerotwoonelabs.picafxfree;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zerotwoonelabs.picafxfreev2.R;

public class MainActivityNavigation extends FragmentActivity 
		implements OnClickListener {	

	public static final String BMP_HANDLER_BOOLEAN_KEY = "BMPHandlerBool";
	
	public static final String BMP_HANDLER_KEY = "BMPHandler";
	public static final String PATH_ARRAY_KEY = "PathKey";
	public static final String BUNDLE_KEY = "bundleKey";
	
	public static final int REQUEST_RECENT = 2000;
	
	private MainFragmentTabs mPager;
	private ViewPager mViewPager;
	
	private AdView AdsShow;
	private static InAppStore mStoreAPI;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (AdsShow != null)
			AdsShow.destroy();

		if (mStoreAPI != null)
			mStoreAPI.destroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (AdsShow != null)
			AdsShow.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (AdsShow != null)
			AdsShow.resume();
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_mainredo);
		
		findViewById(R.id.txtHome).setOnClickListener(this);
		findViewById(R.id.txtRecent).setOnClickListener(this);		
	
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		mPager = new MainFragmentTabs(getSupportFragmentManager());
		mViewPager.setAdapter(mPager);
		
		mStoreAPI = new InAppStore(this);
		boolean isPurchased = PreferenceManager.getDefaultSharedPreferences(
				this).getBoolean(InAppStore.UPGRADE, false);
		
		AdsShow = (AdView) findViewById(R.id.adView);
		if (!isPurchased) 			
			AdsShow.loadAd(new AdRequest.Builder().addTestDevice(
					"883921C29483F7D030D5A5E27455B425")
					.addTestDevice("A0CB025723CDC39E7B4B431B82661E2E").build());
		else
			AdsShow.setVisibility(View.GONE);
		
		/*mStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
		
		mStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mStrip.setTabIndicatorColorResource(android.R.color.background_light);
		mStrip.setGravity(Gravity.LEFT);
		mStrip.setBackgroundColor(getResources().getColor(android.R.color.background_dark));*/
	}

	public static boolean isPurchased() {
		if (mStoreAPI != null)
			return mStoreAPI.isUpgraded();

		return false;
	}
	
	public static InAppStore getStoreAPI() {
		return mStoreAPI;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mStoreAPI != null)
			mStoreAPI.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_RECENT && resultCode == RESULT_OK) {
			
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MainFragmentTabs extends FragmentPagerAdapter {

		private Fragment HomeFragment, RecentFragment;

		public MainFragmentTabs(FragmentManager fm) {
			super(fm);

			HomeFragment = new HomeFragment();
			RecentFragment = new RecentFragment();
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				return HomeFragment;
			case 1:
				return RecentFragment;

			default:
				break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "HOME";
			case 1:
				return "RECENT";
			default:
				break;
			}
			return "";
		}
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtHome:
			if (mViewPager != null)
				mViewPager.setCurrentItem(0, true);
			break;
		case R.id.txtRecent:
			if (mViewPager != null)
				mViewPager.setCurrentItem(1, true);
			break;
		default:
			break;
		}
	}
}
