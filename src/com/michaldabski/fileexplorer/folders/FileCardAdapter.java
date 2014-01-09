package com.michaldabski.fileexplorer.folders;

import java.io.File;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.ViewHolder;

public class FileCardAdapter extends FileAdapter
{
	public FileCardAdapter(Context context, File[] files)
	{
		super(context, R.layout.list_item_file_card, files);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final View view = super.getView(position, convertView, parent);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		ImageView imgFileContent = viewHolder.getViewById(R.id.imgFileContent);
		// TOOD: implement preview
		return view;
	}
	
}
