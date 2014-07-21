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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.michaldabski.filemanager.folders.FolderActivity;

import java.io.File;
import java.util.List;

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
			shortcutIntent = new Intent(context, FolderActivity.class);
			shortcutIntent.putExtra(FolderActivity.EXTRA_DIR, file.getAbsolutePath());
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
