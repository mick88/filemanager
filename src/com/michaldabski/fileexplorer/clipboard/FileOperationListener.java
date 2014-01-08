package com.michaldabski.fileexplorer.clipboard;


public interface FileOperationListener
{
	void onFileProcessed(String filename);
	boolean isOperationCancelled();
}
