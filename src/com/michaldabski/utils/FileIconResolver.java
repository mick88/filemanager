package com.michaldabski.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.michaldabski.filemanager.R;

public class FileIconResolver extends LruCache<String, Bitmap>
{	
	public static final int CACHE_SIZE = 5 * 1024 * 1024;
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
