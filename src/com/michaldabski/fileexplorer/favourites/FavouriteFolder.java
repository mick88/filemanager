package com.michaldabski.fileexplorer.favourites;

import java.io.File;

import android.content.Context;

import com.michaldabski.fileexplorer.nav_drawer.NavDrawerShortcut;
import com.michaldabski.msqlite.Annotations.PrimaryKey;

public class FavouriteFolder extends NavDrawerShortcut
{
	private String label;
	@PrimaryKey
	private String path;
	
	public FavouriteFolder()
	{
		
	}
	
	public FavouriteFolder(File folder, String label)
	{
		this();
		if (folder.isDirectory() == false)
			throw new RuntimeException(folder.getName()+" is not a directory");
		this.path = folder.getAbsolutePath();
		this.label = label;
	}
	
	public FavouriteFolder(File folder)
	{
		this(folder, folder.getName());
	}
	
	public FavouriteFolder(String path, String label) 
	{
		this(new File(path), label);
	}

	public File getFile()
	{
		return new File(path);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FavouriteFolder)
			return ((FavouriteFolder) o).path.equals(path);
		else if (o instanceof File)
			return o.equals(getFile());
		return super.equals(o);
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	@Override
	public int hashCode()
	{
		return getFile().hashCode();
	}
	
	public boolean exists()
	{
		return getFile().exists();
	}

	@Override
	public CharSequence getTitle(Context context)
	{
		return label;
	}

}
