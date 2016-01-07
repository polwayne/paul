package de.paulwein.paul.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;

public class LocationsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_screen);
		TextView title = (TextView) findViewById(R.id.tv_locations_title);
		ILookAndFeel ilaf = new LookAndFeel(this);
		title.setTypeface(ilaf.getHeadLineTypeFace());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locations_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_add_location:
			Intent i = new Intent(this,EditLocationActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
