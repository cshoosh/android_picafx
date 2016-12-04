package com.zerotwoonelabs.picafxfreev2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.preference.PreferenceManager;

public class Static {
	
	public static final String KEY_SCROLLTIP = "scrollTipKey";
	public static final String KEY_PENTIP = "penTipKey";
	
	public static boolean isScrollTipShown;
	public static boolean isPenTipShown;
	
	@SuppressLint("CommitTransaction")
	public static FragmentTransaction getTransaction(Activity activity) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.toolbox_frag_anim_out, 
				R.animator.toolbox_frag_anim_in);
		
		return ft;
	}
	
	public static void initializeStatic(Context context) {
		isScrollTipShown = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(KEY_SCROLLTIP, false);
		
		isPenTipShown = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(KEY_PENTIP, false);
	}
}
