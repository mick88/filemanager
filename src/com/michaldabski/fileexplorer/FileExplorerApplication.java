package com.michaldabski.fileexplorer;

import android.app.Application;

public class FileExplorerApplication extends Application
{
	AppPreferences appPreferences=null;
	
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
}
