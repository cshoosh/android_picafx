package com.zerotwoonelabs.picafxfreev2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class PathArrayHandler implements Parcelable{
	
	private ArrayList<Float> xArray;
	private ArrayList<Float> yArray;
	private ArrayList<Integer> actionArray;

	private Path mPath;
	
	public PathArrayHandler() {
		xArray = new ArrayList<Float>();
		yArray = new ArrayList<Float>();
		actionArray = new ArrayList<Integer>();
		
		mPath = new Path();
	}
	
	@SuppressWarnings("unchecked")
	public PathArrayHandler (Parcel src){
		xArray = src.readArrayList(Float.class.getClassLoader());
		yArray = src.readArrayList(Float.class.getClassLoader());
		actionArray = src.readArrayList(Integer.class.getClassLoader());
		
		mPath = getNewPath();
	}
	
	public PathArrayHandler(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Integer> action){
		xArray = new ArrayList<Float>(x);
		yArray = new ArrayList<Float>(y);
		actionArray = new ArrayList<Integer>(action);
		
		mPath = getNewPath();
	}
	
	public void setX(ArrayList<Float> x) {
		xArray = new ArrayList<Float>(x);
	}
	
	public void setY(ArrayList<Float> y) {
		yArray = new ArrayList<Float>(y);
	}
	
	public void setAction (ArrayList<Integer> action) {
		actionArray = new ArrayList<Integer>(action);
	}
	
	public ArrayList<Float> getX() {
		return xArray;
	}
	
	public ArrayList<Float> getY() {
		return yArray;
	}
	
	public ArrayList<Integer> getAction() {
		return actionArray;
	}
	
	public Path getPath(){
		return mPath;
	}

	public Path getNewPath() {
		Path p = new Path();

		if (xArray.size() > 1) {
			p.moveTo(xArray.get(0), yArray.get(0));
			for (int i = 1; i < xArray.size(); i++) {
				switch (actionArray.get(i)) {
				case 1:
					p.lineTo(xArray.get(i), yArray.get(i));
					break;
				case 0:
					p.moveTo(xArray.get(i), yArray.get(i));
					break;
				default:
					break;
				}
			}
		}
		
		return p;
	}
	
	public static Path getMiniPath(Path p, Context context){
		if (p != null){
			
			RectF bounds = new RectF();
			Matrix m = new Matrix();
			
			Path src = new Path(p);
			src.computeBounds(bounds, true);
			src.offset(-bounds.left, -bounds.top);
			float xRatio, yRatio;
			float mWidth = context.getResources().getDimension(R.dimen.icon_size);
			float mSupposeHeight = mWidth/(bounds.width()/bounds.height());
			
			if (mSupposeHeight > mWidth)
				mSupposeHeight = mWidth;
			
			xRatio = (mWidth - 8)/bounds.width();
			yRatio = (mSupposeHeight - 8)/bounds.height();
				
			m.setScale(xRatio, yRatio);
			src.transform(m);
			
			if (mSupposeHeight < mWidth - 10)
				src.offset(4, 10 + 4);
			else
				src.offset(4, 4);
			
			return src;
		}
		
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(xArray);
		dest.writeList(yArray);
		dest.writeList(actionArray);
	}
	
	public static final Creator<PathArrayHandler> CREATOR = new Creator<PathArrayHandler>() {
		
		@Override
		public PathArrayHandler[] newArray(int size) {
			return new PathArrayHandler[size];
		}
		
		@Override
		public PathArrayHandler createFromParcel(Parcel source) {
			return new PathArrayHandler(source);
		}
	};
}
