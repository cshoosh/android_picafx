package com.zerotwoonelabs.picafxfree.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class BMPHandler implements Parcelable {
	private Uri mBMPuri;
	// private Context mContext;

	private FileChannel mBMPFileChannel;
	private RandomAccessFile mBMPRandFile;
	private MappedByteBuffer mBMPByteBuffer;
	private File tempFile;
	
	private File tempLastFile;
	private boolean  isUndoAvailable;
	
	private int mWidth, mHeight, mRowBytes;
	private long mId;
	private Config mConfig;

	//private ArrayList<PathArrayHolder> mPathList = new ArrayList<BMPHandler.PathArrayHolder>();

	public BMPHandler(Uri bmpURI, Activity context, long id) throws IOException {
		
		mBMPuri = bmpURI;
		// InputStream in =
		// context.getContentResolver().openInputStream(mBMPuri);

		Bitmap original = NormalizeBitmap(context);

		mWidth = original.getWidth();
		mHeight = original.getHeight();
		mConfig = original.getConfig();
		mRowBytes = original.getRowBytes();

		tempFile = new File(context.getCacheDir(), "temp.tmp");
		tempLastFile = new File(context.getCacheDir(), "tempLast.png");
		//mPathList.clear();
		//mPathList.add(null);
		mBMPRandFile = new RandomAccessFile(tempFile, "rw");
		mBMPFileChannel = mBMPRandFile.getChannel();
		mBMPByteBuffer = mBMPFileChannel.map(MapMode.READ_WRITE, 0,
				original.getRowBytes() * original.getHeight());

		mId = id;
		original.copyPixelsToBuffer(mBMPByteBuffer);
		original.recycle();
	}

	public BMPHandler(RecentStruct struct, Activity context) throws IOException {
		mBMPuri = struct.uri;
		// InputStream in =
		// context.getContentResolver().openInputStream(mBMPuri);
		FileInputStream stream = context.openFileInput(String
				.valueOf(struct.id) + ".png");
		Bitmap original = BitmapFactory.decodeStream(stream);
		stream.close();
		mWidth = struct.width;
		mHeight = struct.height;
		mConfig = struct.config;
		//mPathList = struct.list;
		mRowBytes = original.getRowBytes();

		tempFile = new File(context.getCacheDir(), "temp.tmp");
		tempLastFile = new File(context.getCacheDir(), "tempLast.png");

		mBMPRandFile = new RandomAccessFile(tempFile, "rw");
		mBMPFileChannel = mBMPRandFile.getChannel();
		mBMPByteBuffer = mBMPFileChannel.map(MapMode.READ_WRITE, 0,
				original.getRowBytes() * original.getHeight());

		mId = struct.id;
		original.copyPixelsToBuffer(mBMPByteBuffer);
		original.recycle();
	}

	public BMPHandler(Parcel out) throws IOException {
		mBMPuri = out.readParcelable(Uri.class.getClassLoader());
		mRowBytes = out.readInt();
		tempFile = (File) out.readSerializable();
		tempLastFile = (File) out.readSerializable();
		mWidth = out.readInt();
		mHeight = out.readInt();
		mConfig = (Config) out.readSerializable();
		mId = out.readLong();
		isUndoAvailable = Boolean.parseBoolean(out.readString());

		//mPathList = out.readArrayList(PathArrayHolder.class.getClassLoader());

		mBMPRandFile = new RandomAccessFile(tempFile, "rw");
		mBMPFileChannel = mBMPRandFile.getChannel();
		mBMPByteBuffer = mBMPFileChannel.map(MapMode.READ_WRITE, 0, mRowBytes
				* mHeight);
	}
	
	public void writeBitmapOnSameThread(Bitmap bmp) {

				FileOutputStream stream;
				try {
					stream = new FileOutputStream(tempLastFile);
					getBitmap().compress(CompressFormat.PNG, 100, stream);
					isUndoAvailable = true;
					stream.close();
				} catch (FileNotFoundException e) {e.printStackTrace();}
				  catch (IOException e) {e.printStackTrace();}
				
				mBMPByteBuffer.clear();
				bmp.copyPixelsToBuffer(mBMPByteBuffer);

				mWidth = bmp.getWidth();
				mHeight = bmp.getHeight();
				
				
				bmp.recycle();
				forceWrite();
				
				
				if (mInterface != null)
					mInterface.saveImage(getBitmapScaled(mInterface.getScreenWidth(),
							mInterface.getScreenHeight()));
			
	}

	public void writeBitmapOnNewThread(final Bitmap bmp) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				writeBitmapOnSameThread(bmp);
			}
		}).start();

	}
	public void askToSave(int where, final Context context) {
		Builder diag = new Builder(context);
		switch (where) {
		case 0:
			diag.setTitle("Save Image").setMessage("Are you sure?")
					.setPositiveButton("Yes", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveToMedia(getName(getBMPUri(), context)+"-1", context);
						}
					}).setNegativeButton("No", null).create().show();
			break;
		case 1:
			diag.setTitle("Share Image").setMessage("Are you sure?")
					.setPositiveButton("Yes", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveToMedia(getName(getBMPUri(), context) + "-1", context);
							Intent share = new Intent(Intent.ACTION_SEND);
							share.setType("image/jpeg");
							share.putExtra(Intent.EXTRA_STREAM, getBMPUri());
							context.startActivity(Intent.createChooser(share,
									"Share Via"));
						}
					}).setNegativeButton("No", null).create().show();
			break;

		default:
			break;
		}

	}

	private void saveToMedia(String title, Context context) {

		Bitmap tmp = getBitmap();
		/*
		 * if (!MainActivity.isPurchased()) {
		 * 
		 * Bitmap waterMark =
		 * BitmapFactory.decodeResource(context.getResources(),
		 * R.drawable.ic_launcher);
		 * 
		 * Canvas c = new Canvas(tmp); Paint p = new Paint();
		 * 
		 * RectF dst = new RectF(getWidth() * .05f, getWidth() * 0.05f,
		 * getWidth() * 0.1f, getWidth() * 0.1f);
		 * 
		 * p.setXfermode(new PorterDuffXfermode(Mode.ADD));
		 * c.drawBitmap(waterMark, null, dst, p);
		 * 
		 * if (waterMark != null && !waterMark.isRecycled())
		 * waterMark.recycle(); }
		 */
		Uri uri = SaveImageUtility
				.insertImage(context.getContentResolver(), tmp, title,
						"Created using PicaFx Image Studio. Available on Google Play Store.");
		tmp.recycle();
		mBMPuri = uri == null ? mBMPuri : uri;
	}

	public void forceWrite() {
		mBMPByteBuffer.force();
	}

	public Uri getBMPUri() {
		return mBMPuri;
	}

	public Bitmap getBitmap() {
		Bitmap ret = Bitmap.createBitmap(mWidth, mHeight, mConfig);

		mBMPByteBuffer.clear();
		ret.copyPixelsFromBuffer(mBMPByteBuffer);
		return ret;
	}
	
	public Bitmap getBitmapScaled(int width, int height){
		return OpenCV.ReturnScale(getBitmap(), width, height);
	}

	public Config getConfig() {
		return mConfig;
	}

	public long getId() {
		return mId;
	}

	public int getWidth() {
		return mWidth;
	}

	/*public ArrayList<PathArrayHolder> getPathArrayList() {
		return mPathList;
	}*/

	public int getHeight() {
		return mHeight;
	}
	
	public boolean isUndoAvailable(){
		return isUndoAvailable;
	}

	public void undo() throws IOException{
		if (isUndoAvailable) {
			FileInputStream io = new FileInputStream(tempLastFile);
			Bitmap bmp = BitmapFactory.decodeStream(io);
			io.close();
			writeBitmapOnSameThread(bmp);
			isUndoAvailable = false;
		}
	}
	
	public boolean doesFileExist() {
		return tempFile != null && tempFile.exists();
	}

	/*public PathArrayHolder addPathArrayHolder(float[] x, float[] y) {
		if (x != null && x.length > 0) {
			PathArrayHolder holder = new PathArrayHolder();

			for (int i = 0; i < x.length; i++) {
				holder.X = x;
				holder.Y = y;
			}
			//mPathList.add(holder);
			return holder;
		}
		return null;
	}*/

	public void destroy() throws IOException {
		forceWrite();
		mBMPFileChannel.close();
		mBMPRandFile.close();
		tempLastFile.delete();
		// mContext = null;
	}
	
	public static String getDate(long time) {
		return SimpleDateFormat.getDateInstance()
				.format(new Date(time));
	}
	
	public static String getName(Uri uri, Context context) {
		String fileName = "";
		String scheme = uri.getScheme();
		if (scheme.equals("file")) {
			fileName = uri.getLastPathSegment();
		} else if (scheme.equals("content")) {
			String[] proj = { MediaStore.Images.Media.TITLE };
			Cursor cursor = context.getContentResolver().query(
					uri, proj, null, null, null);
			if (cursor != null && cursor.getCount() != 0) {
				int columnIndex = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
				cursor.moveToFirst();
				fileName = cursor.getString(columnIndex);
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return fileName;
	}
	public static boolean isValidUri(Uri uri, Context context){
		String scheme = uri.getScheme();
		if (scheme.equals("file")) {
			if(new File(uri.getPath()).exists())
				return true;
		} else if (scheme.equals("content")) {
			String[] proj = { MediaStore.Images.Media._ID };
			Cursor cursor = context.getContentResolver().query(
					uri, proj, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
		}
		return false;
	}

	private BMPSaveInterface mInterface;
	public void setInterface(BMPSaveInterface inter){mInterface = inter;}
	public interface BMPSaveInterface{
		public abstract void saveImage(Bitmap bmp);
		public abstract int getScreenWidth();
		public abstract int getScreenHeight();
	}
	
	private Bitmap NormalizeBitmap(Context cntxt) throws IOException {
		Options op = new Options();
		op.inJustDecodeBounds = true;

		InputStream in = cntxt.getContentResolver().openInputStream(mBMPuri);
		Bitmap tmp = BitmapFactory.decodeStream(in, null, op);
		in = cntxt.getContentResolver().openInputStream(mBMPuri);

		int widthOut = op.outWidth;
		int heightOut = op.outHeight;
		Long allowedMem = Runtime.getRuntime().maxMemory();
		if (widthOut * heightOut * 4 * 2 >= allowedMem - (13 * 1024 * 1024)) {
			float ratio = widthOut / (float) heightOut;
			long availableMem = allowedMem - (13 * 1024 * 1024);
			availableMem = availableMem / 4;
			availableMem = availableMem / 2;

			float height = (float) Math.sqrt(availableMem / ratio);
			float width = ratio * height;

			int inSample = (int) (widthOut / width);

			while (widthOut / (float) inSample > width)
				inSample++;
			op.inJustDecodeBounds = false;
			op.inSampleSize = inSample;
			op.inPreferredConfig = Config.ARGB_8888;
			tmp = BitmapFactory.decodeStream(in, null, op);
			if (tmp.getConfig() != Config.ARGB_8888) {
				Bitmap tmp1 = tmp.copy(Config.ARGB_8888, true);
				tmp.recycle();
				tmp = tmp1;
			}
		} else {
			op.inPreferredConfig = Config.ARGB_8888;
			op.inJustDecodeBounds = false;
			tmp = BitmapFactory.decodeStream(in, null, op);
			if (tmp.getConfig() != Config.ARGB_8888) {
				Bitmap tmp1 = tmp.copy(Config.ARGB_8888, true);
				tmp.recycle();
				tmp = tmp1;
			}
		}
		if (in != null)
			in.close();
		return tmp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mBMPuri, 0);
		dest.writeInt(mRowBytes);
		dest.writeSerializable(tempFile);
		dest.writeSerializable(tempLastFile);
		dest.writeInt(mWidth);
		dest.writeInt(mHeight);
		dest.writeSerializable(mConfig);
		dest.writeLong(mId);
		dest.writeString("" + isUndoAvailable);
		//dest.writeList(mPathList);
	}

	public static final Creator<BMPHandler> CREATOR = new Creator<BMPHandler>() {

		@Override
		public BMPHandler[] newArray(int size) {
			return new BMPHandler[size];
		}

		@Override
		public BMPHandler createFromParcel(Parcel source) {
			try {
				return new BMPHandler(source);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	/*public static class PathArrayHolder implements Parcelable {
		public float[] X;
		public float[] Y;

		public PathArrayHolder() {
		}

		public PathArrayHolder(Parcel out) {
			X = out.createFloatArray();
			Y = out.createFloatArray();
		}

		public Path convertToPath() {
			Path path = new Path();
			path.moveTo(X[0], Y[0]);
			int j = 0;
			for (float f : X) {
				path.lineTo(f, Y[j]);
				j++;
			}
			path.lineTo(X[0], Y[0]);
			return path;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloatArray(X);
			dest.writeFloatArray(Y);
		}

		public static final Creator<PathArrayHolder> CREATOR = new Creator<PathArrayHolder>() {

			@Override
			public PathArrayHolder[] newArray(int size) {
				return new PathArrayHolder[size];
			}

			@Override
			public PathArrayHolder createFromParcel(Parcel source) {
				return new PathArrayHolder(source);
			}
		};
	}*/
}