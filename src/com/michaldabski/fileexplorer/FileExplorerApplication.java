package com.michaldabski.fileexplorer;

import com.michaldabski.utils.FileIconResolver;

import android.app.Application;

public class FileExplorerApplication extends Application
{
	AppPreferences appPreferences=null;
	FileIconResolver fileIconResolver = null;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	public AppPreferences getAppPreferences()
	{
		if (appPreferences == null)
			appPreferences = AppPreferences.loadPreferences(getApplicationContext());

		return appPreferences;
	}
	
	public FileIconResolver getFileIconResolver()
	{
		if (fileIconResolver == null)
			fileIconResolver = new FileIconResolver(getApplicationContext());
		return fileIconResolver;
	}
}
