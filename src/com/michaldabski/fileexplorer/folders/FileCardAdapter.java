package com.michaldabski.fileexplorer.folders;

import java.io.File;
import java.util.List;

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
	
	public FileCardAdapter(Context context, List<File> files)
	{
		super(context, R.layout.list_item_file_card, files);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final View view = super.getView(position, convertView, parent);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		ImageView imgFileContent = viewHolder.getViewById(R.id.imgFileContent);
		File file = getItem(position);
		
		View cardBg = viewHolder.getViewById(R.id.layoutCard); 
		if (isSelected(file)) cardBg.setBackgroundResource(R.drawable.card_selected);
		else cardBg.setBackgroundResource(R.drawable.card);
		view.setBackgroundResource(android.R.color.transparent);
		return view;
	}
	
}
