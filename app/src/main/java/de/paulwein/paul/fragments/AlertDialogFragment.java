package de.paulwein.paul.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

	public interface IAlertDialog{
		void doPositiveClick();
		void doNegativeClick();
	}
	
	private IAlertDialog mCallback;
	
    public static AlertDialogFragment newInstance(int title, IAlertDialog callback) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        frag.mCallback = callback;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle(title)
                .setPositiveButton("Ja",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mCallback.doPositiveClick();
                        }
                    }
                )
                .setNegativeButton("Nein",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mCallback.doNegativeClick();
                        }
                    }
                )
                .create();
    }
}