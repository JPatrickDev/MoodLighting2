package com.jdp30.moodlighting2.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.jdp30.moodlighting2.Adapters.HomeScreenAdapter;
import com.jdp30.moodlighting2.R;

/**
 * Created by jackp on 30/05/2018.
 */

public class HomeScreenFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_mood_lighting_home, container, false);

        ((GridView) v.findViewById(R.id.home_grid)).setAdapter(new HomeScreenAdapter(getActivity()));

        return v;
    }

}
