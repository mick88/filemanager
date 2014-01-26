package com.michaldabski.filemanager.folders;

import java.io.File;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.michaldabski.filemanager.R;
import com.michaldabski.utils.FilePreviewCache;
import com.michaldabski.utils.FileUtils;

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
