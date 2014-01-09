package com.michaldabski.fileexplorer.clipboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.michaldabski.fileexplorer.BaseFileAdapter;
import com.michaldabski.fileexplorer.R;

public class ClipboardFileAdapter extends BaseFileAdapter
{
	final Clipboard clipboard;
	public ClipboardFileAdapter(Context context, Clipboard clipboard)
	{
		super(context, R.layout.list_item_file, clipboard.getFilesList());
		this.clipboard = clipboard;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TOOD: implement paste icon
		return super.getView(position, convertView, parent);
	}
	
}
