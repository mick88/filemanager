package com.michaldabski.fileexplorer;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;

import com.michaldabski.utils.FileUtils;

public class AppPreferences
{
	private static final String 
		NAME = "FileExplorerPreferences",
		
		PREF_START_FOLDER = "start_folder",
		PREF_SORT_BY = "sort_by";
	
	public static final int
		SORT_BY_NAME = 0,
		SORT_BY_TYPE = 1,
		SORT_BY_SIZE = 2;
	
	private final static String DEFAULT_START_FOLDER = "/";
	private final static int DEFAULT_SORT_BY = SORT_BY_NAME;
	
	File startFolder;
	int sortBy;
	
	private AppPreferences() {		
	}
	
	private void loadFromSharedPreferences(SharedPreferences sharedPreferences)
	{
		this.startFolder = new File(sharedPreferences.getString(PREF_START_FOLDER, DEFAULT_START_FOLDER));
		this.sortBy = sharedPreferences.getInt(PREF_SORT_BY, DEFAULT_SORT_BY);
	}
	
	private void saveToSharedPreferences(SharedPreferences sharedPreferences)
	{
		sharedPreferences.edit()
			.putString(PREF_START_FOLDER, startFolder.getAbsolutePath())
			.putInt(PREF_SORT_BY, sortBy)
			.commit();		
	}
	
	public void saveChangesAsync(final Context context)
	{
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				saveChanges(context);
				
			}
		}).run();
	}
	
	public void saveChanges(Context context)
	{
		saveToSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
	}

	public AppPreferences setStartFolder(File startFolder)
	{
		this.startFolder = startFolder;
		return this;
	}
	
	public AppPreferences setSortBy(int sortBy)
	{
		if (sortBy < 0 || sortBy > 2)
			throw new InvalidParameterException(String.valueOf(sortBy)+" is not a valid id of sorting order");
		
		this.sortBy = sortBy;
		return this;
	}
	
	public int getSortBy()
	{
		return sortBy;
	}
	
	public File getStartFolder()
	{
		return startFolder;
	}
	
	public Comparator<File> getFileSortingComparator()
	{
		switch (sortBy)
		{
			case SORT_BY_SIZE:
				return new FileUtils.FileSizeComparator();
				
			case SORT_BY_TYPE:
				return new FileUtils.FileExtensionComparator();
				
			default:
				return new FileUtils.FileNameComparator();
		}
	}
	
	public static AppPreferences loadPreferences(Context context)
	{
		AppPreferences instance = new AppPreferences();
		instance.loadFromSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
		return instance;
	}
}
