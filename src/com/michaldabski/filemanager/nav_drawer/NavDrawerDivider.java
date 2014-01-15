package com.michaldabski.filemanager.nav_drawer;

import android.content.Context;
import android.widget.ImageView;

import com.michaldabski.filemanager.folders.FolderActivity;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter.NavDrawerItem;

public class NavDrawerDivider implements NavDrawerItem
{
	CharSequence title;
	
	public NavDrawerDivider(CharSequence title)
	{
		this.title = title;
	}

	@Override
	public CharSequence getTitle(Context context)
	{
		return title;
	}

	@Override
	public void setImageToView(ImageView imageView)
	{		
	}
	
	@Override
	public boolean onClicked(FolderActivity activity)
	{
		return false;
	}

	@Override
	public int getViewType()
	{
		return TYPE_SECTION_DIVIDER;
	}

	@Override
	public CharSequence getSubTitle(Context context)
	{
		return null;
	}
	
}
