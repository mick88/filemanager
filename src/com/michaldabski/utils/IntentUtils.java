package com.michaldabski.utils;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class IntentUtils
{
	public static List<ResolveInfo> getAppsThatHandleFile(File file, Context context)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(FileUtils.getFileMimeType(file));
		intent.setData(Uri.fromFile(file));
		return getAppsThatHandleIntent(intent, context);
	}
	
	public static List<ResolveInfo> getAppsThatHandleIntent(Intent intent, Context context)
	{
		PackageManager packageManager = context.getPackageManager();
		return packageManager.queryIntentActivities(intent, 0);
	}
	
	public static Drawable getAppIconForFile(File file, Context context)
	{
		List<ResolveInfo> infos = getAppsThatHandleFile(file, context);
		PackageManager packageManager = context.getPackageManager();
		for (ResolveInfo info : infos)
		{
			Drawable drawable = info.loadIcon(packageManager);
			if (drawable != null)
				return drawable;
		}
		return null;
	}
}
