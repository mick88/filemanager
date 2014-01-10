package com.michaldabski.utils;

import java.io.File;

import com.michaldabski.fileexplorer.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

public class FileIconResolver extends LruCache<String, Drawable>
{	
	final Context context;
	Drawable nullIcon=null;
	
	public FileIconResolver(Context context)
	{
		super(50);
		this.context = context;
	}
	
	public Drawable getNullIcon()
	{
		if (nullIcon == null)
			nullIcon = new BitmapDrawable(context.getResources(), 
					BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_file));
		return nullIcon;
	}
	
	public Drawable getFileIcon(File file)
	{
		String mimeType = FileUtils.getFileMimeType(file);
		if (mimeType == null) return getNullIcon();
		
		Drawable drawable = super.get(mimeType);
		if (drawable == null)
			drawable = IntentUtils.getAppIconForFile(file, context);
		put(mimeType, drawable);
		return drawable;
	}
}
