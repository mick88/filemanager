package com.michaldabski.fileexplorer.folders;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.FilePreviewCache;

public class CardPreviewer extends AsyncTask<File, Void, Bitmap>
{
	private static final int THUMBNAIL_SIZE = 256;
	final ImageView imageView;
	final FilePreviewCache thumbCache;
	File file;
	
	public CardPreviewer(ImageView imageView, FilePreviewCache thumbCache) 
	{
		this.imageView = imageView;
		this.thumbCache = thumbCache;
	}
	
	Bitmap getPreview(File image) 
	{
		if (image.isDirectory()) return null;
	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    if (originalSize > imageView.getWidth()) 
	    	opts.inSampleSize = originalSize / imageView.getWidth();
	    return BitmapFactory.decodeFile(image.getPath(), opts);     
	}
	
	@Override
	protected Bitmap doInBackground(File... params)
	{
		file = params[0];
		try
		{
			Bitmap bitmap = thumbCache.get(file);
			if (bitmap == null) bitmap = getPreview(file);
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
		imageView.setImageResource(R.drawable.ic_launcher);
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
			
		}
		else
		{
			thumbCache.put(file, result);
			imageView.setImageBitmap(result);
		}
	}

}
