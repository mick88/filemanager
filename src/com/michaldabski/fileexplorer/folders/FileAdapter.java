package com.michaldabski.fileexplorer.folders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.BaseFileAdapter;
import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.ViewHolder;


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
	
	public FileAdapter(Context context,	List<File> objects)
	{
		super(context, R.layout.list_item_file, objects);
	}
	
	public FileAdapter(Context context,	File [] objects)
	{
		super(context, R.layout.list_item_file, objects);
	}
	
	public FileAdapter(Context context)
	{
		this(context, new ArrayList<File>(0));
	}
	
	boolean isSelected(File file)
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
			view.setBackgroundResource(R.drawable.list_item_selected);
			imgIcon.setImageResource(R.drawable.icon_selected);
		}
		else
		{
			view.setBackgroundResource(R.drawable.list_item);
		}
		
		if (onFileSelectedListener != null)
		{
			imgIcon.setBackgroundResource(R.drawable.list_item);
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
