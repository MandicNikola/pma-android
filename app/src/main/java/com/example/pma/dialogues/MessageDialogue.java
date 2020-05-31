package com.example.pma.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pma.R;

public class MessageDialogue  extends AppCompatDialogFragment {
    private String message = "";
    private String title = "";
    public MessageDialogue(String text, String title ){
        this.message = text;
        this.title = title;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(title).setMessage(message).setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        //alertDialog.getWindow().setLayout(20,20);
        return alertDialog;
    }
}
