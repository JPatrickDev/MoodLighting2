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
import android.widget.Toast;

import com.jdp30.moodlighting2.Adapters.ColorGridListAdapter;
import com.jdp30.moodlighting2.Adapters.HomeScreenAdapter;
import com.jdp30.moodlighting2.R;

/**
 * Created by jackp on 31/05/2018.
 */

public class StartFadeFragment extends Fragment {
    private ColorGridListAdapter adapter = new ColorGridListAdapter(this.getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_start_fade, container, false);

        ((GridView) v.findViewById(R.id.color_grid)).setAdapter(adapter);

        ((SeekBar) v.findViewById(R.id.moodlighting_fade_time_seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!ignoreFadeSlider)
                    updateFadeTextInput();
                else
                    ignoreFadeSlider = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((EditText) v.findViewById(R.id.moodlighting_fade_time_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ignoreFadeText)
                    updateFadeSlider();
                else
                    ignoreFadeText = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((SeekBar) v.findViewById(R.id.moodlighting_pause_time_seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!ignorePauseSlider)
                    updatePauseTextInput();
                else
                    ignorePauseSlider = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((EditText) v.findViewById(R.id.moodlighting_pause_time_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ignorePauseText)
                    updatePauseSlider();
                else
                    ignorePauseText = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });






        return v;
    }

    boolean ignoreFadeSlider = false, ignoreFadeText = false, ignorePauseSlider = false, ignorePauseText = false;

    public void updateFadeTextInput() {
        double value = getFadeSlider();
        ((EditText) getView().findViewById(R.id.moodlighting_fade_time_text)).setText(value + "");
        ignoreFadeText = true;
    }

    public void updateFadeSlider() {
        double value = getFadeText();
        ((SeekBar) getView().findViewById(R.id.moodlighting_fade_time_seek)).setProgress((int) (value * 1000));
        ignoreFadeSlider = true;
    }

    public void updatePauseTextInput() {
        double value = getPauseSlider();
        ((EditText) getView().findViewById(R.id.moodlighting_pause_time_text)).setText(value + "");
        ignorePauseText = true;
    }

    public void updatePauseSlider() {
        double value = getPauseText();
        ((SeekBar) getView().findViewById(R.id.moodlighting_pause_time_seek)).setProgress((int) (value * 1000));
        ignorePauseSlider = true;
    }

    public double getFadeSlider() {
        return ((double) ((SeekBar) getView().findViewById(R.id.moodlighting_fade_time_seek)).getProgress()) / 1000.0;
    }

    public double getFadeText() {
        try {
            return Double.parseDouble(((EditText) getView().findViewById(R.id.moodlighting_fade_time_text)).getText().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getPauseSlider() {
        return ((double) ((SeekBar) getView().findViewById(R.id.moodlighting_pause_time_seek)).getProgress()) / 1000.0;
    }

    public double getPauseText() {
        try {
            return Double.parseDouble(((EditText) getView().findViewById(R.id.moodlighting_pause_time_text)).getText().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
