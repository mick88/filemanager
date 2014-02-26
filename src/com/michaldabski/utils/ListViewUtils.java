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
