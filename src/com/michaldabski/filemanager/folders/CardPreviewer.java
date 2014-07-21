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
package com.michaldabski.filemanager.folders;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.michaldabski.filemanager.R;
import com.michaldabski.utils.FilePreviewCache;
import com.michaldabski.utils.FileUtils;

import java.io.File;

public class CardPreviewer extends AsyncTask<File, Void, Bitmap>
{
	ImageView imageView;
	final FilePreviewCache thumbCache;
	File file;
	
	public CardPreviewer(ImageView imageView, FilePreviewCache thumbCache) 
	{
		this.imageView = imageView;
		this.thumbCache = thumbCache;
	}
	
	public void setImageView(ImageView imageView)
	{
		this.imageView = imageView;
		if (imageView != null) imageView.setImageResource(R.drawable.card_image_placeholder);
	}
	
	@Override
	protected Bitmap doInBackground(File... params)
	{
		file = params[0];
		try
		{
			Bitmap bitmap = thumbCache.get(file);
			if (bitmap == null) bitmap = FileUtils.getPreview(file);
			return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (imageView != null) imageView.setImageResource(R.drawable.card_image_placeholder);
	}
	
	@Override
	protected void onCancelled(Bitmap result)
	{
		if (result != null)
		{
			thumbCache.put(file, result);
		}
		super.onCancelled(result);
	}
	
	@Override
	protected void onPostExecute(Bitmap result)
	{
		super.onPostExecute(result);
		if (result == null)
		{
			if (imageView != null) imageView.setImageResource(R.drawable.card_image_error);
		}
		else
		{
			thumbCache.put(file, result);
			if (imageView != null) imageView.setImageBitmap(result);
		}
	}

}
