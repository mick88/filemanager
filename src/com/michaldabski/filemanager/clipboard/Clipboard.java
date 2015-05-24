/*******************************************************************************
 * Copyright (c) 2014 Michal Dabski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.michaldabski.filemanager.clipboard;

import android.util.Log;

import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.FileUtils.DirectoryNotEmptyException;

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
			try
			{
				if (files.get(file) == FileAction.Cut)
					FileUtils.deleteEmptyFolders(Arrays.asList(file));
			}
			catch (DirectoryNotEmptyException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			File newFile = new File(destinationDir, file.getName());
			if (newFile.exists())
				throw new FileUtils.FileAlreadyExistsException(newFile);
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
