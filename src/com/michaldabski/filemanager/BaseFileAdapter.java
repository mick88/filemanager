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