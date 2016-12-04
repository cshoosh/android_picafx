package com.zerotwoonelabs.picafxfree.support;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;



public class OpenCV {
	
	/*
	public static Bitmap ApplyEffect (EffectList effect, Bitmap src, float intensity, float extra){
		if (effect != null)
		switch (effect) {
		case ADJUST:
			break;
		case BlacknWhite:
			break;
		case Bokeh:
			return Dilate(src, intensity, extra);
		case BoxBlur:
			break;
		case Bright:
			break;
		case ChangeImage:
			break;
		case Emboss:
			return Emboss(src, (int) intensity, false);
		case GaussianBlur:
			break;
		case Glass:
			break;
		case HSV_ADJUST:
			break;
		case Invert:
			break; 
		case Pixelate:
			break;
		case Posterize:
			return Erode(src, intensity, extra);
		case Sharp:
			break;
		case SmartBlur:
			break;
		case Stone:
			return Emboss(src, (int) intensity, true);
		case TRANSPOSE:
			break;
		default:
			return src;
		}
		return src;
	}*/
	
	public static Bitmap ReturnCannyImage (Bitmap srcbMap,float intensity)
	{
		Mat src = new Mat();

		Utils.bitmapToMat(srcbMap, src);
		
		Mat src_gray = new Mat();
		Mat detected_edges = new Mat();
		int ratio = 3;
		int kernel_size = 3;
		
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(src_gray, detected_edges, new Size(3, 3));
		Imgproc.Canny(detected_edges, detected_edges, intensity, intensity*ratio,kernel_size,true);
		//src.copyTo(dst, detected_edges);
		
		Bitmap ret = Bitmap.createBitmap(srcbMap.getWidth(), srcbMap.getHeight(), Config.ARGB_8888);
		
		Utils.matToBitmap(detected_edges, ret);
		detected_edges.release();
		src.release();
		src_gray.release();
		return ret;
	}
	
	public static Bitmap GreyScale(Bitmap src){
		Mat srcMat = new Mat();
		Utils.bitmapToMat(src, srcMat, false);
		
		Mat src_gray = new Mat();
		Imgproc.cvtColor(srcMat, src_gray , Imgproc.COLOR_BGR2GRAY);
		srcMat.release();
		
		Utils.matToBitmap(src_gray, src, false);
		src_gray.release();
		
		return src;
	}
	
	/*public static Bitmap RotateBitmap (Bitmap bmp,boolean clockwise) 
	{
		IntBuffer map = IntBuffer.allocate(bmp.getHeight()*bmp.getWidth());
		
		bmp.copyPixelsToBuffer(map);
		
		map.rewind();
		
		int newWidth = bmp.getHeight();
		int newHeight = bmp.getWidth();
		
		int Width = bmp.getWidth();
		int Height = bmp.getHeight()-1;
		bmp.recycle();
		IntBuffer bytes = IntBuffer.allocate(map.limit());
		bytes.rewind();
		int index;
		if (clockwise)
		{
		for (int i = 0;i < newHeight; i++)
			for(int j = 0;j < newWidth; j++)
			{
				index = getIndex(i,Height -j, Width);
				bytes.put(map.get(index));
			}
		}
		else
		{
			for (int i = 0;i < newHeight; i++)
				for(int j = 0;j < newWidth; j++)
				{
					index = getIndex(Width-i-1,j, Width);
					bytes.put(map.get(index));
				}
		}
		bytes.rewind();
		map = null;
		
		bmp = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
		bmp.copyPixelsFromBuffer(bytes);
		bytes = null;
		return bmp;
	}*/
	
