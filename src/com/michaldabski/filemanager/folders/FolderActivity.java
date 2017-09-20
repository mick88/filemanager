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
package com.michaldabski.filemanager.folders;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.michaldabski.filemanager.FileManagerApplication;
import com.michaldabski.filemanager.R;
import com.michaldabski.filemanager.about.AboutActivity;
import com.michaldabski.filemanager.clipboard.Clipboard;
import com.michaldabski.filemanager.clipboard.Clipboard.ClipboardListener;
import com.michaldabski.filemanager.clipboard.ClipboardFileAdapter;
import com.michaldabski.filemanager.favourites.FavouritesManager;
import com.michaldabski.filemanager.favourites.FavouritesManager.FavouritesListener;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.FontApplicator;
import com.michaldabski.utils.ListViewUtils;

import java.io.File;
import java.util.ArrayList;


public class FolderActivity extends Activity implements OnItemClickListener, ClipboardListener, FavouritesListener
{	
	public static class FolderNotOpenException extends Exception
	{
		
	}

	private static final String LOG_TAG = "Main Activity";

	public static final String EXTRA_DIR = FolderFragment.EXTRA_DIR;

    NavDrawerAdapter navDrawerAdapter;
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle actionBarDrawerToggle;
	File lastFolder=null;
	private FontApplicator fontApplicator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupDrawers();
		Clipboard.getInstance().addListener(this);
		
