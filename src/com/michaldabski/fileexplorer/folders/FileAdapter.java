package com.michaldabski.fileexplorer.folders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.ViewHolder;

public class FileAdapter extends ArrayAdapter<File>
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
		super(context, 0, 0, objects);
	}
	
	public FileAdapter(Context context,	File [] objects)
	{
		super(context, 0, 0, objects);
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
		View view = convertView;
		if (view == null)
		{
			view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.list_item_file, parent, false);
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
		}
		else
		{			
			tvFileDetails.setText(getContext().getString(R.string.size_s, FileUtils.formatFileSize(file)));
		}
		imgIcon.setImageResource(FileUtils.getFileIconResource(file));
		
		if (isSelected(file))
		{
/*			if (file.exists() == false)
			{
				remove(file);
				notifyDataSetChanged();
			}*/

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
