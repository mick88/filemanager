package com.michaldabski.fileexplorer;

import com.michaldabski.fileexplorer.favourites.FavouritesManager;

import android.app.Application;

public class FileExplorerApplication extends Application
{
	AppPreferences appPreferences=null;
	FavouritesManager favouritesManager=null;
	
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
	
	public FavouritesManager getFavouritesManager()
	{
		if (favouritesManager == null)
			favouritesManager = new FavouritesManager(getApplicationContext());
		return favouritesManager;
	}
}
