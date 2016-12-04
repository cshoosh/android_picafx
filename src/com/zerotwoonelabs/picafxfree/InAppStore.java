package com.zerotwoonelabs.picafxfree;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class InAppStore implements ServiceConnection {

	private static final String KEY_APP = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqOudhJUAh8cbfvFYEqO9AdKhKE1xmPw6Sd3a93A2U7TTGoJ43cY14/5riJbb/EGnuniyTFc2KuOn0cD+nKw6QcmM9xfCXOCh2gXLrDwc1VRhRNQne9mKoFS5MuSI20+NgzeZWqO+U+sg2F5qZPEZRF/wnScspHks2/1dHOWh/HbwPCWmS5FqIo5QyD9vneq6ylm9BvqI2ThKe4q+CwMaLK6fekeYmdYy0ixE36WQlt6I3NQ1nAkuscIS9TGOOwhpwAV94rE4fpF8fpwVcVOb4MWJDArgI6R4SQWAUcNlnpwo59mOicVAZICQFC/RO+OCqZy/0D3YO7bJgTm15Xh++wIDAQAB";

	public static final String UPGRADE = "upgrade";

	public static final String TYPE_INAPP = "inapp";

	private IInAppBillingService mBillingService;
	private Activity mContext;
	private boolean isUpgraded;

	private static final int REQUEST_PURCHASE = 5;
	private static final int REQUEST_GOOGLE_SERVICE_ERROR = 6;

	public InAppStore(Activity context) {
		init(context);
	}

	public InAppStore() {
	}

	public void init(Activity context) {
		mContext = context;

		int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(mContext);

		if (result == ConnectionResult.SUCCESS) {
			context.bindService(new Intent(
					"com.android.vending.billing.InAppBillingService.BIND"),
					this, Context.BIND_AUTO_CREATE);
		} else {
			Dialog diagError = GooglePlayServicesUtil.getErrorDialog(result,
					(Activity) context, REQUEST_GOOGLE_SERVICE_ERROR);
			diagError.show();
		}
	}

	public boolean isUpgraded() {
		return isUpgraded;
	}

	public void setUpgraded(boolean isUpgraded) {
		if (isUpgraded) {

		}
		PreferenceManager.getDefaultSharedPreferences(mContext).edit()
				.putBoolean(UPGRADE, isUpgraded).commit();
		this.isUpgraded = isUpgraded;
	}

	public boolean isBillingSupported() {
		if (mBillingService != null)
			try {
				if (mBillingService.isBillingSupported(3,
						mContext.getPackageName(), TYPE_INAPP) == 0)
					return true;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PURCHASE) {
			// int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			// String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

			if (resultCode == Activity.RESULT_OK) {
				String signatureData = data
						.getStringExtra("INAPP_DATA_SIGNATURE");
				String inappData = data.getStringExtra("INAPP_PURCHASE_DATA");

				try {
					JSONObject json = new JSONObject(inappData);
					boolean isPackaage = json.getString("productId").equals(
							UPGRADE);
					if (signatureData.equals(KEY_APP) && isPackaage){
						setUpgraded(true);
						Toast.makeText(mContext, "Please, restart the app to remove ads\n" +
								"Thank You.", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void makePurchase(String id, String type) {
		Bundle buyIntentBundle = null;

		if (isBillingSupported()) {
			try {
				if (mBillingService != null) {
					buyIntentBundle = mBillingService.getBuyIntent(3,
							mContext.getPackageName(), id, type,
							Secure.ANDROID_ID);
				} else
					Toast.makeText(
							mContext,
							"Service not available, try installing Google "
									+ "Play services", Toast.LENGTH_SHORT)
							.show();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (buyIntentBundle != null) {
				PendingIntent pendingIntent = buyIntentBundle
						.getParcelable("BUY_INTENT");
				try {
					mContext.startIntentSenderForResult(
							pendingIntent.getIntentSender(), REQUEST_PURCHASE,
							new Intent(), 0, 0, 0);
				} catch (SendIntentException e) {
					e.printStackTrace();
				}
			}
		} else {
			Toast.makeText(
					mContext,
					"Billing not supported on this device, "
							+ "\nUpgrade Google Play Store", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void destroy() {
		if (mContext != null)
			mContext.unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBillingService = IInAppBillingService.Stub.asInterface(service);

		try {

			Bundle purchases = mBillingService.getPurchases(3,
					mContext.getPackageName(), "inapp", null);
			int response = purchases.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList<String> listPurchases = purchases
						.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

				for (String thisResponse : listPurchases) {
					if (thisResponse.equals(UPGRADE))
						setUpgraded(true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mBillingService = null;
	}
}
