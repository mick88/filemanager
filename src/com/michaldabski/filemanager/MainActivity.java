package com.michaldabski.filemanager;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.michaldabski.filemanager.R;
import com.michaldabski.filemanager.clipboard.Clipboard;
import com.michaldabski.filemanager.clipboard.ClipboardFileAdapter;
import com.michaldabski.filemanager.clipboard.Clipboard.ClipboardListener;
import com.michaldabski.filemanager.favourites.FavouritesManager;
import com.michaldabski.filemanager.favourites.FavouritesManager.FavouritesListener;
import com.michaldabski.filemanager.folders.FolderFragment;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter;
import com.michaldabski.filemanager.nav_drawer.NavDrawerAdapter.NavDrawerItem;
import com.michaldabski.utils.FontApplicator;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends Activity implements OnItemClickListener, ClipboardListener, FavouritesListener
{	
	private static final String LOG_TAG = "Main Activity";

	public static final String EXTRA_DIR = FolderFragment.EXTRA_DIR;
	
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle actionBarDrawerToggle;
	File lastFolder=null;
	private FontApplicator fontApplicator;

	private SystemBarTintManager tintManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tintManager = new SystemBarTintManager(this);
		
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
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
	}
	
	public void setActionbarVisible(boolean visible)
	{
		if (visible)
		{
			getActionBar().show();
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.accent_color);
		}
		else
		{
			getActionBar().hide();
			tintManager.setStatusBarTintEnabled(false);
			tintManager.setStatusBarTintResource(android.R.color.transparent);
		}
	}
	
	private void setupDrawers()
	{
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer)
		{
			boolean actionBarShown = false;;
			
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
//		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.END);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        setupNavDrawer();
		setupClipboardDrawer();
	}
	
	void setupNavDrawer()
	{
		FileManagerApplication application = (FileManagerApplication) getApplication();
        loadFavourites(application.getFavouritesManager());
        application.getFavouritesManager().addFavouritesListener(this);
        
		// add listview header to push items below the actionbar
		LayoutInflater inflater = getLayoutInflater();
		ListView navListView = (ListView) findViewById(R.id.listNavigation);
		View header = inflater.inflate(R.layout.list_header_actionbar_padding, navListView, false);
		SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
		int headerHeight = systemBarTintManager.getConfig().getPixelInsetTop(true);
		header.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight));
		navListView.addHeaderView(header, null, false);
		int footerHeight = systemBarTintManager.getConfig().getPixelInsetBottom();
		if (footerHeight > 0)
		{
			View footer = inflater.inflate(R.layout.list_header_actionbar_padding, navListView, false);
			footer.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
			navListView.addFooterView(footer, null, false);
		}		
	}
	
	void setupClipboardDrawer()
	{
		// add listview header to push items below the actionbar
		LayoutInflater inflater = getLayoutInflater();
		ListView clipboardListView = (ListView) findViewById(R.id.listClipboard);
		View header = inflater.inflate(R.layout.list_header_actionbar_padding, clipboardListView, false);
		SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
		int headerHeight = systemBarTintManager.getConfig().getPixelInsetTop(true);
		header.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight));
		clipboardListView.addHeaderView(header, null, false);
		int footerHeight = systemBarTintManager.getConfig().getPixelInsetBottom();
		int rightPadding = systemBarTintManager.getConfig().getPixelInsetRight();
		if (footerHeight > 0)
		{
			View footer = inflater.inflate(R.layout.list_header_actionbar_padding, clipboardListView, false);
			footer.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
			clipboardListView.addFooterView(footer, null, false);
		}
		if (rightPadding > 0)
		{
			clipboardListView.setPadding(clipboardListView.getPaddingLeft(), clipboardListView.getPaddingTop(), 
					clipboardListView.getPaddingRight()+rightPadding, clipboardListView.getPaddingBottom());
		}
		onClipboardContentsChange(Clipboard.getInstance());
	}
	
	void loadFavourites(FavouritesManager favouritesManager)
	{
		ListView listNavigation = (ListView) findViewById(R.id.listNavigation);
		NavDrawerAdapter navDrawerAdapter = new NavDrawerAdapter(this, new ArrayList<NavDrawerAdapter.NavDrawerItem>(favouritesManager.getFolders()));
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	
	public FolderFragment getFolderFragment()
	{
		Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
		if (fragment instanceof FolderFragment)
			return (FolderFragment) fragment;
		else return null;
		
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
	
}
