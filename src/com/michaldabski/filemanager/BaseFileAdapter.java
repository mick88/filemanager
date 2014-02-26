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

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaldabski.utils.FileIconResolver;
import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.ViewHolder;

public class BaseFileAdapter extends RobotoAdapter<File>
{	
	protected final int layoutId; 
	final FileIconResolver fileIconResolver;
	
	public BaseFileAdapter(Context context, int resource, File[] objects, FileIconResolver fileIconResolver)
	{
		super(context, resource, objects);
		this.layoutId = resource;
		this.fileIconResolver = fileIconResolver;
	}
	
	public BaseFileAdapter(Context context, int resource, List<File> objects, FileIconResolver fileIconResolver)
	{
		super(context, resource, objects);
		this.layoutId = resource;
		this.fileIconResolver = fileIconResolver;
	}
	
	protected int getItemLayoutId(int position)
	{
		return layoutId;
	}
	
	protected View buildView(int position, ViewGroup parent)
	{
		View view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(getItemLayoutId(position), parent, false);
		view.setTag(new ViewHolder(view));
		applyFont(view);
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null) view = buildView(position, parent);
		
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		final File file = getItem(position);
		TextView 
			tvFileName = viewHolder.getViewById(R.id.tvFileName),
			tvFileDetails = viewHolder.getViewById(R.id.tvFileDetails);
		ImageView imgIcon = viewHolder.getViewById(R.id.imgFileIcon);
		
		tvFileName.setText(file.getName());
		
		if (file.isDirectory())
		{
			int files = FileUtils.getNumFilesInFolder(file); 
			if (files == 0) tvFileDetails.setText(R.string.folder_empty);
			else tvFileDetails.setText(getContext().getString(R.string.folder, files));
			imgIcon.setImageResource(FileUtils.getFileIconResource(file));
		}
		else
		{			
			tvFileDetails.setText(getContext().getString(R.string.size_s, FileUtils.formatFileSize(file)));
			imgIcon.setImageBitmap(fileIconResolver.getFileIcon(file));
		}
		
			
		return view;
	}
	
}
