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
package com.michaldabski.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.michaldabski.filemanager.R;

public class FileUtils
{

	public static class DirectoryNotEmptyException extends IOException
	{
		private static final long serialVersionUID = 1L;

		public DirectoryNotEmptyException(File file)
		{
			super("Directory "+file.getName()+" is not empty");
		}
	}
	
	public static class FileAlreadyExistsException extends IOException
	{
		private static final long serialVersionUID = 1L;

		public FileAlreadyExistsException(File file)
		{
			super("File "+file.getName()+" already exists in destination");
		}
	}
	
	@SuppressLint("SdCardPath")
	private static final String SDCARD_DISPLAY_PATH = "/sdcard";
	private static final double FILE_APP_ICON_SCALE = 0.2;
	private static final int NUM_FOLDER_PREVIEWS = 6;
	private static final int THUMBNAIL_SIZE = 256;
	private static final int PREVIEW_WIDTH = (int) (THUMBNAIL_SIZE * 1.3),
			PREVIEW_HEIGHT = THUMBNAIL_SIZE;
	
	// user-friendly names for predefined folders
	public static final String 
		DISPLAY_NAME_ROOT = "Root",
		DISPLAY_NAME_SD_CARD = "SD Card";
	
	public final static int 
		KILOBYTE = 1024,
		MEGABYTE = KILOBYTE * 1024,
		GIGABYTE = MEGABYTE * 1024,
		MAX_BYTE_SIZE = KILOBYTE / 2,
		MAX_KILOBYTE_SIZE = MEGABYTE / 2,
		MAX_MEGABYTE_SIZE = GIGABYTE / 2;
	
	public static final String MIME_TYPE_ANY = "*/*";
	
	public static final FileFilter DEFAULT_FILE_FILTER = new FileFilter()
	{
		
		@Override
		public boolean accept(File pathname)
		{
			return pathname.isHidden() == false;
		}
	};
	
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
	
	public static String formatFileSize(Collection<File> files)
	{
		return formatFileSize(getFileSize(files));
	}
	
	public static long getFileSize(File... files)
	{
		if (files == null) return 0l;
		long size=0;
		for (File file : files)
		{
			if (file.isDirectory())
				size += getFileSize(file.listFiles());
			else size += file.length();
		}
		return size;
	}
	
	public static long getFileSize(Collection<File> files)
	{
		return getFileSize(files.toArray(new File[files.size()]));
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
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
		if (type == null) return "*/*";
		return type;
	}
	
	public static int getNumFilesInFolder(File folder)
	{
		if (folder.isDirectory() == false) return 0;
		File [] files = folder.listFiles(DEFAULT_FILE_FILTER);
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
			for (File file : directory.listFiles(DEFAULT_FILE_FILTER))
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
		else if (toFolder.equals(file.getParentFile()))
			throw new IOException("Source and target directory are the same");
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
	
	public static void deleteEmptyFolders(Collection<File> directories) throws DirectoryNotEmptyException
	{
		for (File file : directories) if (file.isDirectory())
		{			
			deleteFiles(Arrays.asList(file.listFiles()));
			file.delete();
		}
		else throw new DirectoryNotEmptyException(file);
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
	@SuppressLint("NewApi")
	public static int getFileIconResource(File file)
	{
		if (file.isDirectory())
		{
			if (file.equals(Environment.getExternalStorageDirectory()) || SDCARD_DISPLAY_PATH.equals(file.getAbsolutePath()))
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
			else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && file.equals(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)))
				return R.drawable.icon_documents;
			return R.drawable.icon_folder;
		}
		else
		{
			return R.drawable.icon_file;
		}
	}
	
