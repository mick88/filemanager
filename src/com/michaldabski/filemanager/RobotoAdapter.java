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
package com.michaldabski.filemanager;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.michaldabski.utils.FontApplicator;

import java.util.List;

public class RobotoAdapter<T> extends ArrayAdapter<T>
{
	private FontApplicator fontApplicator;
	private String fontName = "Roboto_Light.ttf";

	public RobotoAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public RobotoAdapter(Context context, int resource, int textViewResourceId,
			T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public RobotoAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public RobotoAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

	public RobotoAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
	}

	public RobotoAdapter(Context context, int resource) {
		super(context, resource);
	}
	
	public RobotoAdapter<T> setFontApplicator(FontApplicator fontApplicator)
	{
		this.fontApplicator = fontApplicator;
		return this;
	}
	
	protected void applyFont(View view)
	{
		getFontApplicator().applyFont(view);
	}
	
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}
	
	public FontApplicator getFontApplicator()
	{
		if (fontApplicator == null)
			fontApplicator = new FontApplicator(getContext(), fontName);
		return fontApplicator;
	}
}
