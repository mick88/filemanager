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
package com.michaldabski.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.michaldabski.utils.FileUtils;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Comparator;

public class AppPreferences
{
	private static final String 
		NAME = "FileExplorerPreferences",
		
		PREF_START_FOLDER = "start_folder",
		PREF_CARD_LAYOUT = "card_layout",
		PREF_SORT_BY = "sort_by";
	
	public static final int
		SORT_BY_NAME = 0,
		SORT_BY_TYPE = 1,
		SORT_BY_SIZE = 2;
	
	public static final int
		CARD_LAYOUT_MEDIA = 0,
		CARD_LAYOUT_ALWAYS = 1,
		CARD_LAYOUT_NEVER = 2;
	
	private final static int DEFAULT_SORT_BY = SORT_BY_NAME;
	
	File startFolder;
	int sortBy;
	int cardLayout;
	
	private AppPreferences() {		
	}
	
	private void loadFromSharedPreferences(SharedPreferences sharedPreferences)
	{
		String startPath = sharedPreferences.getString(PREF_START_FOLDER, null);
		if (startPath == null)
		{
			if (Environment.getExternalStorageDirectory().list() != null)
				startFolder = Environment.getExternalStorageDirectory();
			else 
				startFolder = new File("/");
		}
		else this.startFolder = new File(startPath);
		this.sortBy = sharedPreferences.getInt(PREF_SORT_BY, DEFAULT_SORT_BY);
		this.cardLayout = sharedPreferences.getInt(PREF_CARD_LAYOUT, CARD_LAYOUT_MEDIA);
	}
	
	private void saveToSharedPreferences(SharedPreferences sharedPreferences)
	{
		sharedPreferences.edit()
			.putString(PREF_START_FOLDER, startFolder.getAbsolutePath())
			.putInt(PREF_SORT_BY, sortBy)
			.putInt(PREF_CARD_LAYOUT, cardLayout)
			.apply();
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
	
	public void setCardLayout(int cardLayout)
	{
		this.cardLayout = cardLayout;
	}
	
	public int getCardLayout()
	{
		return cardLayout;
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
