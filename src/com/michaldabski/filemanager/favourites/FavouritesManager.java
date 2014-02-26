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
package com.michaldabski.filemanager.favourites;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

import com.michaldabski.filemanager.sqlite.SQLiteHelper;

public class FavouritesManager
{
	public static class FolderAlreadyFavouriteException extends Exception
	{

		public FolderAlreadyFavouriteException(FavouriteFolder folder) {
			super(folder.toString() + " is already bookmarked");
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static interface FavouritesListener
	{
		void onFavouritesChanged(FavouritesManager favouritesManager);
	}
	
	private final List<FavouriteFolder> folders;
	private final SQLiteHelper sqLiteHelper;
	private final Set<FavouritesListener> favouritesListeners;

	public FavouritesManager(Context context) 
	{
		this.sqLiteHelper = new SQLiteHelper(context);
		this.favouritesListeners = new HashSet<FavouritesManager.FavouritesListener>();
		this.folders = sqLiteHelper.selectAll(FavouriteFolder.class);
		sort();
		fixFavouritesOrder();
	}
	
	public void sort()
	{
		Collections.sort(folders);
	}
	
	private void fixFavouritesOrder()
	{
		int lastOrder=0;
		for (FavouriteFolder folder : folders)
		{
			if (folder.hasValidOrder() == false || folder.getOrder() <= lastOrder)
			{
				folder.setOrder(lastOrder+1);
			}
				
			lastOrder = folder.getOrder();
		}
	}
	
	public void addFavouritesListener(FavouritesListener favouritesListener)
	{
		favouritesListeners.add(favouritesListener);
	}
	
	public void removeFavouritesListener(FavouritesListener favouritesListener)
	{
		favouritesListeners.remove(favouritesListener);
	}
	
	void notifyListeners()
	{
		for (FavouritesListener listener : favouritesListeners)
			listener.onFavouritesChanged(this);
	}
	
	public List<FavouriteFolder> getFolders()
	{
		return folders;
	}
	
	public void addFavourite(FavouriteFolder favouriteFolder) throws FolderAlreadyFavouriteException
	{
		long id = sqLiteHelper.insert(favouriteFolder);
		if (id == -1) throw new FolderAlreadyFavouriteException(favouriteFolder);
		folders.add(favouriteFolder);
		notifyListeners();
	}
	
	public void removeFavourite(File file)
	{
		for (FavouriteFolder folder : folders) if (folder.equals(file))
			{
				removeFavourite(folder);
				break;
			}
	}
	
	public void removeFavourite(FavouriteFolder favouriteFolder)
	{
		folders.remove(favouriteFolder);
		sqLiteHelper.delete(favouriteFolder);
		notifyListeners();
	}
	
	public boolean isFolderFavourite(File file)
	{
		for (FavouriteFolder folder : folders)
			if (folder.equals(file))
				return true;

		return false;
	}

}
