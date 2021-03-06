package com.jdp30.moodlighting2.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;

import com.jdp30.moodlighting2.Adapters.ColorGridListAdapter;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;
import com.jdp30.moodlighting2.Views.ColorSelectorView;

/**
 * Created by jackp on 31/05/2018.
 */

public class SetColorFragment extends Fragment implements ColorSelectorView.ColorListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.moodlighting_set_color_fragment, container, false);
        ((ColorSelectorView)v.findViewById(R.id.moodlight_color_picker_view)).setListener(this);
        return v;
    }

    @Override
    public void onColorChanged(int color) {
        Util.API_setColor(color,getActivity());
    }
}
