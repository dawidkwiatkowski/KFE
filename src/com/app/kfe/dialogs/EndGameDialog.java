package com.app.kfe.dialogs;

import com.app.kfe.rysowanie.Tablica;

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

    private EndGameDialogActionsHandler mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EndGameDialogActionsHandler) (activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("%s must implement EndGameDialogActionsHandler!", activity.toString()));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String sprawdz_1;
        String sprawdz_2;
        Tablica.licznik++;
        if(Tablica.gra.lista_graczy.get(0).is_drawing)
        	sprawdz_1 = "tak";
        else
        	sprawdz_1 = "nie";
        if(Tablica.gra.lista_graczy.get(1).is_drawing)
        	sprawdz_2 = "tak";
        else
        	sprawdz_2 = "nie";
        	
        builder.setTitle(showOnPosition("Gracz", 10)+showOnPosition("Punkty", 6)+showOnPosition("Rysuj¹cy", 8))
                .setMessage(Tablica.gra.lista_graczy.get(0).nazwa_gracza+"\t\t"+Tablica.gra.lista_graczy.get(0).punkty+"\t\t\t\t\t\t"+ sprawdz_1 +" \n"+Tablica.gra.lista_graczy.get(1).nazwa_gracza+"\t\t" + Tablica.gra.lista_graczy.get(1).punkty+
                		"\t\t\t\t\t\t"+sprawdz_2+ "\n\nCzy chcesz kontynuowac gre?");
        builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	
                mListener.onGameRerunAck(EndGameDialog.this);
            }
        });
        builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onGameRerunNack(EndGameDialog.this);
            }
        });
        return builder.create();
    }
    
    private String showOnPosition(String val, int numberOfPositions){
		String result = val;
		while( result.length() < numberOfPositions ){
			result += " ";
		}
		
		return result;
	}

    public interface EndGameDialogActionsHandler {
        public void onGameRerunAck(DialogFragment dialog);

        public void onGameRerunNack(DialogFragment dialog);
    }
}
