package com.app.kfe.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.kfe.R;

/**
 * Created by tobikster on 2015-01-12.
 */
public class EndGameFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.end_game_fragment, container, false);
    }
}