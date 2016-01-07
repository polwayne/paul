package de.paulwein.paul.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.activities.ShowNotesActivity;
import de.paulwein.paul.activities.ShowTasksActivity;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;

public class MenuFragment extends Fragment implements OnClickListener{
		
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.menu_fragment, container,false);
		ILookAndFeel lof = new LookAndFeel(getActivity());
		TextView tv = (TextView) layout.findViewById(R.id.tv_menu_title);
		tv.setTypeface(lof.getOtherTypeFace());
		layout.findViewById(R.id.bt_notes).setOnClickListener(this);
		layout.findViewById(R.id.bt_tasks).setOnClickListener(this);
		return layout;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_notes:
			Intent notesIntent = new Intent(getActivity(),ShowNotesActivity.class);
			startActivity(notesIntent);
			break;
		case R.id.bt_tasks:
			Intent tasksIntent = new Intent(getActivity(),ShowTasksActivity.class); 
			startActivity(tasksIntent);
			break;
		default:
			break;
		}
		
	}
	
}