	public static Bitmap FlipBitmap (Bitmap bmp, boolean vertical)
	{
		
		/*int w = bmp.getWidth();
		int h = bmp.getHeight();
		
		IntBuffer map = IntBuffer.allocate(w*h);
		bmp.copyPixelsToBuffer(map);		
		bmp.recycle();
		bmp = null;
		
		IntBuffer data = IntBuffer.allocate(w*h);
		
		if (vertical)
			for (int i = h-1; i >= 0;i--)
				for (int j = 0; j < w;j++)
					data.put(map.get(getIndex(j, i, w)));
			
		else
			for (int i = 0; i < h;i++)
				for (int j = w-1; j >= 0;j--)
					data.put(map.get(getIndex(j, i, w)));
		
		map = null;
		data.rewind();
		bmp = Bitmap.createBitmap(w, h,Config.ARGB_8888);
		bmp.copyPixelsFromBuffer(data);
		data = null;
		return bmp;		*/
		Mat src = new Mat();
		int flipCode;
		if (vertical)
			flipCode = 0;
		else
			flipCode = 1;
		Utils.bitmapToMat(bmp, src);
		Core.flip(src, src, flipCode);
		Utils.matToBitmap(src, bmp);
		src.release();
		return bmp;
		
	}
	
	
	
		
	public static Bitmap ReturnScale (Bitmap image,int Width, int Height)
	{
		if (image.getWidth() > Width || image.getHeight() > Height)
		{
			float ratio = (float)image.getHeight()/(float)image.getWidth();
			int sWidth = Width;
			int sHeight = (int) (ratio * sWidth);
						
				if(sHeight > Height)
				{
					sHeight = Height;
					sWidth = (int) (sHeight/ratio);
				}
				
				if (sWidth > image.getWidth())
					sWidth = image.getWidth();
				if (sHeight > image.getHeight())
					sHeight = image.getHeight();
				
				Bitmap tmp = Bitmap.createScaledBitmap(image, sWidth, sHeight, true);
			
				image.recycle();
				image = null;
			return tmp;
		}
		
		Bitmap tmp = image.copy(Config.ARGB_8888, true);
		image.recycle();
		image = null;
		return tmp;
	}
	
	public static Bitmap Gaussian (Bitmap bmp, int size, float ratio)
	{
		if (size == 0)
			return bmp;
		
		int SIZE_MAX = 7;
		int SIZE_MIN = 1;
		int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
		int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		if (ratio > 1)
			mSIZE = (int) (mSIZE * ratio);
		
		if (mSIZE % 2 == 0)
			mSIZE++;
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		
		Mat src = new Mat();
		
		Utils.bitmapToMat(bmp, src);
		
		bmp.recycle();
		Imgproc.GaussianBlur(src, src, new Size(mSIZE,mSIZE), mSIZE,mSIZE);
		
		bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Utils.matToBitmap(src, bmp);
		src.release();
		return bmp;
	}
	
	
	public static Bitmap Pixelate (Bitmap image, int size,float ratio)
	{
		if (size != 0)
		{
		int SIZE_MAX = 15;
		int SIZE_MIN = 3;
		int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
		int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		if (ratio > 1)
			mSIZE = (int) (mSIZE * ratio);
		if (mSIZE % 2 == 0)
			mSIZE++;
		
		int iWidth = image.getWidth();
		int iHeight = image.getHeight();
		
		int perByte = image.getRowBytes()/image.getWidth();
		
		float width = iWidth/mSIZE;
		float height = iHeight/mSIZE;
		int widthExtra = (int) (iWidth - (width * mSIZE));
		int heightExtra = (int) (iHeight - (height * mSIZE));
		
		ByteBuffer map = ByteBuffer.allocateDirect(image.getRowBytes() * image.getHeight());
		image.copyPixelsToBuffer(map);
		
		map.rewind();
		
		Config config = image.getConfig();
		image.recycle();
		
		Point midpoint = new Point();
		int pixel;
		for (int i = 0;i < height;i++)
			for(int j = 0;j < width;j++)
			{
				midpoint.y = (i*mSIZE + mSIZE/2);
				midpoint.x = (j*mSIZE + mSIZE/2);
				
				pixel = map.getInt(getIndex(midpoint.x, midpoint.y, iWidth)*perByte);
								
				for (int a = 0; a < mSIZE;a++)
					for(int b = 0;b < mSIZE;b++)
						map.putInt(getIndex((j*mSIZE) + b, (i*mSIZE) + a, iWidth) * perByte, pixel);
						
			}
		if (widthExtra != 0 || heightExtra != 0)
		{
			if (widthExtra != 0)
			{
				int x = (int) (width * mSIZE);
				midpoint.x = (x + widthExtra/2);
				
				for (int i = 0;i < height; i++)
				{
					midpoint.y = (i*mSIZE + mSIZE/2);
					pixel = map.getInt(getIndex(midpoint.x, midpoint.y, iWidth)*perByte);
				
					for (int a = 0; a < mSIZE;a++)
						for(int b = 0;b < widthExtra;b++)
							map.putInt(getIndex(x + b, (i*mSIZE) + a, iWidth) * perByte, pixel);
				}
			}
			
			if (heightExtra != 0)
			{
				int y = (int) (height * mSIZE);
				midpoint.y = (y + heightExtra/2);
				
				for (int i = 0;i < width; i++)
				{
					midpoint.x = (i*mSIZE + mSIZE/2);
					pixel = map.getInt(getIndex(midpoint.x, midpoint.y, iWidth) * perByte);
				
					for (int a = 0; a < heightExtra;a++)
						for(int b = 0;b < mSIZE;b++)
							map.putInt(getIndex((i*mSIZE) + b, y + a, iWidth) * perByte, pixel);
							
				}
			}
		}
		if (widthExtra != 0 && heightExtra !=0)
		{
			int x = (int) (width * mSIZE);
			int y = (int) (height * mSIZE);
			midpoint.x = (x + widthExtra/2);
			midpoint.y = (y + heightExtra/2);
			
			pixel = map.getInt(getIndex(midpoint.x, midpoint.y, iWidth) * perByte);
			
			for (int a = 0; a < heightExtra;a++)
				for(int b = 0;b < widthExtra;b++)
					map.putInt(getIndex(x + b, y + a, iWidth) * perByte, pixel);
		}
		
		image = Bitmap.createBitmap(iWidth, iHeight, config);
		
		if (map.position() != 0)
			map.position(0);
		image.copyPixelsFromBuffer(map);
		map = null;
		System.gc();
		}
		return image;
	}
	
