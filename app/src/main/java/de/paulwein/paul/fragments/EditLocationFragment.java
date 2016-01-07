package de.paulwein.paul.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.paulwein.paul.R;
import de.paulwein.paul.config.ILookAndFeel;
import de.paulwein.paul.config.LookAndFeel;
import de.paulwein.paul.contentprovider.LocationsProvider;
import de.paulwein.paul.database.DatabaseTables.LocationsColumns;

public class EditLocationFragment extends Fragment{
	
	private EditText et_name;
	private Spinner spinner_mode;
	private Spinner spinner_list;
	private ArrayAdapter<String> modesAdapter;
	private ArrayAdapter<String> identifierAdapter;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.location_mask, container,false);
		initUi(layout);
		return layout;
	}
	
	private void initUi(View layout) {
		et_name = (EditText) layout.findViewById(R.id.et_name);
		TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
		ILookAndFeel ilaf = new LookAndFeel(getActivity());
		tv_title.setTypeface(ilaf.getHeadLineTypeFace());
		spinner_list = (Spinner) layout.findViewById(R.id.spinner_list);
		spinner_mode = (Spinner) layout.findViewById(R.id.spinner_mode);	
		modesAdapter = new ArrayAdapter<String>(getActivity(),R.layout.simple_dropdown_item_color,getResources().getStringArray(R.array.location_modes));
		spinner_mode.setAdapter(modesAdapter);
		WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> nws = wm.getConfiguredNetworks();
		if(nws == null){
			Toast.makeText(getActivity(), R.string.wifi_location_fehler,Toast.LENGTH_LONG).show();
			getActivity().finish();
			return;
		}
		List<String> ssids = new ArrayList<String>();
		for(WifiConfiguration wc : nws){
			ssids.add(wc.SSID);
		}
		identifierAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_dropdown_item_color,ssids);
		spinner_list.setAdapter(identifierAdapter);
	}
	
	private boolean validateInput(){
		
		boolean result = true;
		
		String name = et_name.getText().toString();
		
		if(TextUtils.isEmpty(name)){
			et_name.setError(getActivity().getString(R.string.bitte_notiz_eintragen));
			result = false;
		}
		
		return result;
	}
	 
	public boolean save(){
		
		if(!validateInput()) return false;
		
		String name = et_name.getText().toString();
		
		int id_pos = spinner_list.getSelectedItemPosition();
		String ssid = identifierAdapter.getItem(id_pos);
		
		ContentValues values = new ContentValues();
		values.put(LocationsColumns.LOCATION_NAME, name);
		values.put(LocationsColumns.LOCATION_TYPE, spinner_list.getSelectedItemPosition());
		values.put(LocationsColumns.LOCATION_IDENTIFIER, ssid);
		getActivity().getContentResolver().insert(LocationsProvider.CONTENT_URI, values);
				
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_location_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_save:
			if(save()){
				Toast.makeText(getActivity(), R.string.ort_wurde_hinzugefuegt, Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
}
