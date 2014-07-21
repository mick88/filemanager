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
package com.michaldabski.filemanager.folders;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.michaldabski.filemanager.BaseFileAdapter;
import com.michaldabski.filemanager.R;
import com.michaldabski.utils.FileIconResolver;
import com.michaldabski.utils.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FileAdapter extends BaseFileAdapter
{
	Set<File> selectedFiles=null;
	OnFileSelectedListener onFileSelectedListener=null;
	
	public static interface OnFileSelectedListener
	{
		void onFileSelected(File file);
	}
	
	public void setOnFileSelectedListener(
			OnFileSelectedListener onFileSelectedListener)
	{
		this.onFileSelectedListener = onFileSelectedListener;
	}
	
	public FileAdapter(Context context, int layoutId, File[] files, FileIconResolver fileIconResolver)
	{
		super(context, layoutId, files, fileIconResolver);
	}
	
	public FileAdapter(Context context, int layoutId, List<File> files, FileIconResolver fileIconResolver)
	{
		super(context, layoutId, files, fileIconResolver);
	}
	
	public FileAdapter(Context context,	List<File> objects, FileIconResolver fileIconResolver)
	{
		super(context, R.layout.list_item_file, objects, fileIconResolver);
	}
	
	public FileAdapter(Context context,	File [] objects, FileIconResolver fileIconResolver)
	{
		super(context, R.layout.list_item_file, objects, fileIconResolver);
	}
	
	public FileAdapter(Context context, FileIconResolver fileIconResolver)
	{
		this(context, new ArrayList<File>(0), fileIconResolver);
	}
	
	protected boolean isSelected(File file)
	{
		return (selectedFiles != null && selectedFiles.contains(file));
	}
	
	public void setSelectedFiles(Set<File> selectedFiles)
	{
		this.selectedFiles = selectedFiles;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final View view = super.getView(position, convertView, parent);
		final File file = getItem(position);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		final ImageView imgIcon = viewHolder.getViewById(R.id.imgFileIcon);
		
		if (isSelected(file))
		{
			view.setBackgroundResource(R.drawable.selector_list_item_selected);
			imgIcon.setImageResource(R.drawable.icon_selected);
		}
		else
		{
			view.setBackgroundResource(R.drawable.selector_list_item);
		}
		
		if (onFileSelectedListener != null)
		{
			imgIcon.setBackgroundResource(R.drawable.selector_list_item);
			imgIcon.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					onFileSelectedListener.onFileSelected(file);					
				}
			});
		}
		
		return view;
	}
	
}
