package com.jdp30.moodlighting2.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.jdp30.moodlighting2.Adapters.GroupsListAdapter;
import com.jdp30.moodlighting2.Adapters.PresetListAdapter;
import com.jdp30.moodlighting2.Model.FadePreset;
import com.jdp30.moodlighting2.Model.Group;
import com.jdp30.moodlighting2.Model.Preset;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;

import java.util.List;

/**
 * Created by jackp on 11/06/2018.
 */

public class PresetsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.moodlighting_presets_fragment, container, false);
        Util.PresetsCallback c = new Util.PresetsCallback() {
            @Override
            public void callback(List<Preset> preset) {
                PresetListAdapter adapter = new PresetListAdapter(getActivity());
                adapter.presets = preset;
                adapter.notifyDataSetChanged();
                ((GridView)v.findViewById(R.id.moodlighting_preset_fragment_grid_view)).setAdapter(adapter);
            }
        };
        Util.getPresets(c,this.getActivity());
       // v.findViewById(R.id.moodlighting_preset_fragment_add_preset).setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
      //          Util.addNewGroup(getActivity());
        //    }
     //   });
        return v;
    }

}