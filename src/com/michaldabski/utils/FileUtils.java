package com.michaldabski.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.michaldabski.fileexplorer.MainActivity;
import com.michaldabski.fileexplorer.R;

public class FileUtils
{
	public final static int 
		KILOBYTE = 1024,
		MEGABYTE = KILOBYTE * 1024,
		GIGABYTE = MEGABYTE * 1024,
		MAX_BYTE_SIZE = KILOBYTE / 2,
		MAX_KILOBYTE_SIZE = MEGABYTE / 2,
		MAX_MEGABYTE_SIZE = GIGABYTE / 2;
	
	public static final String MIME_TYPE_ANY = "*/*";
	
	/**
	 * Compares files by name, where directories come always first
	 */
	public static class FileNameComparator implements Comparator<File>
	{
		protected final static int 
			FIRST = -1,
			SECOND = 1;
		@Override
		public int compare(File lhs, File rhs)
		{
			if (lhs.isDirectory() || rhs.isDirectory())
			{
				if (lhs.isDirectory() == rhs.isDirectory())
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				else if (lhs.isDirectory()) return FIRST;
				else return SECOND;
			}
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}		
	}
	
	/**
	 * Compares files by extension. 
	 * Falls back to sort by name if extensions are the same or one of the objects is a Directory
	 * @author Michal
	 *
	 */
	public static class FileExtensionComparator extends FileNameComparator
	{
		@Override
		public int compare(File lhs, File rhs)
		{
			if (lhs.isDirectory() || rhs.isDirectory())
				return super.compare(lhs, rhs);

			String ext1 = getFileExtension(lhs),
					ext2 = getFileExtension(rhs);

			if (ext1.equals(ext2))
				return super.compare(lhs, rhs);
			else
				return ext1.compareToIgnoreCase(ext2);
		}
	}
	
	public static class FileSizeComparator extends FileNameComparator
	{
		private final boolean ascending = false;
		
		@Override
		public int compare(File lhs, File rhs)
		{
			if (lhs.isDirectory() || rhs.isDirectory())
				return super.compare(lhs, rhs);

			if (lhs.length() > rhs.length())
				return ascending ? SECOND : FIRST;
			else if (lhs.length() < rhs.length())
				return ascending ? FIRST : SECOND;
			else return super.compare(lhs, rhs);
		}
	}
	
	public static String formatFileSize(File file)
	{
		return formatFileSize(file.length());		
	}
	
	public static String formatFileSize(long size)
	{
		if (size < MAX_BYTE_SIZE)
			return String.format(Locale.ENGLISH, "%d bytes", size);
		else if (size < MAX_KILOBYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f kb", (float)size / KILOBYTE);
		else if (size < MAX_MEGABYTE_SIZE)
			return String.format(Locale.ENGLISH, "%.2f mb", (float)size / MEGABYTE);
		else 
			return String.format(Locale.ENGLISH, "%.2f gb", (float)size / GIGABYTE);
	}
	
	public static String getFileExtension(File file)
	{
		return getFileExtension(file.getName());
	}
	
	/**
	 * Gets extension of the file name excluding the . character
	 */
	public static String getFileExtension(String fileName)
	{
		if (fileName.contains("."))
			return fileName.substring(fileName.lastIndexOf('.')+1);
		else 
			return "";
	}
	
	public static String getFileMimeType(File file)
	{
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
	}
	
	public static Intent createFileOpenIntent(File file)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);		
		String mimeType = getFileMimeType(file);
		intent.setDataAndType(Uri.fromFile(file), mimeType);
		return intent;
	}
	
	public static void createShortcut(Context context, File file)
	{
		final Intent shortcutIntent;
		if (file.isDirectory())
		{
			shortcutIntent = new Intent(context, MainActivity.class);
			shortcutIntent.putExtra(MainActivity.EXTRA_DIR, file.getAbsolutePath());
		}
		else 
		{
			shortcutIntent = createFileOpenIntent(file);
		}
		
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, file.getName());
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, 
				Intent.ShortcutIconResource.fromContext(context, getFileIconResource(file)));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}
	
	public static int getNumFilesInFolder(File folder)
	{
		if (folder.isDirectory() == false) return 0;
		String [] files = folder.list();
		if (files == null) return 0;
		return files.length;
	}
	
	/**
	 * Attempts to get common mime type for a collection of assorted files
	 */
	public static String getCollectiveMimeType(Collection<File> files)
	{
		String typeStart=null;
		String type=null;
		for (File file : files) if (file.isDirectory() == false)
		{
			String thisType = getFileMimeType(file);
			if (thisType == null) continue;
			if (type == null)
			{
				type = thisType;
				try
				{
					typeStart = thisType.substring(0, thisType.indexOf('/'));
				}
				catch(Exception e)
				{
					return MIME_TYPE_ANY;
				}
			}
			else if (type.equalsIgnoreCase(thisType))
				continue;
			else if (thisType.startsWith(typeStart))
				type = typeStart + "*";
			else return MIME_TYPE_ANY;
		}
		if (type == null) return MIME_TYPE_ANY;
		return type;
	}
	
	public static StringBuilder combineFileNames(Collection<File> files)
	{
		StringBuilder fileNamesStringBuilder = new StringBuilder();
		boolean first=true;
		for (File file : files)
		{
			if (first == false) fileNamesStringBuilder.append(", ");
			fileNamesStringBuilder.append(file.getName());
			first=false;
		}
		return fileNamesStringBuilder;
	}
	
	
	public static void flattenDirectory(File directory, List<File> result)
	{
		if (directory.isDirectory())
		{
			for (File file : directory.listFiles())
			{
				if (file.isDirectory())
					flattenDirectory(file, result);
				else result.add(file);
			}
		}
		else result.add(directory);
	}
	
	public static void validateCopyMoveDirectory(File file, File toFolder) throws IOException
	{
		if (toFolder.equals(file))
			throw new IOException("Folder cannot be copied to itself");
		else if (toFolder.getAbsolutePath().startsWith(file.getAbsolutePath()))
			throw new IOException("Folder cannot be copied to its child folder");
	}
	
	public static void copyFile(File src, File dst) throws IOException 
	{
		if (src.isDirectory())
			throw new IOException("Source is a directory");
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public static void deleteEmptyFolders(Collection<File> directories)
	{
		for (File file : directories) if (file.isDirectory())
		{			
			deleteFiles(Arrays.asList(file.listFiles()));
			file.delete();
		}
	}
	
	public static int deleteFiles(Collection<File> files)
	{
		int n=0;
		for (File file : files)
		{
			if (file.isDirectory())
			{
				n += deleteFiles(Arrays.asList(file.listFiles()));
			}
			if (file.delete()) n++;
		}
		return n;
	}
	
	/**
	 * gets icon for this file type
	 */
	public static int getFileIconResource(File file)
	{
		if (file.isDirectory())
		{
			if (file.equals(Environment.getExternalStorageDirectory()))
				return R.drawable.icon_sdcard;
			else if (file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)))
				return R.drawable.icon_pictures;
			else if (file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)))
				return R.drawable.icon_downloads;
			else if (file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)))
				return R.drawable.icon_movies;
			else if (file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)))
				return R.drawable.icon_music;
			else if (file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)))
				return R.drawable.icon_pictures;
			return R.drawable.icon_folder;
		}
		else
		{
			return R.drawable.icon_file;
		}
	}
	
	public static String getUserFriendlySdcardPath(File file)
	{
		String path;
		try
		{
			path = file.getCanonicalPath();
		} catch (IOException e)
		{
			path = file.getAbsolutePath();
		}
		return path
				.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
	}
}
