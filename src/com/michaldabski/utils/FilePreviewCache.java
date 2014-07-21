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
import android.util.LruCache;

import java.io.File;

public class FilePreviewCache extends LruCache<File, Bitmap>
{
	public static final int DEFAULT_CACHE_SZE = 24*1024*1024;
	/**
	 * create new cache
	 * @param maxSize max size in bytes
	 */
	public FilePreviewCache(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub
	}
	
	public FilePreviewCache()
	{
		this(DEFAULT_CACHE_SZE);
	}
	
	@Override
	protected int sizeOf(File key, Bitmap value)
	{
		return value.getByteCount();
	}

}
