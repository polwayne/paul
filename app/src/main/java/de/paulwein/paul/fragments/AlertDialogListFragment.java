package de.paulwein.paul.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import de.paulwein.paul.R;
import de.paulwein.paul.contentprovider.NotesListProvider;
import de.paulwein.paul.database.DatabaseTables.NotesListColumns;

public class AlertDialogListFragment extends DialogFragment{
	
	private IListDialogInterface mCallback;	
	
	public interface IListDialogInterface{
		public void doPositiveClick(long selectedItem);
		public void doNegativeClick();
	}

    public static AlertDialogListFragment newInstance(int title, IListDialogInterface callback) {
        AlertDialogListFragment frag = new AlertDialogListFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        frag.mCallback = callback;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

       Cursor cursor = getActivity().getContentResolver().query(NotesListProvider.CONTENT_URI, NotesListColumns.ALL_COLUMNS, null, null, NotesListColumns.ID + " ASC");
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle(title)
                .setPositiveButton(R.string.ja,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	ListView lv = ((AlertDialog) dialog).getListView();
                        	long[] ids = lv.getCheckedItemIds();
                        	if(ids.length > 0)
                        	{
                        		long id = ids[0]; 
                        		mCallback.doPositiveClick(id);
                        	}
                        }
                    }
                )
                .setNegativeButton(R.string.nein,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	mCallback.doNegativeClick();
                        }
                    }
                ).setSingleChoiceItems(cursor, -1, NotesListColumns.LIST_NAME,null)
                .create();
    }
}