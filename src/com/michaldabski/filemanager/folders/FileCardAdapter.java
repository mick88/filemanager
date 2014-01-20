package com.michaldabski.filemanager.folders;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.michaldabski.filemanager.R;
import com.michaldabski.utils.FileIconResolver;
import com.michaldabski.utils.FilePreviewCache;
import com.michaldabski.utils.ViewHolder;

public class FileCardAdapter extends FileAdapter
{
	private static final int MAX_PREFETCH_JOBS = 2;
	private final FilePreviewCache thumbCache;
	Map<ImageView, CardPreviewer> runningTasks = new HashMap<ImageView, CardPreviewer>();
	Map<File, CardPreviewer> prefetchTasks = new HashMap<File, CardPreviewer>();
	
	public FileCardAdapter(Context context, File[] files, FilePreviewCache previewCache, FileIconResolver fileIconResolver)
	{
		super(context, R.layout.list_item_file_card, files, fileIconResolver);
		this.thumbCache = previewCache;
	}
	
	public FileCardAdapter(Context context, List<File> files, FilePreviewCache previewCache, FileIconResolver fileIconResolver)
	{
		super(context, R.layout.list_item_file_card, files, fileIconResolver);
		this.thumbCache = previewCache;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final View view = super.getView(position, convertView, parent);
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		ImageView imgFileContent = viewHolder.getViewById(R.id.imgFileContent);
		if (runningTasks.containsKey(imgFileContent))
		{
			runningTasks.get(imgFileContent).setImageView(null);
			runningTasks.remove(imgFileContent);
		}
		File file = getItem(position);
		if (thumbCache.get(file) != null)
			imgFileContent.setImageBitmap(thumbCache.get(file));
		else if (prefetchTasks.containsKey(file))
		{
			CardPreviewer cardPreviewer = prefetchTasks.get(file);
			cardPreviewer.setImageView(imgFileContent);
			runningTasks.put(imgFileContent, cardPreviewer);
			prefetchTasks.remove(cardPreviewer);
		}
		else
		{
			CardPreviewer previewer = (CardPreviewer) new CardPreviewer(imgFileContent, thumbCache).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
			runningTasks.put(imgFileContent, previewer);
		}
		
		View cardBg = viewHolder.getViewById(R.id.layoutCard); 
		if (isSelected(file)) cardBg.setBackgroundResource(R.drawable.selector_card_selected);
		else cardBg.setBackgroundResource(R.drawable.selector_card);
		view.setBackgroundResource(R.color.color_window_background);
		return view;
	}
	
	public void prefetchImage(File file)
	{
		if (prefetchTasks.containsKey(file) == false && thumbCache.get(file) == null && prefetchTasks.size() < MAX_PREFETCH_JOBS)
		{
			prefetchTasks.put(file, (CardPreviewer) new CardPreviewer(null, thumbCache).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file));
		}
	}

	public void prefetchImages(int startPrefetch, int numItems)
	{
		if (startPrefetch >= getCount()) return;
		if (startPrefetch+numItems >= getCount()) numItems = getCount() - startPrefetch - 1;
		for (int i=startPrefetch; i < startPrefetch+numItems; i++)
		{
			prefetchImage(getItem(i));
		}
	}
}