	public static Bitmap GlassEffect (Bitmap image, int size,float ratio)
	{
		if (size != 0)
		{
		int SIZE_MAX = 15;
		int SIZE_MIN = 3;
		int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
		int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		if (ratio != 0)
			mSIZE = (int) (mSIZE * ratio);
		int width = image.getWidth();
		int height = image.getHeight();
		
		int perByte = image.getRowBytes()/width;
		
		ByteBuffer map = ByteBuffer.allocateDirect(image.getRowBytes() * height);
				
		image.copyPixelsToBuffer(map);
		Config config = image.getConfig();
		
		image.recycle();
		
		map.rewind();
		
		ByteBuffer result = ByteBuffer.allocateDirect(map.limit());
		
		int pixel;
		Point randPoint = new Point();
		Random r = new Random();
		
		for(int i = 0;i < height;i++)
			for(int j = 0;j < width;j++)
			{
				randPoint.x = j + r.nextInt(mSIZE);
				randPoint.y = i + r.nextInt(mSIZE);
				
				if (randPoint.x >= width)
					randPoint.x = j - r.nextInt(mSIZE);
				if (randPoint.y >= height)
					randPoint.y = i - r.nextInt(mSIZE);
				
				pixel = map.getInt(getIndex(randPoint.x, randPoint.y, width)*perByte);
				result.putInt(pixel);
			}
		map = null;
		System.gc();
		image = Bitmap.createBitmap(width,height,config);
		result.position(0);
		
		image.copyPixelsFromBuffer(result);
		result = null;
		System.gc();
		}
		return image;
	}
	public static Bitmap LinearBlur (Bitmap image,int size, float ratio)
	{
		if (size != 0)
		{
		int SIZE_MAX = 7;
		int SIZE_MIN = 1;
		int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
		int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		if (ratio > 1)
			mSIZE = (int) (mSIZE * ratio);
		if (mSIZE % 2 == 0)
			mSIZE++;
		
		Mat src = new Mat();
		Utils.bitmapToMat(image, src);
		src.convertTo(src, CvType.CV_8U);
		Imgproc.medianBlur(src, src, mSIZE);
		Utils.matToBitmap(src, image);
		src.release();
		}
		return image;
	}
	public static Bitmap Emboss (Bitmap image,int size,boolean StoneEffect)
	{
		if (size != 0)
		{
			int SIZE_MAX = 15;
			int SIZE_MIN = 2;
			int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
			int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		
			float[][] filterEmboss = {
					{-mSIZE, -1,0},
					{-1,	1, 1 },
					{0,	1, mSIZE }
			};
		
			if (!StoneEffect)
				image = applyFilter(image, filterEmboss, 0, 1, 3);
			else
			{
				image = applyFilter(image, filterEmboss, 128, 1, 3);
				Mat src = new Mat();
				Utils.bitmapToMat(image, src);
				Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
				Utils.matToBitmap(src, image);
				src.release();
			}
		
		}
		return image;
	}
	public static Bitmap BoxBlur (Bitmap image,int size, float ratio) 
	{
		if (size != 0)
		{
		int SIZE_MAX = 7;
		int SIZE_MIN = 1;
		int SIZE_RANGE = SIZE_MAX - SIZE_MIN;
		
		int mSIZE = (int) ((SIZE_RANGE * ((float)size/100)) + SIZE_MIN);
		if (ratio > 1)
			mSIZE = (int) (mSIZE * ratio);
		
		if (mSIZE % 2 == 0)
			mSIZE++;
	
		Mat src = new Mat();
		Mat dst = new Mat();
		Utils.bitmapToMat(image, src);
		Imgproc.boxFilter(src, dst, -1, new Size(mSIZE, mSIZE),new org.opencv.core.Point(-1, -1), true);
		src.release();
		Utils.matToBitmap(dst, image);
		dst.release();
		}
		return image;
	}
	public static Bitmap Sharpen (Bitmap image,int size, float ratio)
	{
		if (size != 0)
		{
		
		int mRadius = 7;
		if (ratio > 1)
			mRadius = (int) (mRadius * ratio);
		if (mRadius % 2 == 0)
			mRadius++;
		
		float mAmount = 2 * (size/100f);
		Mat src = new Mat();
		Mat dst = new Mat();
		Utils.bitmapToMat(image, src);
		
		Mat blur = new Mat();
		Imgproc.GaussianBlur(src, blur, new Size(mRadius, mRadius),mRadius,mRadius);
		Core.addWeighted(src,mAmount, blur,-mAmount, 0, dst);
		Core.addWeighted(src, 1, dst, 1, 0, src);
		blur.release();
		dst.release();
		Utils.matToBitmap(src, image);
		src.release();
		}
		return image;
	}
	public static Bitmap Lighten (Bitmap image, float size)
	{
		if (size != 0)
		{
		float SIZE_MAX = 2f;
		float SIZE_MIN = 1.1f;
		float SIZE_RANGE = (SIZE_MAX - SIZE_MIN);
		
		float mSIZE = (SIZE_RANGE * (size/100)) + SIZE_MIN;
		
		float[][] filterLighten = {
				{0, 0, 0},
				{0, mSIZE, 0},
				{0, 0, 0}
		};
		
		image = applyFilter(image, filterLighten, 0, 1, 3);
		}
		return image;
	}
	public static Bitmap Darken (Bitmap image, float size)
	{
		if (size != 0)
		{
		float SIZE_MAX = 0.5f;
		float SIZE_MIN = 0.1f;
		float SIZE_RANGE = (SIZE_MAX - SIZE_MIN);
		
		float mSIZE = (SIZE_RANGE * (size/100)) + SIZE_MIN;
		mSIZE = SIZE_MAX - mSIZE + SIZE_MIN;
		
		float[][] filterDark = {
				{0, 0, 0},
				{0, mSIZE, 0},
				{0, 0, 0}
		};
		
		image = applyFilter(image, filterDark, 0, 1, 3);
		}
		return image;
	}
	
