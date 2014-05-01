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

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.michaldabski.filemanager.R;

public class FileIconResolver extends LruCache<String, Bitmap>
{	
	public static final int CACHE_SIZE = 5242880; // 5 * 1024 * 1024
	final Context context;
	Bitmap nullIcon=null;
	
	public FileIconResolver(Context context)
	{
		super(CACHE_SIZE);
		this.context = context;
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value)
	{
		return value.getByteCount();
	}
	
	public Bitmap getNullIcon()
	{
		if (nullIcon == null)
			nullIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_file);
		return nullIcon;
	}
	
	public Bitmap getFileIcon(File file)
	{
		String mimeType = FileUtils.getFileMimeType(file);
		if (mimeType == null) return getNullIcon();
		
		Bitmap bitmap = super.get(mimeType);
		if (bitmap == null)
			bitmap = FileUtils.createFileIcon(file, context, false);
		put(mimeType, bitmap);
		return bitmap;
	}
}
