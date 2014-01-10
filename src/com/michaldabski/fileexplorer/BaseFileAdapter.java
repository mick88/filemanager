package com.michaldabski.fileexplorer;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.IntentUtils;
import com.michaldabski.utils.ViewHolder;

public class BaseFileAdapter extends ArrayAdapter<File>
{	
	protected final int layoutId; 
	
	public BaseFileAdapter(Context context, int resource, File[] objects)
	{
		super(context, resource, objects);
		this.layoutId = resource;
	}
	
	public BaseFileAdapter(Context context, int resource, List<File> objects)
	{
		super(context, resource, objects);
		this.layoutId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null)
		{
			view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(layoutId, parent, false);
			view.setTag(new ViewHolder(view));
		}
		
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
			imgIcon.setImageDrawable(IntentUtils.getAppIconForFile(file, getContext()));
		}
		
			
		return view;
	}
	
}