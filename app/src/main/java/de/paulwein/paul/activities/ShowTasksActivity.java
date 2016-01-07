package de.paulwein.paul.activities;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.NotesListProvider;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;
import de.paulwein.paul.fragments.AlertDialogListFragment;
import de.paulwein.paul.fragments.AlertDialogListFragment.IListDialogInterface;
import de.paulwein.paul.fragments.CreateListDialogFragment;
import de.paulwein.paul.fragments.TasksListFragment;

public class ShowTasksActivity extends FragmentActivity implements OnNavigationListener,IListDialogInterface{
	
	private TasksListFragmentAdapter mAdapter;
	private SimpleCursorAdapter mNavigationAdapter;
	private ViewPager mPager;
	private int mNum = 0;
	private long[] mCategorieIDs;
	private String[] mCategorieNames;
	private Cursor mCursor;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_list_screen);
		ILookAndFeel lof = new LookAndFeel(this);
    	TextView tv_title = (TextView) findViewById(R.id.tv_tasks_title);
    	tv_title.setTypeface(lof.getHeadLineTypeFace());
    	mCursor = getContentResolver().query(NotesListProvider.CONTENT_URI, NotesListColumns.ALL_COLUMNS, null, null, NotesListColumns.ID + " ASC");
    	// TODO Where EntriesCount > 0
    	initViewPager();
    	
    	initNavigationBar();
    	
	}
	
	
	private void initViewPager(){
    	mCategorieIDs = new long[mCursor.getCount()];
    	mCategorieNames = new String[ mCategorieIDs.length];
    	while(mCursor.moveToNext()){
    		int id_index = mCursor.getColumnIndex(NotesListColumns.ID);
    		int name_index = mCursor.getColumnIndex(NotesListColumns.LIST_NAME);
    		mCategorieIDs[mNum] = mCursor.getLong(id_index);
    		mCategorieNames[mNum++] = mCursor.getString(name_index);
    	}    	
    	
    	mAdapter = new TasksListFragmentAdapter(getSupportFragmentManager());
    	
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
	}
	
	
	private void initNavigationBar(){
		 ActionBar actionBar = getActionBar();
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	        
	     String[] from = new String[]{NotesListColumns.LIST_NAME};
	     int[] to = new int[]{android.R.id.text1};
	     
	     mNavigationAdapter =
	    		 new SimpleCursorAdapter(
	    		            this, android.R.layout.simple_spinner_item,
	    		            mCursor, from, to,
	    		            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	     mNavigationAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
	     actionBar.setListNavigationCallbacks(mNavigationAdapter, this);
	     actionBar.setDisplayShowTitleEnabled(false);
	}
	
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	   inflater.inflate(R.menu.notes_list_menu, menu);
	   return true;
   }
	
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
       case R.id.action_add_note:
    	  Intent taskIntent = new Intent(this,EditTaskActivity.class);
    	  startActivity(taskIntent);
          return true;
       case R.id.action_edit:
    	   startActionMode(mActionModeCallback);
    	   return true;
       case R.id.action_export:
	       // TODO export tasks
	       	return true;
       }
       
       return false;
   }
	
   
   public class TasksListFragmentAdapter extends FragmentPagerAdapter {
       public TasksListFragmentAdapter(android.support.v4.app.FragmentManager fm) {
           super(fm);
       }

       @Override
       public int getCount() {
           return mNum;
       }

       @Override
       public android.support.v4.app.Fragment getItem(int position) {
    	   return TasksListFragment.newInstance(mCategorieIDs[position]);
       }
       
       @Override
       public CharSequence getPageTitle(int position) {
    	   if(mCategorieNames != null)
    		   return mCategorieNames[position];
    	   else
    		   return "Keine Einträge";
       }
   }


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		mPager.setCurrentItem(itemPosition);
		return true;
	}

	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			// Assumes that you have "contexual.xml" menu resources
			inflater.inflate(R.menu.notes_list_action_menu, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_new_list:
		    	   FragmentManager fm = getFragmentManager();
		           CreateListDialogFragment createListDialog = new CreateListDialogFragment();
		           createListDialog.show(fm, "fragment_create_list");
		           mode.finish();
				return true;
			case R.id.action_edit_list:
				 DialogFragment newFragment = AlertDialogListFragment.newInstance(
				            R.string.list_loeschen_dialog_text,ShowTasksActivity.this);
				    newFragment.show(getFragmentManager(), "rename_dialog");
				mode.finish();
				return true;
			case R.id.action_delete_list:
				DialogFragment deleteFragment = AlertDialogListFragment.newInstance(
			            R.string.list_loeschen_dialog_text,ShowTasksActivity.this);
				 deleteFragment.show(getFragmentManager(), "loeschen_dialog");
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		public void onDestroyActionMode(ActionMode mode) {}
	};
	
	@Override
	public void onDestroy(){
		if(mCursor != null)
			mCursor.close();
		super.onDestroy();
	}


	@Override
	public void doPositiveClick(long selectedItem) {
		getContentResolver().delete(NotesProvider.CONTENT_URI,NotesColumns.LIST_ID + "=?", new String[]{String.valueOf(selectedItem)});
		getContentResolver().delete(ContentUris.withAppendedId(NotesListProvider.CONTENT_URI,selectedItem),null, null);
		mNavigationAdapter.notifyDataSetChanged();
	} 


	@Override
	public void doNegativeClick() {
		// TODO Auto-generated method stub
		
	}
}
