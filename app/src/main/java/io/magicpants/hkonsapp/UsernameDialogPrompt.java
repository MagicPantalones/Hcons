package io.magicpants.hkonsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.EditText;


/**
 * Temp class for setting username as user are now manually created by me.
 * Created by Erik on 07.01.2018.
 */

public class UsernameDialogPrompt extends DialogFragment {

    public static UsernameDialogPrompt newInstance(String title){
        UsernameDialogPrompt frag = new UsernameDialogPrompt();
        Bundle args = new Bundle();
        args.putString("Set new username", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity());
        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setIcon(R.drawable.ic_add)
                .setTitle("Please Provide a Username!")
                .setView(editText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((MainActivity)getActivity()).updateUsername(editText.getText().toString());
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        return (i == KeyEvent.KEYCODE_BACK);
                    }
                })
                .create();
    }
}
