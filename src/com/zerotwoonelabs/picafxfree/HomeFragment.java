package com.zerotwoonelabs.picafxfree;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerotwoonelabs.picafxfree.support.BMPHandler;
import com.zerotwoonelabs.picafxfreev2.R;
import com.zerotwoonelabs.picafxfreev2.UpdateMainActivity;

public class HomeFragment extends Fragment implements OnClickListener {


	private Uri mImageUri, mImageCameraUri;
	

	private boolean isProcessImage;

	private static final String IMAGE_SAVE_URI = "camerauri";
	public static final String DISABLE_TIPS = "disabletips";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mImageCameraUri = savedInstanceState.getParcelable(IMAGE_SAVE_URI);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View ret = inflater.inflate(R.layout.activity_main, container, false);
		

		ret.findViewById(R.id.imgGallery).setOnClickListener(this);
		ret.findViewById(R.id.imgCamera).setOnClickListener(this);
		ret.findViewById(R.id.removead).setOnClickListener(this);
		return ret;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (isProcessImage) {

			Intent intent = new Intent(getActivity(), UpdateMainActivity.class);
			intent.putExtra(MainActivityNavigation.BMP_HANDLER_BOOLEAN_KEY, true);
			intent.putExtra("URI", mImageUri);
			this.startActivity(intent);

		}
		isProcessImage = false;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(IMAGE_SAVE_URI, mImageCameraUri);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 || requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
				case 1:
					mImageUri = data.getData();
					break;
				case 2:
					mImageUri = mImageCameraUri;
					break;
				default:
					break;
				}
				isProcessImage = true;
			} else {
				if (mImageCameraUri != null)
					getActivity().getContentResolver().delete(mImageCameraUri,
							null, null);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.imgGallery:
			Intent intentGallery = new Intent();
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intentGallery, "Select Gallery"), 1);
			break;
		case R.id.imgCamera:
			Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			List<ResolveInfo> list = getActivity().getPackageManager()
					.queryIntentActivities(intentCam,
							PackageManager.MATCH_DEFAULT_ONLY);
			if (list.size() > 0) {
				// mImageCameraUri = null;
				ContentValues values = new ContentValues();

				values.put(Images.Media.TITLE,
						"IMG_" + BMPHandler.getDate(System.currentTimeMillis()));
				values.put(Images.Media.DISPLAY_NAME,
						"IMG_" + BMPHandler.getDate(System.currentTimeMillis()));
				values.put(Images.Media.DESCRIPTION, "Picture taken by PicaFx");
				values.put(Images.Media.MIME_TYPE, "image/jpeg");
				// Add the date meta data to ensure the image is added at
				// the front of the gallery
				values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
				values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());

				mImageCameraUri = getActivity().getContentResolver().insert(
						Media.EXTERNAL_CONTENT_URI, values);

				intentCam.putExtra(MediaStore.EXTRA_OUTPUT, mImageCameraUri);
				startActivityForResult(
						Intent.createChooser(intentCam, "Select Camera"), 2);
			} else
				Toast.makeText(getActivity(),
						"No Application Found To Capture Image",
						Toast.LENGTH_SHORT).show();
			break;
		case R.id.removead:
			if (MainActivityNavigation.getStoreAPI() != null)
				MainActivityNavigation.getStoreAPI().makePurchase(InAppStore.UPGRADE,
						InAppStore.TYPE_INAPP);
			break;

		default:
			break;
		}
	}
}
