package com.michaldabski.filemanager.clipboard;


public interface FileOperationListener
{
	void onFileProcessed(String filename);
	boolean isOperationCancelled();
}
