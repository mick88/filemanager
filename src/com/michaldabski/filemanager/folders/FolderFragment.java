package com.michaldabski.filemanager.folders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.michaldabski.filemanager.AppPreferences;
import com.michaldabski.filemanager.FileManagerApplication;
import com.michaldabski.filemanager.MainActivity;
import com.michaldabski.filemanager.R;
import com.michaldabski.filemanager.clipboard.Clipboard;
import com.michaldabski.filemanager.clipboard.FileOperationListener;
import com.michaldabski.filemanager.clipboard.Clipboard.FileAction;
import com.michaldabski.filemanager.favourites.FavouriteFolder;
import com.michaldabski.filemanager.favourites.FavouritesManager;
import com.michaldabski.filemanager.favourites.FavouritesManager.FolderAlreadyFavouriteException;
import com.michaldabski.filemanager.folders.FileAdapter.OnFileSelectedListener;
import com.michaldabski.utils.AsyncResult;
import com.michaldabski.utils.FileUtils;
import com.michaldabski.utils.FontApplicator;
import com.michaldabski.utils.IntentUtils;
import com.michaldabski.utils.OnResultListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class FolderFragment extends Fragment implements OnItemClickListener, OnScrollListener, OnItemLongClickListener, MultiChoiceModeListener, OnFileSelectedListener
{
	private static final String LOG_TAG = "FolderFragment";
	private final int DISTANCE_TO_HIDE_ACTIONBAR = 0; 
	public static final String 
		EXTRA_DIR = "directory",
		EXTRA_SELECTED_FILES = "selected_files",
		EXTRA_SCROLL_POSITION = "scroll_position";
	File currentDir,
		nextDir = null;
	int topVisibleItem=0;
	List<File> files = null;
	@SuppressWarnings("rawtypes")
	AsyncTask loadFilesTask=null;
	ListView listView = null;
	FileAdapter fileAdapter;
	private ActionMode actionMode = null;
	private final HashSet<File> selectedFiles = new HashSet<File>();
	private ShareActionProvider shareActionProvider;
	// set to true when selection shouldnt be cleared from switching out fragments
	boolean preserveSelection = false;
	
	public ListView getListView()
	{
		return listView;
	}
	
	private void setListAdapter(FileAdapter fileAdapter)
	{
		this.fileAdapter = fileAdapter;
		if (listView != null)
		{
			listView.setAdapter(fileAdapter);
			listView.setSelection(topVisibleItem);
			
			getView().findViewById(R.id.layoutMessage).setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	FontApplicator getFontApplicator()
	{
		MainActivity mainActivity = (MainActivity) getActivity();
		return mainActivity.getFontApplicator();
	}
	
	void showProgress()
	{
		if (getView() != null)
		{
			getListView().setVisibility(View.GONE);
			getView().findViewById(R.id.layoutMessage).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.tvMessage).setVisibility(View.GONE);
		}
	}
	
	FileManagerApplication getApplication()
	{
		if (getActivity() == null) return null;
		return (FileManagerApplication) getActivity().getApplication();
	}
	
	AppPreferences getPreferences()
	{
		if (getApplication() == null) return null;
		return getApplication().getAppPreferences();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		Log.d(LOG_TAG, "Fragment created");
		
		if (savedInstanceState != null)
		{
			this.topVisibleItem = savedInstanceState.getInt(EXTRA_SCROLL_POSITION, 0);
			this.selectedFiles.addAll((HashSet<File>) savedInstanceState.getSerializable(EXTRA_SELECTED_FILES));
		}
		
		Bundle arguments = getArguments();
		
		if (arguments != null && arguments.containsKey(EXTRA_DIR))
			currentDir = new File(arguments.getString(EXTRA_DIR));
		else 
			currentDir = getPreferences().getStartFolder();
		
		setHasOptionsMenu(true);
		
		loadFileList();
	}
	
	void showMessage(CharSequence message)
	{
		View view = getView();
		if (view != null)
		{
			getListView().setVisibility(View.GONE);
			view.findViewById(R.id.layoutMessage).setVisibility(View.VISIBLE);
			view.findViewById(R.id.progress).setVisibility(View.GONE);
			TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
			tvMessage.setText(message);
					
		}
	}
	
	void showMessage(int message)
	{
		showMessage(getString(message));
	}
	
	void showList()
	{
		getListView().setVisibility(View.VISIBLE);
		getView().findViewById(R.id.layoutMessage).setVisibility(View.GONE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		this.listView = (ListView) view.findViewById(android.R.id.list);
		return view;
	}
	
	void loadFileList()
	{
		if (loadFilesTask != null) return;
		this.loadFilesTask = new AsyncTask<File, Void, AsyncResult<File[]>>()
		{
			@Override
			protected AsyncResult<File[]> doInBackground(File... params)
			{
				try
				{
					File[] files =params[0].listFiles(FileUtils.DEFAULT_FILE_FILTER);
					if (files == null)
						throw new NullPointerException(getString(R.string.cannot_read_directory_s, params[0].getName()));
					if (isCancelled())
						throw new Exception("Task cancelled");
					Arrays.sort(files, getPreferences().getFileSortingComparator());
					return new AsyncResult<File[]>(files);
				}
				catch (Exception e)
				{
					return new AsyncResult<File[]>(e);
				}
			}
			
			@Override
			protected void onCancelled(AsyncResult<File[]> result) 
			{
				loadFilesTask = null;
			}
			
			@Override
			protected void onPostExecute(AsyncResult<File[]> result) 
			{
				Log.d("folder fragment", "Task finished");
				loadFilesTask = null;
				
				FileAdapter adapter;
				try
				{
					files = Arrays.asList(result.getResult());
					
					if (files.isEmpty())
					{
						showMessage(R.string.folder_empty);
						return;
					}
					adapter = new FileAdapter(getActivity(), files, getApplication().getFileIconResolver());
					adapter.setSelectedFiles(selectedFiles);
					adapter.setOnFileSelectedListener(FolderFragment.this);
					adapter.setFontApplicator(getFontApplicator());
					setListAdapter(adapter);
					
				} catch (Exception e)
				{
					// exception was thrown while loading files
					showMessage(e.getMessage());
					adapter = new FileAdapter(getActivity(), getApplication().getFileIconResolver());
				}
				
				getActivity().invalidateOptionsMenu();
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentDir);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.folder_browser, menu);
		
		menu.findItem(R.id.menu_selectAll).setVisible(!(files == null || files.isEmpty()));
		
		if (getApplication().getFavouritesManager().isFolderFavourite(currentDir))
		{
			menu.findItem(R.id.menu_unfavourite).setVisible(true);
			menu.findItem(R.id.menu_favourite).setVisible(false);
		}
		else
		{
			menu.findItem(R.id.menu_unfavourite).setVisible(false);
			menu.findItem(R.id.menu_favourite).setVisible(true);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_paste).setVisible(Clipboard.getInstance().isEmpty() == false);
		menu.findItem(R.id.menu_navigate_up).setVisible(currentDir.getParentFile() != null);
	}
	
	void showEditTextDialog(int title, int okButtonText, final OnResultListener<CharSequence> enteredTextResult, CharSequence hint, CharSequence defaultValue)
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edittext, (ViewGroup) getActivity().getWindow().getDecorView(), false);
		final EditText editText = (EditText) view.findViewById(android.R.id.edit);
		editText.setHint(hint);
		editText.setText(defaultValue);
		editText.setSelection(defaultValue.length());
		
		new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setView(view)
			.setPositiveButton(okButtonText, new OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					enteredTextResult.onResult(new AsyncResult<CharSequence>(editText.getText()));
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{				
			case R.id.menu_selectAll:
				selectFiles(this.files);
				return true;
				
			case R.id.menu_navigate_up:
				String newFolder = currentDir.getParent();
				if (newFolder != null)
				{
					Bundle args = new Bundle(1);
					args.putString(EXTRA_DIR, newFolder);
					FolderFragment fragment = new FolderFragment();
					fragment.setArguments(args);
					MainActivity activity = (MainActivity) getActivity();
					activity.showFragment(fragment);
					
				}
				return true;
				
			case R.id.menu_favourite:				
				try
				{
					final String directoryName;
					if (currentDir.equals(Environment.getExternalStorageDirectory()))
						directoryName = getActivity().getString(R.string.sd_card);
					else directoryName = currentDir.getName();
					
					FavouritesManager favouritesManager = getApplication().getFavouritesManager();
					favouritesManager.addFavourite(new FavouriteFolder(currentDir, directoryName));
					getActivity().invalidateOptionsMenu();
				} catch (FolderAlreadyFavouriteException e1)
				{
					e1.printStackTrace();
				}
				return true;
				
			case R.id.menu_unfavourite:				
				FavouritesManager favouritesManager = getApplication().getFavouritesManager();
				favouritesManager.removeFavourite(currentDir);
				getActivity().invalidateOptionsMenu();
				return true;
				
			case R.id.menu_create_folder:
				showEditTextDialog(R.string.create_folder, R.string.create, new OnResultListener<CharSequence>()
				{

					@Override
					public void onResult(AsyncResult<CharSequence> result)
					{
						try
						{
							String name = result.getResult().toString();
							File newFolder = new File(currentDir, name);
							if (newFolder.mkdirs())
							{
								refreshFolder();
								Toast.makeText(getActivity(), R.string.folder_created_successfully, Toast.LENGTH_SHORT).show();
								navigateTo(newFolder);
							}
							else Toast.makeText(getActivity(), R.string.folder_could_not_be_created, Toast.LENGTH_SHORT).show();
							
						} catch (Exception e)
						{
							e.printStackTrace();
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}						
					}
				}, "", "");
				return true;
				
			case R.id.menu_paste:
				new AsyncTask<Clipboard, Float, Exception>()
				{

					ProgressDialog progressDialog;
					
					@Override
					protected void onPreExecute()
					{
						super.onPreExecute();
						progressDialog = new ProgressDialog(getActivity());
						progressDialog.setTitle(getActivity().getString(R.string.pasting_files_));
						progressDialog.setIndeterminate(false);
						progressDialog.setCancelable(false);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.show();
					}
					
					@Override
					protected void onProgressUpdate(Float... values) 
					{
						float progress = values[0];
						progressDialog.setMax(100);
						progressDialog.setProgress((int) (progress * 100));
					}
					@Override
					protected Exception doInBackground(Clipboard... params)
					{
						try
						{
							final int total = FileUtils.countFilesIn(params[0].getFiles());
							final int[] progress = {0};
							params[0].paste(currentDir, new FileOperationListener()
							{								
								@Override
								public void onFileProcessed(String filename)
								{
									progress[0]++;
									publishProgress((float)progress[0] / (float)total);
								}
								
								@Override
								public boolean isOperationCancelled()
								{
									return isCancelled();
								}
							});
							return null;
						} catch (IOException e)
						{
							e.printStackTrace();
							return e;
						}
					}
					
					@Override
					protected void onCancelled() {
						progressDialog.dismiss();
						refreshFolder();
					};
					
					@Override
					protected void onPostExecute(Exception result) {
						progressDialog.dismiss();
						refreshFolder();
						if (result == null)
						{
							Clipboard.getInstance().clear();
							Toast.makeText(getActivity(), R.string.files_pasted, Toast.LENGTH_SHORT).show();
						}
						else
						{
							new AlertDialog.Builder(getActivity())
								.setMessage(result.getMessage())
								.setPositiveButton(android.R.string.ok, null)
								.show();
						}
					};
					
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Clipboard.getInstance());
				
				return true;
				
			case R.id.menu_refresh:
				refreshFolder();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onViewCreated(View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		getFontApplicator().applyFont(view);
		
		loadFileList();
		
		if (selectedFiles.isEmpty() == false)
		{
			selectFiles(selectedFiles);
		}
		
		final String directoryName;
		if (currentDir.equals(Environment.getExternalStorageDirectory()))
			directoryName = getActivity().getString(R.string.sd_card);
		else directoryName = currentDir.getName();
		getActivity().setTitle(directoryName);
		getListView().setOnItemClickListener(FolderFragment.this);
		getListView().setOnScrollListener(this);
		getListView().setOnItemLongClickListener(this);
		getListView().setMultiChoiceModeListener(this);
		getActivity().getActionBar().setSubtitle(FileUtils.getUserFriendlySdcardPath(currentDir));
		
		if (topVisibleItem <= DISTANCE_TO_HIDE_ACTIONBAR)
			setActionbarVisibility(true);
		
		// add listview header to push items below the actionbar
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.list_header_actionbar_padding, getListView(), false);
		SystemBarTintManager systemBarTintManager = new SystemBarTintManager(getActivity());
		int headerHeight = systemBarTintManager.getConfig().getPixelInsetTop(true);
		header.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, headerHeight));
		getListView().addHeaderView(header, null, false);
		
		// add footer
		int footerHeight = systemBarTintManager.getConfig().getPixelInsetBottom();
		if (footerHeight > 0)
		{
			View footer = inflater.inflate(R.layout.list_header_actionbar_padding, getListView(), false);
			footer.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
			getListView().addFooterView(footer, null, false);
		}
		
		int paddingRight = systemBarTintManager.getConfig().getPixelInsetRight();
		if (paddingRight > 0)
		{
			
			View layoutList = view.findViewById(R.id.layoutlist);
			layoutList.setPadding(layoutList.getPaddingLeft(), layoutList.getPaddingTop(), 
					layoutList.getPaddingRight()+paddingRight, layoutList.getPaddingBottom());
		}
		
		if (fileAdapter != null)
			setListAdapter(fileAdapter);
		
		MainActivity activity = (MainActivity) getActivity();
		activity.setLastFolder(currentDir);

	}
	
	@Override
	public void onDestroyView()
	{
		finishActionMode(true);
		listView = null;
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() 
	{
		if (loadFilesTask != null)
			loadFilesTask.cancel(true);
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_SCROLL_POSITION, topVisibleItem);
		outState.putSerializable(EXTRA_SELECTED_FILES, selectedFiles);
	}
	
	void navigateTo(File folder)
	{		
		nextDir = folder;
		MainActivity activity = (MainActivity) getActivity();
		FolderFragment fragment = new FolderFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_DIR, folder.getAbsolutePath());
		fragment.setArguments(args);
		activity.showFragment(fragment);
	}
	
	void openFile(File file)
	{
		if (file.isDirectory())
			throw new IllegalArgumentException("File cannot be a directory!");
		
		Intent intent = IntentUtils.createFileOpenIntent(file);

		try
		{
			startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			startActivity(Intent.createChooser(intent, getString(R.string.open_file_with_, file.getName())));
		}
		catch (Exception e)
		{
			new AlertDialog.Builder(getActivity())
				.setMessage(e.getMessage())
				.setTitle(R.string.error)
				.setPositiveButton(android.R.string.ok, null)
				.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3)
	{
		Object selectedObject = adapterView.getItemAtPosition(position);
		if (selectedObject instanceof File)
		{
			if (actionMode == null)
			{
				File selectedFile = (File) selectedObject;
				if (selectedFile.isDirectory())
					navigateTo(selectedFile);
				else 
					openFile(selectedFile);
			}
			else
			{
				toggleFileSelected((File) selectedObject);
			}			
		}
	}
	
	void setActionbarVisibility(boolean visible)
	{
		if (actionMode == null || visible == true) // cannot hide CAB
			((MainActivity) getActivity()).setActionbarVisible(visible);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{		
		if (firstVisibleItem < this.topVisibleItem - DISTANCE_TO_HIDE_ACTIONBAR)
		{
			setActionbarVisibility(true);
			this.topVisibleItem = firstVisibleItem;
		}
		else if (firstVisibleItem > this.topVisibleItem + DISTANCE_TO_HIDE_ACTIONBAR)
		{
			setActionbarVisibility(false);
			this.topVisibleItem = firstVisibleItem;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		setFileSelected((File) arg0.getItemAtPosition(arg2), true);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_delete:
				new AlertDialog.Builder(getActivity())
					.setMessage(getString(R.string.delete_d_items_, selectedFiles.size()))
					.setPositiveButton(R.string.delete, new OnClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							int n = FileUtils.deleteFiles(selectedFiles);
							Toast.makeText(getActivity(), getString(R.string._d_files_deleted, n), Toast.LENGTH_SHORT).show();
							refreshFolder();
							finishActionMode(false);
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				return true;
				
			case R.id.action_selectAll:
				if (isEverythingSelected()) clearFileSelection();
				else selectFiles(files);
				return true;
				
			case R.id.action_copy:
				Clipboard.getInstance().addFiles(selectedFiles, FileAction.Copy);
				Toast.makeText(getActivity(), R.string.objects_copied_to_clipboard, Toast.LENGTH_SHORT).show();
				finishActionMode(false);
				return true;
				
			case R.id.action_cut:
				Clipboard clipboard = Clipboard.getInstance();
				clipboard.addFiles(selectedFiles, FileAction.Cut);
				Toast.makeText(getActivity(), R.string.objects_cut_to_clipboard, Toast.LENGTH_SHORT).show();
				finishActionMode(false);
				return true;
				
			case R.id.action_rename:
				final File fileToRename = (File) selectedFiles.toArray()[0];
				showEditTextDialog(fileToRename.isDirectory()?R.string.rename_folder:R.string.rename_file, R.string.rename, new OnResultListener<CharSequence>()
				{
					
					@Override
					public void onResult(AsyncResult<CharSequence> result)
					{
						try
						{
							String newName = result.getResult().toString();
							if (fileToRename.renameTo(new File(fileToRename.getParentFile(), newName)))
							{
								finishActionMode(false);
								refreshFolder();
								Toast.makeText(getActivity(), R.string.file_renamed, Toast.LENGTH_SHORT).show();
							}
							else Toast.makeText(getActivity(), getActivity().getString(R.string.file_could_not_be_renamed_to_s, newName), Toast.LENGTH_SHORT).show();
						} 
						catch (Exception e)
						{
							e.printStackTrace();
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
					}
				}, fileToRename.getName(), fileToRename.getName());
				return true;
			
			case R.id.menu_add_homescreen_icon:

				for (File file : selectedFiles)
					IntentUtils.createShortcut(getActivity(), file);
				Toast.makeText(getActivity(), R.string.shortcut_created, Toast.LENGTH_SHORT).show();
				actionMode.finish();
				return true;
		}
		return false;
	}
	
	protected void refreshFolder()
	{
		showProgress();
		loadFileList();		
	}

	void updateActionMode()
	{
		if (actionMode != null)
		{
			actionMode.invalidate();
			int count = selectedFiles.size();
			actionMode.setTitle(getString(R.string._d_objects, count));
			
			actionMode.setSubtitle(FileUtils.combineFileNames(selectedFiles));
			
			if (shareActionProvider != null)
			{
				final Intent shareIntent;
				if (selectedFiles.isEmpty()) shareIntent = null;
				else if (selectedFiles.size() == 1)
				{
					File file = (File) selectedFiles.toArray()[0];
					shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType(FileUtils.getFileMimeType(file));
					shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				}
				else
				{
					ArrayList<Uri> fileUris = new ArrayList<Uri>(selectedFiles.size());
					
					for (File file : selectedFiles) if (file.isDirectory() == false)
					{
						fileUris.add(Uri.fromFile(file));
					}
					shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
					shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
					shareIntent.setType(FileUtils.getCollectiveMimeType(selectedFiles));
				}			
				
				shareActionProvider.setShareIntent(shareIntent);
			}
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		setActionbarVisibility(true);
		getActivity().getMenuInflater().inflate(R.menu.action_file, menu);
		
		MenuItem shareMenuItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();
		this.preserveSelection = false;
		return true;
	}
	
	void finishSelection()
	{
		if (listView != null)
			listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		clearFileSelection();		
	}
	
	void finishActionMode(boolean preserveSelection)
	{
		this.preserveSelection = preserveSelection;
		if (actionMode != null)
			actionMode.finish();
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		actionMode = null;
		shareActionProvider = null;
		if (preserveSelection == false)
			finishSelection();
		Log.d(LOG_TAG, "Action mode destroyed");
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		int count = selectedFiles.size();
		if (count == 1)
		{
			menu.findItem(R.id.action_rename).setVisible(true);
			menu.findItem(R.id.menu_add_homescreen_icon).setTitle(R.string.add_to_homescreen);
		}
		else
		{
			menu.findItem(R.id.action_rename).setVisible(false);
			menu.findItem(R.id.menu_add_homescreen_icon).setTitle(R.string.add_to_homescreen_multiple);
		}
		
		// show Share button if no folder was selected
		boolean allowShare = (count > 0);
		if (allowShare)
		{
			for (File file : selectedFiles) if (file.isDirectory())
			{
				allowShare = false;
				break;
			}
		}
		menu.findItem(R.id.action_share).setVisible(allowShare);
		
		return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked)
	{	
	}
	
	void toggleFileSelected(File file)
	{
		setFileSelected(file, !selectedFiles.contains(file));
	}
	
	void clearFileSelection()
	{
		if (listView != null)
			listView.clearChoices();
		selectedFiles.clear();
		updateActionMode();
		if (fileAdapter != null)
			fileAdapter.notifyDataSetChanged();
		Log.d(LOG_TAG, "Selection cleared");
	}
	
	boolean isEverythingSelected()
	{
		return selectedFiles.size() == files.size();
	}
	
	void selectFiles(Collection<File> files)
	{
		if (files == null || files.isEmpty()) return;
		
		if (actionMode == null)
		{
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			actionMode = getActivity().startActionMode(this);
		}
		
		selectedFiles.addAll(files);
		updateActionMode();
		if (fileAdapter != null)
			fileAdapter.notifyDataSetChanged();
	}
	
	void setFileSelected(File file, boolean selected)
	{
		if (actionMode == null)
		{
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			actionMode = getActivity().startActionMode(this);
		}
		
		if (selected)
			selectedFiles.add(file);
		else 
			selectedFiles.remove(file);
		updateActionMode();
		if (fileAdapter != null)
			fileAdapter.notifyDataSetChanged();
		
		if (selectedFiles.isEmpty())
			finishActionMode(false);
	}

	@Override
	public void onFileSelected(File file)
	{
		toggleFileSelected(file);		
	}
}
