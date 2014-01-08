package com.michaldabski.fileexplorer.nav_drawer;

import android.content.Context;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.MainActivity;
import com.michaldabski.fileexplorer.nav_drawer.NavDrawerAdapter.NavDrawerItem;

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
	public boolean onClicked(MainActivity activity)
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
