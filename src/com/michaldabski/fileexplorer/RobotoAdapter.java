package com.michaldabski.fileexplorer;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.michaldabski.utils.FontApplicator;

public class RobotoAdapter<T> extends ArrayAdapter<T>
{
	private FontApplicator fontApplicator;
	private String fontName = "Roboto_Light.ttf";

	public RobotoAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public RobotoAdapter(Context context, int resource, int textViewResourceId,
			T[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public RobotoAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public RobotoAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	public RobotoAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	public RobotoAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	
	public RobotoAdapter<T> setFontApplicator(FontApplicator fontApplicator)
	{
		this.fontApplicator = fontApplicator;
		return this;
	}
	
	protected void applyFont(View view)
	{
		getFontApplicator().applyFont(view);
	}
	
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}
	
	public FontApplicator getFontApplicator()
	{
		if (fontApplicator == null)
			fontApplicator = new FontApplicator(getContext(), fontName);
		return fontApplicator;
	}
}
