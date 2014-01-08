package com.michaldabski.fileexplorer.nav_drawer;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.MainActivity;
import com.michaldabski.fileexplorer.R;
import com.michaldabski.fileexplorer.folders.FolderFragment;
import com.michaldabski.fileexplorer.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.FileUtils;

public class NavDrawerShortcut implements NavDrawerItem
{
	String name;
	File folder;
	
	public NavDrawerShortcut(File folder, String name)
	{
		this.folder = folder;
		this.name = name;
	}
	
	@Override
	public boolean onClicked(MainActivity activity)
	{
		Bundle args = new Bundle();
		args.putString(FolderFragment.EXTRA_DIR, folder.getAbsolutePath());
		FolderFragment folderFragment = new FolderFragment();
		folderFragment.setArguments(args);
		activity.showFragment(folderFragment);
		return true;
	}

	@Override
	public CharSequence getTitle(Context context)
	{
		return name;
	}
	
	@Override
	public CharSequence getSubTitle(Context context)
	{
		return FileUtils.getUserFriendlySdcardPath(folder);
	}

	@Override
	public void setImageToView(ImageView imageView)
	{
		imageView.setImageResource(FileUtils.getFileIconResource(folder));
	}

	@Override
	public int getViewType()
	{
		return TYPE_SHORTCUT;
	}
	
}
