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
package com.michaldabski.filemanager.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.michaldabski.filemanager.R;
import com.michaldabski.filemanager.favourites.FavouriteFolder;
import com.michaldabski.msqlite.MSQLiteOpenHelper;
import com.michaldabski.utils.FileUtils;

public class SQLiteHelper extends MSQLiteOpenHelper
{
	private static final String DB_NAME = "file_manager.db";
	private static final int DB_VERSION = 3;
	
	private final Context context;

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION,
				new Class[] { FavouriteFolder.class });
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		super.onCreate(db);
		
		List<FavouriteFolder> favouriteFolders = new ArrayList<FavouriteFolder>();
		if (Environment.getExternalStorageDirectory().isDirectory())
		{
			favouriteFolders.add(new FavouriteFolder(Environment.getExternalStorageDirectory(), FileUtils.DISPLAY_NAME_SD_CARD));
			if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).isDirectory())
				favouriteFolders.add(new FavouriteFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.downloads)));
			if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).isDirectory())
				favouriteFolders.add(new FavouriteFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), getString(R.string.music)));
			if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).isDirectory())
				favouriteFolders.add(new FavouriteFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), getString(R.string.photos)));
		}
		else favouriteFolders.add(new FavouriteFolder(Environment.getExternalStoragePublicDirectory("/"), getString(R.string.root)));
		
		for (FavouriteFolder favouriteFolder : favouriteFolders)
		{
			if (favouriteFolder.exists())
				insert(db, favouriteFolder);
		}
		
	}
	
	protected String getString(int res)
	{
		return context.getString(res);
	}

}
