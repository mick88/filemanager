package com.michaldabski.fileexplorer.sqlite;

import android.content.Context;

import com.michaldabski.msqlite.MSQLiteOpenHelper;

public class SQLiteHelper extends MSQLiteOpenHelper
{
	private static final String DB_NAME = "file_manager.db";
	private static final int DB_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

}
