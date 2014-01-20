package com.michaldabski.filemanager.folders;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.michaldabski.filemanager.R;
import com.michaldabski.utils.BitmapUtils;
import com.michaldabski.utils.FilePreviewCache;
import com.michaldabski.utils.FileUtils;

public class CardPreviewer extends AsyncTask<File, Void, Bitmap>
{
	private static final int THUMBNAIL_SIZE = 256;
	private static final int NUM_FOLDER_PREVIEWS = 6;
	private static final int PREVIEW_WIDTH = (int) (THUMBNAIL_SIZE * 1.3),
			PREVIEW_HEIGHT = THUMBNAIL_SIZE;
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
	
	Bitmap buildFolderPreview(File folder)
	{
		List<Bitmap> previews = new ArrayList<Bitmap>(NUM_FOLDER_PREVIEWS);
		getBitmapsInFolder(folder, previews);
		if (previews.isEmpty()) return null;
		
		Bitmap folderPreview = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		Rect[] destRect = BitmapUtils.layoutImagesInGrid(folderPreview, 3, 2);
		Canvas canvas = new Canvas(folderPreview);
		Paint paint = new Paint();
		Rect srcRect = new Rect();
		int i=0;
		for (Bitmap bitmap : previews)
		{
			srcRect = BitmapUtils.getBestFitRect(bitmap, destRect[i]);
			canvas.drawBitmap(bitmap, srcRect, destRect[i++], paint);
			bitmap.recycle();
		}
		return folderPreview;
	}
	
	void getBitmapsInFolder(File folder, List<Bitmap>previews)
	{
		File [] files = folder.listFiles(FileUtils.DEFAULT_FILE_FILTER);
		Arrays.sort(files, new Comparator<File>()
		{

			@Override
			public int compare(File lhs, File rhs)
			{
				if (lhs.lastModified() > rhs.lastModified()) return -1;
				else if (lhs.lastModified() < rhs.lastModified()) return 1;
				else return 0;
			}
			
		});
		for (File file : files) 
			if (file.isDirectory() == false)
		{
			Bitmap bitmap = getPreview(file);
			if (bitmap != null)
			{
				previews.add(bitmap);
				if (previews.size() == NUM_FOLDER_PREVIEWS)
					break;
			}
		}
	}
	
	Bitmap getPreview(File image) 
	{
		if (image.isDirectory()) return buildFolderPreview(image);
	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
	            : bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
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
