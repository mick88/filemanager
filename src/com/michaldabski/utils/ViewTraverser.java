package com.michaldabski.utils;

import android.view.View;
import android.view.ViewGroup;

/***
 * Traverse layout tree one view at a time
 * @author Michal
 *
 */
public class ViewTraverser
{
	public interface ForeachAction<T>
	{
		void onElement(T element);
	}
	
	private final View root;
	
	public ViewTraverser(View root)
	{
		this.root = root;
	}
	
	public void traverse(ForeachAction<View> foreach)
	{
		traverse(root, foreach);
	}
	
	protected void traverse(View root, ForeachAction<View> foreach)
	{
		foreach.onElement(root);
		if (root instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) root;
			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				traverse(viewGroup.getChildAt(i), foreach);
			}
		}
	}
}
