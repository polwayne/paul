package de.paulwein.paul.fragments;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.NotesListProvider;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;

public class CreateListDialogFragment extends DialogFragment implements OnEditorActionListener {

	 private EditText et_list_name;
	 private long mID;
	 private String mName;
	 
	    public CreateListDialogFragment() {
	    	super();
	    }

	    public CreateListDialogFragment(long id, String name) {
	    	super();
	    	mID = id;
	    	mName = name;
	    }
	    
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.create_list_fragment, container);
	        
	        if(!TextUtils.isEmpty(mName))
	        	et_list_name.setText(mName);
	        
	        et_list_name = (EditText) view.findViewById(R.id.et_list_name);
	        getDialog().setTitle(R.string.neue_liste_erstellen);

	        // Show soft keyboard automatically
	        et_list_name.requestFocus();
	        getDialog().getWindow().setSoftInputMode(
	                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	        et_list_name.setOnEditorActionListener(this);
	        
	        return view;
	    }
	    
	    
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (EditorInfo.IME_ACTION_DONE == actionId) {
	            // Return input text to activity
	        	String list_name = et_list_name.getText().toString();
	        	
	        	ContentValues values = new ContentValues();
	        	values.put(NotesListColumns.LIST_NAME, list_name);
	        	if(!TextUtils.isEmpty(list_name))
	        		getActivity().getContentResolver().insert(NotesListProvider.CONTENT_URI, values);
	        	//TODO show error if empty
	            this.dismiss();
	            return true;
	        }
	        return false;
	    }
	    
}
