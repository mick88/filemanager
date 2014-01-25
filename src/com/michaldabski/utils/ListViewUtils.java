package com.michaldabski.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

import com.michaldabski.filemanager.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class ListViewUtils
{
	/**
	 * Add header to listview to compensate for translucent navbar and system bar
	 */
	public static void addListViewHeader(ListView listView, Activity activity)
	{
		addListViewHeader(listView, activity, false);
	}

	/**
	 * Add header to listview to compensate for translucent navbar and system bar
	 */
	public static void addListViewHeader(ListView listView, Activity activity, boolean ignoreRightInset)
	{
		listView.setHeaderDividersEnabled(false);
		listView.setFooterDividersEnabled(false);
		
		SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		View header = inflater.inflate(R.layout.list_header_actionbar_padding, listView, false);
		int headerHeight = systemBarTintManager.getConfig().getPixelInsetTop(true);
		header.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight));
		listView.addHeaderView(header, null, false);
		
		// add footer
		int footerHeight = systemBarTintManager.getConfig().getPixelInsetBottom();
		if (footerHeight > 0)
		{
			View footer = inflater.inflate(R.layout.list_header_actionbar_padding, listView, false);
			footer.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
			listView.addFooterView(footer, null, false);
		}
		
		if (ignoreRightInset == false)
		{
			int paddingRight = systemBarTintManager.getConfig().getPixelInsetRight();
			if (paddingRight > 0)
			{
				listView.setPadding(listView.getPaddingLeft(), listView.getPaddingTop(), 
						listView.getPaddingRight()+paddingRight, listView.getPaddingBottom());
			}
		}
	}
}
