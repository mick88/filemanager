/*******************************************************************************
 * Copyright (c) 2014 Michal Dabski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.michaldabski.utils;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class BitmapUtils
{
	private static final String LOG_TAG = "BitmapUtils";

	public static Rect getBestFitRect(Bitmap bitmap, Rect destination)
	{
		return getBestFitRect(bitmap, (float)destination.width() / (float)destination.height());
	}
	
	public static Rect getBestFitRect(Bitmap bitmap, float ratio)
	{
		float bmpRatio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
			
		if (bmpRatio > ratio) // bmp is wider
		{
			int height = bitmap.getHeight();
			int width = (int) (height * ratio);
			int offset = (bitmap.getWidth() - width) / 2;
			return new Rect(offset, 0, offset+width, height);
		}
		else if (bmpRatio < ratio) // bmp is taller
		{
			int width = bitmap.getWidth();
			int height = (int) (width / ratio);
			int offset = (bitmap.getHeight() - height) / 2;
			return new Rect(0, offset, width, offset+height);
		}
		else return new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}
	
	public static Rect[] layoutImagesInGrid(Bitmap destination, int cols, int rows)
	{
		int numItems = cols*rows;
		int itemWidth = destination.getWidth() / cols,
			itemHeight = destination.getHeight() / rows;
		
		Rect[] result = new Rect[numItems];
		
		for (int i=0; i < numItems; i++)
		{
			int x = (i % cols) * itemWidth,
				y = (i / (rows+1)) * itemHeight;
			Log.d(LOG_TAG, x+"x"+y);
			result[i] = new Rect(x, y, x+itemWidth, y+itemHeight);
		}
		
		return result;
	}
}
