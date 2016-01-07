package de.paulwein.paul.activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.NotesProvider;
import de.paulwein.paul.database.DatabaseTables.NotesColumns;

public class SearchableActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list);
		
		ILookAndFeel lof = new LookAndFeel(this);
    	TextView tv_headline = (TextView) findViewById(R.id.tv_headline);
    	tv_headline.setTypeface(lof.getHeadLineTypeFace());
		
		Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
	}

	private void doMySearch(String query) {
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(NotesProvider.CONTENT_URI,
				NotesColumns.ALL_COLUMNS,
				NotesColumns.NOTE_SUBJECT + " like '%" +query + "%' OR " + 
				NotesColumns.NOTE_TEXT + " like '%" +query + "%'", null, null);
		String[] uiBindFrom = { NotesColumns.NOTE_SUBJECT };
		int[] uiBindTo = { R.id.textView1 };
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.note_list_item, c, uiBindFrom, uiBindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(sca);
	}
	
	@Override
	public void onListItemClick(ListView lv,View v,int position,long id){
		super.onListItemClick(lv, v, position, id);
		Intent noteIntent = new Intent(this,NoteActivity.class);
		noteIntent.putExtra(Config.EXTRA_NOTE_ID, id);
		startActivity(noteIntent);
	}

}
