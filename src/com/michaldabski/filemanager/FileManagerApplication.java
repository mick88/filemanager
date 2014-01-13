package com.michaldabski.filemanager;

import android.app.Application;

import com.michaldabski.filemanager.favourites.FavouritesManager;
import com.michaldabski.utils.FileIconResolver;

public class FileManagerApplication extends Application
{
	AppPreferences appPreferences=null;
	FavouritesManager favouritesManager=null;
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
	
	public FavouritesManager getFavouritesManager()
	{
		if (favouritesManager == null)
			favouritesManager = new FavouritesManager(getApplicationContext());
		return favouritesManager;
	}

	public FileIconResolver getFileIconResolver()
	{
		if (fileIconResolver == null)
			fileIconResolver = new FileIconResolver(getApplicationContext());
		return fileIconResolver;
	}
}
