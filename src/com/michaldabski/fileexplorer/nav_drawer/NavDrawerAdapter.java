package com.michaldabski.fileexplorer.nav_drawer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.michaldabski.fileexplorer.MainActivity;
import com.michaldabski.fileexplorer.R;
import com.michaldabski.fileexplorer.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.ViewHolder;

public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem>
{
	private static final int TYPE_COUNT = 2;
	
	public static interface NavDrawerItem
	{
		public static final int 
			TYPE_SHORTCUT = 0,
			TYPE_SECTION_DIVIDER = 1;

		CharSequence getTitle(Context context);
		CharSequence getSubTitle(Context context);
		void setImageToView(ImageView imageView);
		boolean onClicked(MainActivity activity);
		int getViewType();
	}
	
	@Override
	public int getViewTypeCount()
	{
		return TYPE_COUNT;
	}
	
	public int getItemViewType(int position) 
	{
		return getItem(position).getViewType();
	}
	
	public NavDrawerAdapter(Context context, List<NavDrawerItem> objects)
	{
		super(context, 0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder viewHolder;
		View  view = convertView;
		if (view == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (getItemViewType(position))
			{	
				case NavDrawerItem.TYPE_SECTION_DIVIDER:
					view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
					break;
				case NavDrawerItem.TYPE_SHORTCUT:
					view = layoutInflater.inflate(R.layout.list_item_file, parent, false);
					break;
				default:
					throw new RuntimeException("Nav drawer item does not conform to available view types");
			}
			view.setTag(viewHolder = new ViewHolder(view));
		}
		else viewHolder = (ViewHolder) view.getTag();
		
		NavDrawerItem item = getItem(position);
		
		switch (getItemViewType(position))
		{
			case NavDrawerItem.TYPE_SHORTCUT:
				TextView tvName = viewHolder.getViewById(R.id.tvFileName),
					tvSubtitle = viewHolder.getViewById(R.id.tvFileDetails);
				ImageView imgIcon = viewHolder.getViewById(R.id.imgFileIcon);
				
				tvName.setText(item.getTitle(getContext()));
				tvSubtitle.setText(item.getSubTitle(getContext()));
				item.setImageToView(imgIcon);
				break;
				
			case NavDrawerItem.TYPE_SECTION_DIVIDER:
				TextView tvTitle = viewHolder.getViewById(android.R.id.text1);
				tvTitle.setText(item.getTitle(getContext()));
				break;
		}		
		
		return view;
	}
}
