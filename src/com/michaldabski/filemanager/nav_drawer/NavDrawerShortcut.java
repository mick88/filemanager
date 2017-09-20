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
package com.michaldabski.filemanager.nav_drawer;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.michaldabski.filemanager.folders.FolderActivity;
import com.michaldabski.filemanager.folders.FolderFragment;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.FileUtils;

import java.io.File;

public abstract class NavDrawerShortcut implements NavDrawerItem
{
	public abstract File getFile();
	
	@Override
	public boolean onClicked(FolderActivity activity)
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
