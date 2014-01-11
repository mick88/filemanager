package com.michaldabski.fileexplorer.favourites;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.michaldabski.fileexplorer.sqlite.SQLiteHelper;

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
		void onFavouritesChanged();
	}
	
	private final List<FavouriteFolder> folders;
	private final SQLiteHelper sqLiteHelper;

	public FavouritesManager(Context context) 
	{
		this.sqLiteHelper = new SQLiteHelper(context);
		this.folders = sqLiteHelper.selectAll(FavouriteFolder.class);
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
	}
	
	public boolean isFolderFavourite(File file)
	{
		for (FavouriteFolder folder : folders)
			if (folder.equals(file))
				return true;

		return false;
	}

}
