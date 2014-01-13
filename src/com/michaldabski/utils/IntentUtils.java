package com.michaldabski.utils;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.michaldabski.filemanager.MainActivity;

public class IntentUtils
{
	
	public static Intent createFileOpenIntent(File file)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);		
		intent.setDataAndType(Uri.fromFile(file), FileUtils.getFileMimeType(file));
		return intent;
	}
	
	public static void createShortcut(Context context, File file)
	{
		final Intent shortcutIntent;
		if (file.isDirectory())
		{
			shortcutIntent = new Intent(context, MainActivity.class);
			shortcutIntent.putExtra(MainActivity.EXTRA_DIR, file.getAbsolutePath());
		}
		else 
		{
			shortcutIntent = createFileOpenIntent(file);
		}
		
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, file.getName());
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, FileUtils.createFileIcon(file, context, true));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}
	
	public static List<ResolveInfo> getAppsThatHandleFile(File file, Context context)
	{
		return getAppsThatHandleIntent(createFileOpenIntent(file), context);
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
