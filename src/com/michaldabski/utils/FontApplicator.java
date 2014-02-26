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

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.michaldabski.utils.ViewTraverser.ForeachAction;

/**
 * Applies selected font to the view and all views
 * @author Michal
 *
 */
public class FontApplicator
{
	final private Typeface font;
	
	public FontApplicator(Typeface font)
	{
		this.font = font;
	}
	
	public FontApplicator(Context context, String fontName)
	{
		this(Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName));
	}
	
	public FontApplicator(AssetManager assets, String assetFontName)
	{
		this.font = Typeface.createFromAsset(assets, assetFontName);
	}
	
	/**
	 * Applies font to the view and/or its children
	 * @param root
	 * @return
	 */
	public FontApplicator applyFont(View root)
	{
		if (root == null) return this;
		new ViewTraverser(root).traverse(new ForeachAction<View>()
		{
			
			@Override
			public void onElement(View element)
			{
				if (element instanceof TextView)
				{
					((TextView) element).setTypeface(font);
				}
			}
		});
		
		return this;
	}
}
