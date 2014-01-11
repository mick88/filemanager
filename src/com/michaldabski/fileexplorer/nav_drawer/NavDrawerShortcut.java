package com.michaldabski.fileexplorer.nav_drawer;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.MainActivity;
import com.michaldabski.fileexplorer.folders.FolderFragment;
import com.michaldabski.fileexplorer.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.FileUtils;

public abstract class NavDrawerShortcut implements NavDrawerItem
{
	public abstract File getFile();
	
	@Override
	public boolean onClicked(MainActivity activity)
	{
		if (getFile().equals(activity.getLastFolder())) return true;
		Bundle args = new Bundle();
		args.putString(FolderFragment.EXTRA_DIR, getFile().getAbsolutePath());
		FolderFragment folderFragment = new FolderFragment();
		folderFragment.setArguments(args);
		activity.showFragment(folderFragment);
		return true;
	}
	
	@Override
	public CharSequence getSubTitle(Context context)
	{
		return FileUtils.getUserFriendlySdcardPath(getFile());
	}

	@Override
	public void setImageToView(ImageView imageView)
	{
		imageView.setImageResource(FileUtils.getFileIconResource(getFile()));
	}

	@Override
	public int getViewType()
	{
		return TYPE_SHORTCUT;
	}
	
}
