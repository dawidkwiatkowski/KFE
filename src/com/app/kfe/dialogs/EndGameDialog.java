package com.app.kfe.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by tobikster on 2015-01-14.
 */
public class EndGameDialog extends DialogFragment {

    private EndGameDialogActionsHanler mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EndGameDialogActionsHanler) (activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("%s must implement EndGameDialogActionsHandler!", activity.toString()));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Game over")
                .setMessage("New turn?");
        builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onGameRerunAck(EndGameDialog.this);
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onGameRerunNack(EndGameDialog.this);
            }
        });
        return builder.create();
    }

    public interface EndGameDialogActionsHanler {
        public void onGameRerunAck(DialogFragment dialog);

        public void onGameRerunNack(DialogFragment dialog);
    }
}
