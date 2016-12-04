package com.zerotwoonelabs.picafxfree.support;

import java.util.ArrayList;

import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.zerotwoonelabs.picafxfreev2.PathArrayHandler;

public class RecentStruct implements Parcelable {
	public Config config;
	public int width, height;
	public long lastActive, id;
	public Uri uri;
	public ArrayList<PathArrayHandler> list;

	public RecentStruct(Config config, int width, int height, long id,
			Uri uri, long lastActive, ArrayList<PathArrayHandler> list) {
		this.config = config;
		this.width = width;
		this.height = height;
		this.id = id;
		this.uri = uri;
		this.lastActive = lastActive;
		this.list = list;
	}

	public RecentStruct() {
	}

	@SuppressWarnings("unchecked")
	public RecentStruct(Parcel out) {
		uri = out.readParcelable(Uri.class.getClassLoader());
		width = out.readInt();
		height = out.readInt();
		id = out.readLong();
		lastActive = out.readLong();
		config = (Config) out.readSerializable();
		list = out
				.readArrayList(PathArrayHandler.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(uri, flags);
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeLong(id);
		dest.writeLong(lastActive);
		dest.writeSerializable(config);
		dest.writeList(list);
	}

	public static final Creator<RecentStruct> CREATOR = new Creator<RecentStruct>() {

		@Override
		public RecentStruct[] newArray(int size) {
			return new RecentStruct[size];
		}

		@Override
		public RecentStruct createFromParcel(Parcel source) {
			return new RecentStruct(source);
		}
	};
}
