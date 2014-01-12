package com.michaldabski.fileexplorer.clipboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.michaldabski.fileexplorer.BaseFileAdapter;
import com.michaldabski.fileexplorer.R;
import com.michaldabski.utils.FileIconResolver;

public class ClipboardFileAdapter extends BaseFileAdapter
{
	final Clipboard clipboard;
	public ClipboardFileAdapter(Context context, Clipboard clipboard, FileIconResolver fileIconResolver)
	{
		super(context, R.layout.list_item_file, clipboard.getFilesList(), fileIconResolver);
		this.clipboard = clipboard;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TOOD: implement paste icon
		return super.getView(position, convertView, parent);
	}
	
}