	public static Bitmap createFileIcon(File file, Context context, boolean homescreen)
	{
		final Bitmap bitmap;
		final Canvas canvas;
		if (file.isDirectory())
		{
			// load Folder bitmap
			Bitmap folderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_home_folder);
			
			bitmap = Bitmap.createBitmap(folderBitmap.getWidth(), folderBitmap.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(bitmap);
			canvas.drawBitmap(folderBitmap, 0, 0, null);
		}
		else
		{
			Bitmap folderBitmap = BitmapFactory.decodeResource(context.getResources(), homescreen?R.drawable.icon_home_file:R.drawable.icon_file);
			
			bitmap = Bitmap.createBitmap(folderBitmap.getWidth(), folderBitmap.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(bitmap);
			canvas.drawBitmap(folderBitmap, 0, 0, null);
			
			Drawable appIcon = IntentUtils.getAppIconForFile(file, context);
			if (appIcon != null)
			{
				Rect bounds = canvas.getClipBounds();
				int shrinkage = (int)(bounds.width() * FILE_APP_ICON_SCALE);
				bounds.left += shrinkage;
				bounds.right -= shrinkage;
				bounds.top += shrinkage * 1.5;
				bounds.bottom -= shrinkage * 0.5;
				appIcon.setBounds(bounds);
				appIcon.draw(canvas);
			}
		}
		
		// add shortcut symbol
		if (homescreen)
			canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_home_shortcut), 0, 0, null);
		
		return  bitmap;
	}
	
	public static int countFilesIn(Collection<File> roots)
	{
		int result=0;
		for (File file : roots)
			result += countFilesIn(file);
		return result;
	}
	
	public static int countFilesIn(File root)
	{
		if (root.isDirectory() == false) return 1;
		File[] files = root.listFiles();
		if (files == null) return 0;
		
		int n = 0;
		
		for (File file : files)
		{
			if (file.isDirectory())
				n += countFilesIn(file);
			else
				n ++;
		}
		return n;
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
				.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), SDCARD_DISPLAY_PATH);
	}
	
	public static String getFolderDisplayName(File folder)
	{
		if (Environment.getExternalStorageDirectory().equals(folder))
			return DISPLAY_NAME_SD_CARD;
		else if ("/".equals(folder.getAbsolutePath()))
			return DISPLAY_NAME_ROOT;
		else return folder.getName();
	}
	
	public static void getBitmapsInFolder(File folder, List<Bitmap>previews)
	{
		File [] files = folder.listFiles(FileUtils.DEFAULT_FILE_FILTER);
		Arrays.sort(files, new Comparator<File>()
		{

			@Override
			public int compare(File lhs, File rhs)
			{
				if (lhs.lastModified() > rhs.lastModified()) return -1;
				else if (lhs.lastModified() < rhs.lastModified()) return 1;
				else return 0;
			}
			
		});
		for (File file : files) 
			if (file.isDirectory() == false)
		{
			Bitmap bitmap = getPreview(file);
			if (bitmap != null)
			{
				previews.add(bitmap);
				if (previews.size() == NUM_FOLDER_PREVIEWS)
					break;
			}
		}
	}
	
	public static Bitmap buildFolderPreview(File folder)
	{
		List<Bitmap> previews = new ArrayList<Bitmap>(NUM_FOLDER_PREVIEWS);
		getBitmapsInFolder(folder, previews);
		if (previews.isEmpty()) return null;
		
		Bitmap folderPreview = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		Rect[] destRect = BitmapUtils.layoutImagesInGrid(folderPreview, 3, 2);
		Canvas canvas = new Canvas(folderPreview);
		Paint paint = new Paint();
		Rect srcRect = new Rect();
		int i=0;
		for (Bitmap bitmap : previews)
		{
			srcRect = BitmapUtils.getBestFitRect(bitmap, destRect[i]);
			canvas.drawBitmap(bitmap, srcRect, destRect[i++], paint);
			bitmap.recycle();
		}
		return folderPreview;
	}
	
	public static Bitmap getPreview(File image) 
	{
		if (image.isDirectory()) return buildFolderPreview(image);
		String type = getFileMimeType(image);
		if (type.startsWith("video/"))
		{
			return ThumbnailUtils.createVideoThumbnail(image.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
		}
		else if (type.startsWith("image/"))
		{
			BitmapFactory.Options bounds = new BitmapFactory.Options();
		    bounds.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(image.getPath(), bounds);
		    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
		        return null;

		    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
		            : bounds.outWidth;

		    BitmapFactory.Options opts = new BitmapFactory.Options();
		    opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
		    return BitmapFactory.decodeFile(image.getPath(), opts);    
		}
		else return null;
	}
	
	public static boolean isMediaDirectory(File file)
	{
		try
		{
			String path = file.getCanonicalPath();
			for (String directory : new String[]{Environment.DIRECTORY_DCIM, 
					Environment.DIRECTORY_PICTURES})
			{
				if (path.startsWith(Environment.getExternalStoragePublicDirectory(directory)
						.getAbsolutePath()))
					return true;
			}
			return false;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
