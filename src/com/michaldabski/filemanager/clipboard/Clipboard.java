package com.michaldabski.filemanager.clipboard;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.util.Log;

import com.michaldabski.utils.FileUtils;

public class Clipboard
{
	private static final String LOG_TAG = "Clipboard";

	public static interface ClipboardListener 
	{
		void onClipboardContentsChange(Clipboard clipboard);
	}
	
	public static enum FileAction
	{
		None,
		Copy,
		Cut,
	}
	
	private static Clipboard instance = null;
	private final Map<File, FileAction> files;
	final Set<ClipboardListener> clipboardListeners;
	
	private Clipboard()
	{
		this.files = new HashMap<File, Clipboard.FileAction>();
		this.clipboardListeners = new HashSet<ClipboardListener>(1);
	}
	
	public static Clipboard getInstance()
	{
		if (instance == null)
			instance = new Clipboard();
		
		return instance;
	}
	
	public void addFile(File file, FileAction action)
	{
		files.put(file, action);
		
		for (ClipboardListener listener : clipboardListeners)
			listener.onClipboardContentsChange(this);
	}
	
	public void addFiles(Collection<File> files, FileAction fileAction)
	{
		clear();
		for (File file : files)
			this.files.put(file, fileAction);
		
		for (ClipboardListener listener : clipboardListeners)
			listener.onClipboardContentsChange(this);
	}
	
	/**
	 * Recursively move/copy files
	 */
	private void pasteFile(File file, File destinationDir, FileAction fileAction, FileOperationListener fileOperationListener) throws IOException
	{
		if (fileOperationListener.isOperationCancelled())
			return;

		FileUtils.validateCopyMoveDirectory(file, destinationDir);
		destinationDir.mkdirs();
		
		if (file.isDirectory())
		{
			destinationDir = new File(destinationDir, file.getName());
			for (File f : file.listFiles())
				pasteFile(f, destinationDir, fileAction, fileOperationListener);
			FileUtils.deleteEmptyFolders(Arrays.asList(file));
		}
		else
		{
			File newFile = new File(destinationDir, file.getName());
			if (fileAction == FileAction.Cut)
				file.renameTo(newFile);
			else if (fileAction == FileAction.Copy)
				FileUtils.copyFile(file, newFile);
			else 
				throw new RuntimeException("Unsupported operation "+files.get(file));
			fileOperationListener.onFileProcessed(newFile.getName());
			Log.d(LOG_TAG, file.getName()+" pasted to "+newFile.getAbsolutePath());
		}
	}
	
	public void paste(File destination, FileOperationListener operationListener) throws IOException
	{
		destination.mkdirs();
		if (destination.isDirectory() == false)
			throw new RuntimeException(destination.getAbsolutePath()+" is anot a directory");

		for (Entry<File, FileAction> entry : files.entrySet())
		{
			pasteFile(entry.getKey(), destination, entry.getValue(), operationListener);
		}
	}
	
	public void pasteSingleFile(File file, File destinaton, FileOperationListener operationListener) throws IOException
	{
		if (files.containsKey(file) == false)
			throw new InvalidParameterException("File is not in clipboard");
		
		pasteFile(file, destinaton, files.get(file), operationListener);
		
/*		this.files.remove(file);
		for (ClipboardListener listener : clipboardListeners)
			listener.onClipboardContentsChange(this);*/
	}
	
	public Set<File> getFiles()
	{
		return files.keySet();
	}
	
	public List<File> getFilesList()
	{
		return new ArrayList<File>(files.keySet());
	}
	
	public boolean hasFile(File file)
	{
		return files.containsKey(file);
	}
	
	public void clear()
	{
		files.clear();
		for (ClipboardListener listener : clipboardListeners)
			listener.onClipboardContentsChange(this);
	}
	
	public void addListener(ClipboardListener listener)
	{
		clipboardListeners.add(listener);
	}
	
	public void removeListener(ClipboardListener listener)
	{
		clipboardListeners.remove(listener);
	}

	public boolean isEmpty()
	{
		return files.isEmpty();
	}
}
