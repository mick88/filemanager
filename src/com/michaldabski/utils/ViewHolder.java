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

import android.util.SparseArray;
import android.view.View;

public class ViewHolder
{
	final View root;
	final SparseArray<View> views = new SparseArray<View>(3);
	
	public ViewHolder(View root)
	{
		this.root = root;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getViewById(int id)
	{
		T view = (T) views.get(id);
		if (view == null)
		{
			views.put(id, (view = (T) root.findViewById(id)));
			if (view == null)
				throw new NullPointerException("Cannot find requested view id "+String.valueOf(id));
		}
		return view;
	}
	
	public boolean hasView(int id)
	{
		if (views.get(id) != null) return true;
		else
		{
			View view = this.root.findViewById(id);
			if (view == null) return false;
			else
			{
				views.put(id, view);
				return true;
			}
		}
	}
}