		fontApplicator = new FontApplicator(getApplicationContext(), "Roboto_Light.ttf").applyFont(getWindow().getDecorView());
	}
	
	public FontApplicator getFontApplicator()
	{
		return fontApplicator;
	}
	
	@Override
	protected void onDestroy()
	{
		Clipboard.getInstance().removeListener(this);
		FileManagerApplication application = (FileManagerApplication) getApplication();
		application.getFavouritesManager().removeFavouritesListener(this);
		super.onDestroy();
	}
	
	public void setLastFolder(File lastFolder)
	{
		this.lastFolder = lastFolder;
	}
	
	@Override
	protected void onPause()
	{
		if (lastFolder != null)
		{
			FileManagerApplication application = (FileManagerApplication) getApplication();
			application.getAppPreferences().setStartFolder(lastFolder).saveChanges(getApplicationContext());
			Log.d(LOG_TAG, "Saved last folder "+lastFolder.toString());
		}
		super.onPause();
	}

    public void setActionbarVisible(boolean visible)
	{
        ActionBar actionBar = getActionBar();
        if (actionBar == null) return;
		if (visible)
		{
			actionBar.show();
            setSystemBarTranslucency(false);
		}
		else
		{
			actionBar.hide();
            setSystemBarTranslucency(true);
		}
	}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setSystemBarTranslucency(boolean translucent)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        if (translucent)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else
        {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
	
	private void setupDrawers()
	{
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer)
		{
			boolean actionBarShown = false;
			
			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				setActionbarVisible(true);
				invalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerClosed(View drawerView)
			{
				actionBarShown=false;
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				super.onDrawerSlide(drawerView, slideOffset);
				if (slideOffset > 0 && actionBarShown == false)
				{
					actionBarShown = true;
					setActionbarVisible(true);
				}
				else if (slideOffset <= 0) actionBarShown = false;
			}
		};
		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setFocusableInTouchMode(false);
//		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.END);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        setupNavDrawer();
		setupClipboardDrawer();
	}

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();
    }

    void setupNavDrawer()
	{
		FileManagerApplication application = (FileManagerApplication) getApplication();
        
		// add listview header to push items below the actionbar
		ListView navListView = (ListView) findViewById(R.id.listNavigation);
		ListViewUtils.addListViewPadding(navListView, this, true);
		
		loadFavourites(application.getFavouritesManager());
        application.getFavouritesManager().addFavouritesListener(this);
	}

	void setupClipboardDrawer()
	{

		// add listview header to push items below the actionbar
        ListView clipboardListView = (ListView) findViewById(R.id.listClipboard);
		ListViewUtils.addListViewHeader(clipboardListView, this);
		onClipboardContentsChange(Clipboard.getInstance());
	}


	void loadFavourites(FavouritesManager favouritesManager)
	{
		ListView listNavigation = (ListView) findViewById(R.id.listNavigation);
        navDrawerAdapter = new NavDrawerAdapter(this, new ArrayList<NavDrawerAdapter.NavDrawerItem>(favouritesManager.getFolders()));
		navDrawerAdapter.setFontApplicator(fontApplicator);
		listNavigation.setAdapter(navDrawerAdapter);
		listNavigation.setOnItemClickListener(this);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
		
		if (getFragmentManager().findFragmentById(R.id.fragment) == null)
		{
			FolderFragment folderFragment = new FolderFragment();
			if (getIntent().hasExtra(EXTRA_DIR))
			{
				Bundle args = new Bundle();
				args.putString(FolderFragment.EXTRA_DIR, getIntent().getStringExtra(EXTRA_DIR));
				folderFragment.setArguments(args);
			}
			
			getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment, folderFragment)
				.commit();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (actionBarDrawerToggle.onOptionsItemSelected(item))
			return true;
		switch (item.getItemId())
		{
			case R.id.menu_about:
				startActivity(new Intent(getApplicationContext(), AboutActivity.class));
				return true;
            /*case R.id.menu_search:
                Toast.makeText(getApplicationContext(), "ila 3yiti gol 3ad bditi", Toast.LENGTH_LONG).show();
                return true;*/
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showFragment(Fragment fragment)
	{
		getFragmentManager()
			.beginTransaction()
			.addToBackStack(null)
			.replace(R.id.fragment, fragment)
			.commit();
	}
	
	public void goBack()
	{
		getFragmentManager().popBackStack();
	}
	//***********
    //**************
    //**************

    public FolderFragment getFolderFragment()
	{
		Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
		if (fragment instanceof FolderFragment)
			return (FolderFragment) fragment;
		else return null;
		
	}
	
	public File getCurrentFolder() throws FolderNotOpenException
	{
		FolderFragment folderFragment = getFolderFragment();
		if (folderFragment == null)
			throw new FolderNotOpenException();
		else return folderFragment.currentDir;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		switch (arg0.getId())
		{
			case R.id.listNavigation:
				NavDrawerItem item = (NavDrawerItem) arg0.getItemAtPosition(arg2);
				if (item.onClicked(this))
					drawerLayout.closeDrawers();
				break;
				
			case R.id.listClipboard:
				FolderFragment folderFragment = getFolderFragment();
				if (folderFragment != null)
				{
					// TODO: paste single file
				}
				break;
			
			default:
				break;
		}
	}
	
	public File getLastFolder()
	{
		return lastFolder;
	}

	@Override
	public void onClipboardContentsChange(Clipboard clipboard)
	{
		invalidateOptionsMenu();
		
		ListView clipboardListView = (ListView) findViewById(R.id.listClipboard);
		
		if (clipboard.isEmpty() && drawerLayout != null)
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
		else 
		{
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
			FileManagerApplication application = (FileManagerApplication) getApplication();
			if (clipboardListView != null)
			{
                ClipboardFileAdapter clipboardFileAdapter = new ClipboardFileAdapter(this, clipboard, application.getFileIconResolver());
				clipboardFileAdapter.setFontApplicator(fontApplicator);
				clipboardListView.setAdapter(clipboardFileAdapter);
			}
		}
	}

	@Override
	public void onFavouritesChanged(FavouritesManager favouritesManager)
	{
		loadFavourites(favouritesManager);
	}

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        Log.d("Key Long Press", event.toString());
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        else return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Associate SearchView  with QueryTextListener
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // navDrawerAdapter filter
                navDrawerAdapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // navDrawerAdapter filter
                navDrawerAdapter.getFilter().filter(query);
                  return true;
            }
            };

        searchView.setOnQueryTextListener(textChangeListener);
        return true;
        }


}
