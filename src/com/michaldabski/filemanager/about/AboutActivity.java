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
package com.michaldabski.filemanager.about;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.michaldabski.filemanager.R;
import com.michaldabski.utils.FontApplicator;

public class AboutActivity extends Activity implements OnClickListener
{
	
	private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/developer?id=mick88";
	private static final String FEEDBACK_ADDRESS = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		// Show the Up button in the action bar.
		setupActionBar();
		
		TextView tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
		try
		{
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			tvAppVersion.setText(pInfo.versionName);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		new FontApplicator(getApplicationContext(), "Roboto_Light.ttf").applyFont(getWindow().getDecorView());
		
		findViewById(R.id.btnFeedback).setOnClickListener(this);
		findViewById(R.id.btnPlaystore).setOnClickListener(this);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnFeedback:
				try
				{
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
					emailIntent.setData(Uri.parse("mailto:"+FEEDBACK_ADDRESS));
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+" feedback");
					startActivity(emailIntent);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
				
			case R.id.btnPlaystore:
				try
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL));
					startActivity(intent);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			
		}
	}
	
}