	public static Bitmap Dilate (Bitmap image, float size,float ratio)
	{
		if (size != 0)
		{
		float SIZE_MAX = 5f;
		float SIZE_MIN = 1f;
		float SIZE_RANGE = (SIZE_MAX - SIZE_MIN);
		
		float mSIZE = (SIZE_RANGE * (size/100)) + SIZE_MIN;

		if (mSIZE % 2 == 0)
			mSIZE++;
		
		if (ratio < 1f)
			ratio = 1;
		Mat src = new Mat();
		Mat dst = new Mat();
		Utils.bitmapToMat(image, src);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(mSIZE, mSIZE));
		Imgproc.dilate(src, dst, element, new org.opencv.core.Point(-1, -1), (int) ratio);
		element.release();
		src.release();
		Utils.matToBitmap(dst, image);
		dst.release();
		}
		return image;
	}
	
	public static Bitmap Erode (Bitmap image, float size,float ratio)
	{
		if (size != 0)
		{
		float SIZE_MAX = 5f;
		float SIZE_MIN = 1f;
		float SIZE_RANGE = (SIZE_MAX - SIZE_MIN);
		
		float mSIZE = (SIZE_RANGE * (size/100)) + SIZE_MIN;
		if (mSIZE % 2 == 0)
			mSIZE++;
		
		if (ratio < 1f)
			ratio = 1;
		Mat src = new Mat();
		Mat dst = new Mat();
		Utils.bitmapToMat(image, src);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(mSIZE, mSIZE));
		Imgproc.erode(src, dst, element, new org.opencv.core.Point(-1, -1),(int) ratio);
		src.release();
		Utils.matToBitmap(dst, image);
		dst.release();
		}
		return image;
	}
	private static Bitmap applyFilter (Bitmap image,float[][] matrix, double offset,float divider,int kSize)
	{
		Mat src = new Mat();
		
		Utils.bitmapToMat(image, src);
		//int width = image.getWidth();
		//int height = image.getHeight();
		//image.recycle();
	
		for (int i = 0;i < kSize;i++)
			for(int j=0; j < kSize;j++)
				matrix[i][j] /= divider;
		
		int ddepth = -1;
				
		Mat kernel = new Mat(kSize, kSize, CvType.CV_32F);
		for(int i = 0;i < kSize;i++)
			for (int j = 0;j < kSize;j++)
				kernel.put(i, j, matrix[i][j]);
		
		Mat dst = new Mat();
		
		Imgproc.filter2D(src, dst, ddepth, kernel);
		src.release();
		kernel.release();
				
		dst.convertTo(dst, CvType.CV_8UC4);
		/*image.recycle();
		image = Bitmap.createBitmap(width, height, Config.ARGB_8888);*/
		Utils.matToBitmap(dst, image);
		
		dst.release();
		return image;
	}
	public static Bitmap MakeBlacknWhite (Bitmap image,int size){
		if (size == 0)
			return image;
		float intensity;
			intensity = ((200 - 75) * (size/100f)) + 75;
		Mat src = new Mat();
		Utils.bitmapToMat(image, src);
		
		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(src, src, intensity, 200, Imgproc.THRESH_BINARY);
		
		Utils.matToBitmap(src, image);
		src.release();
		return image;
	}
	
	public static Bitmap RGBAdjust(Bitmap image,float[] matrices){
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrices);
		
		Paint p = new Paint();
		p.setFilterBitmap(true);
		p.setColorFilter(filter);		
		
		Bitmap img = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(img);
		c.drawBitmap(image, 0, 0, p);
		image.recycle();
		c = null;
		return img;
	}
	public static Bitmap HSVTransition(Bitmap image,int H,int S,int V)
	{
		int MID_POINT = 50;
		int width = image.getWidth();
		int height = image.getHeight();
		
		float Hue,Saturate,Value;
		
		if (H == MID_POINT)
			Hue = 0;
		else
		{
			Hue = H - MID_POINT;
			Hue = (H/50f) * 179.9f;
			//Hue = getNormTheta(Hue);
		}
		
		if (S == MID_POINT)
			Saturate = 1;
		else
		{
			Saturate = (S - MID_POINT)/50f;
			Saturate = 1f + Saturate;			
		}
			
		if (V == MID_POINT)
			Value = 1;
		else
		{
			Value = (V - MID_POINT)/50f;
			Value = 1f + Value;
		}
		
		float LUMA_R = 0.299f;
		float LUMA_G = 0.587f;
		float LUMA_B = 0.114f;
		
		float cos = (float) Math.cos(Math.toRadians(Hue));
		float sin = (float) Math.sin(Math.toRadians(Hue));
		
		float[] hueMat = {
				(LUMA_R + (cos * (1 - LUMA_R))) + (sin * -(LUMA_R)), ((LUMA_G + (cos * -(LUMA_G))) + (sin * -(LUMA_G))), (LUMA_B + (cos * -(LUMA_B)) + (sin * (1 - LUMA_B))), 0, 0,
                (float) ((LUMA_R + (cos * -(LUMA_R))) + (sin * 0.143)), (float) ((LUMA_G + (cos * (1 - LUMA_G))) + (sin * 0.14)), (float) ((LUMA_B + (cos * -(LUMA_B))) + (sin * -0.283)), 0, 0,
                ((LUMA_R + (cos * -(LUMA_R))) + (sin * -((1 - LUMA_R)))), ((LUMA_G + (cos * -(LUMA_G))) + (sin * LUMA_G)), ((LUMA_B + (cos * (1 - LUMA_B))) + (sin * LUMA_B)), 0, 0,
                0, 0, 0, 1, 0
		};
		
		
		ColorMatrix matrix = new ColorMatrix();
		matrix.set(hueMat);
		
		Bitmap tmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(tmp);
		
		Paint p = new Paint();
		p.setColorFilter(new ColorMatrixColorFilter(matrix));
		p.setFilterBitmap(true);
		
		c.drawBitmap(image, 0, 0, p);
		
		matrix.reset();
		matrix.setSaturation(Saturate);
		p.setColorFilter(new ColorMatrixColorFilter(matrix));
		
		c.setBitmap(image);
		c.drawBitmap(tmp, 0, 0, p);
		matrix.reset();
		matrix.setScale(Value, Value, Value, 1);
		p.setColorFilter(new ColorMatrixColorFilter(matrix));
		
		c.setBitmap(tmp);
		c.drawBitmap(image, 0, 0, p);
		
		image.recycle();		
		matrix = null;
		p = null;
		c = null;
		
		return tmp;
	}
	
	public static Bitmap RGBTransition(Bitmap image, RGB from, RGB to)
	{
		
		float[] matrices = new float[20];
	
		if ((from == RGB.RED && to == RGB.GREEN) || (to == RGB.RED && from == RGB.GREEN))
	 	{
	 		float[] t = {0,1,0,0,0,
	 				 	 1,0,0,0,0,
	 				 	 0,0,1,0,0,
	 				 	 0,0,0,1,0};
	 		matrices = t;
	 	}
	 	else if ((from == RGB.RED && to == RGB.BLUE) || (to == RGB.RED && from == RGB.BLUE))
	 	{
	 		float[] t = {0,0,1,0,0,
					 	 0,1,0,0,0,
					 	 1,0,0,0,0,
					 	 0,0,0,1,0};
	 		matrices = t;
	 	}
	 	else if ((from == RGB.GREEN && to == RGB.BLUE) || (to == RGB.GREEN && from == RGB.BLUE))
	 	{
	 		float[] t = {1,0,0,0,0,
					 	 0,0,1,0,0,
					 	 0,1,0,0,0,
					 	 0,0,0,1,0};
	 		matrices = t;
	 	}
		
		image = RGBAdjust(image, matrices);
		return image;
	}
	
	public static void mapMagicTool (Bitmap src, float x, float y, float up, float down,
			ArrayList<Float> xarray, ArrayList<Float> yarray, ArrayList<Integer> action){
		
		if (x < 0 || y < 0 || x > src.getWidth() || y > src.getHeight())
			return;
		
		float intensity = 3 + (up/100f) * (40 - 3);
		
		Mat mat = new Mat();
		Mat mask = new Mat(mat.size(), CvType.CV_8UC1);
		
		Utils.bitmapToMat(src, mat);
		Mat hierarchy = new Mat();
		
		Imgproc.cvtColor(mat, mask, Imgproc.COLOR_BGR2GRAY);
		mat.release();
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		mat = Mat.zeros(mask.rows() + 2, mask.cols() + 2, CvType.CV_8UC1);
		
		Imgproc.floodFill(mask, mat, new org.opencv.core.Point(x, y), new Scalar(255, 255, 255)
		, new Rect(), new Scalar(intensity, intensity, intensity), new Scalar(intensity ,
				intensity, intensity),
				
		(255 << 8 ) + Imgproc.FLOODFILL_FIXED_RANGE + Imgproc.FLOODFILL_MASK_ONLY);
				
		//Imgproc.threshold(mask, mat, 10, 200, Imgproc.THRESH_BINARY);
		
		mask.release();
		mask = mat.submat(1, mat.rows() - 1, 1, mat.cols() - 1);
		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
		for (MatOfPoint mop:contours){
			List<org.opencv.core.Point> pointlist = mop.toList();
			action.add(0);
			xarray.add((float) pointlist.get(0).x);
			yarray.add((float) pointlist.get(0).y);
			for(org.opencv.core.Point point:pointlist){
				action.add(1);
				xarray.add((float) point.x);
				yarray.add((float) point.y);
			}
		}
		
		mat.release();
		mask.release();
	}
	private static int getIndex (int x,int y,int Width)
	{
		return ((y * Width) + x);
	}

}