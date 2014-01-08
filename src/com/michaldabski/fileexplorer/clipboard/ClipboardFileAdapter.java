package com.michaldabski.fileexplorer.clipboard;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.ViewHolder;

public class ClipboardFileAdapter extends ArrayAdapter<File>
{

	public ClipboardFileAdapter(Context context, Clipboard clipboard)
	{
		super(context, 0, 0, clipboard.getFilesList());
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
		TextView 
		tvFileName = viewHolder.getViewById(R.id.tvFileName),
		tvFileDetails = viewHolder.getViewById(R.id.tvFileDetails);
		ImageView imgIcon = viewHolder.getViewById(R.id.imgFileIcon);
		
		final File file = getItem(position);
		
		tvFileName.setText(file.getName());
		
		if (file.isDirectory())
		{
			int files = FileUtils.getNumFilesInFolder(file); 
			if (files == 0) tvFileDetails.setText(R.string.folder_empty);
			else tvFileDetails.setText(getContext().getString(R.string.folder, files));
			imgIcon.setImageResource(R.drawable.icon_folder);
		}
		else
		{
			imgIcon.setImageResource(R.drawable.icon_file);
			tvFileDetails.setText(getContext().getString(R.string.size_s, FileUtils.formatFileSize(file)));
		}
		
		return view;
	}
	
}
